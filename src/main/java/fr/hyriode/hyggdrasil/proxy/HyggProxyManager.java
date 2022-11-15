package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.event.HyggEventBus;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyStartedEvent;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyStoppedEvent;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyUpdatedEvent;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseCallback;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggProxyInfoPacket;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggStopProxyPacket;
import fr.hyriode.hyggdrasil.api.server.HyggServersRequester;
import fr.hyriode.hyggdrasil.config.nested.ProxiesConfig;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerSwarm;
import fr.hyriode.hyggdrasil.template.HyggTemplate;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyManager {

    public static final DockerImage PROXY_IMAGE = new DockerImage("hygg-proxy", "latest");

    private final int maxProxies;
    private final int startingPort;

    private final Map<String, HyggProxy> proxies;

    private final HyggTemplate proxyTemplate;

    private final DockerSwarm swarm;
    private final HyggPacketProcessor packetProcessor;
    private final HyggEventBus eventBus;

    private final Hyggdrasil hyggdrasil;

    public HyggProxyManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.swarm = this.hyggdrasil.getDocker().getSwarm();
        this.packetProcessor = this.hyggdrasil.getAPI().getPacketProcessor();
        this.eventBus = this.hyggdrasil.getAPI().getEventBus();
        this.proxies = new HashMap<>();

        final ProxiesConfig config = Hyggdrasil.getConfig().getProxies();

        this.proxyTemplate = this.hyggdrasil.getTemplateManager().getTemplate(config.getTemplate());
        this.maxProxies = config.getMaxProxies();
        this.startingPort = config.getStartingPort();
        this.hyggdrasil.getDocker().getImageManager().buildImage(Paths.get(References.PROXY_IMAGES_FOLDER.toString(), "Dockerfile").toFile(), PROXY_IMAGE.getName());

        for (HyggProxy proxy : this.hyggdrasil.getAPI().getProxiesRequester().fetchProxies()) {
            this.proxies.put(proxy.getName(), proxy);
        }
    }

    public HyggProxy startProxy(HyggData data) {
        if (this.proxies.size() >= this.maxProxies) {
            System.err.println("Cannot start a new proxy! Proxies limit has already been reached (" + this.proxies.size() + "/" + this.maxProxies + ")!");
            return null;
        }

        final int availablePort = this.getAvailablePort();

        if (availablePort != -1) {
            final HyggProxy proxy = new HyggProxy(data);
            final String proxyName = proxy.getName();
            final Path proxyFolder = Paths.get(References.PROXIES_FOLDER.toString(), proxyName);

            if (!IOUtil.createDirectory(proxyFolder)) {
                return null;
            }

            for (Path plugin : this.hyggdrasil.getTemplateManager().getDownloader().getPluginsFiles(this.proxyTemplate)) {
                IOUtil.copy(plugin, Paths.get(proxyFolder.toString(), plugin.getFileName().toString()));
            }

            proxy.setPort(availablePort);

            this.swarm.runService(new HyggProxyService(proxy));
            this.proxies.put(proxyName, proxy);
            this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.set(HyggServersRequester.REDIS_KEY + proxy.getName(), HyggdrasilAPI.GSON.toJson(proxy))); // Save proxy in Redis cache
            this.eventBus.publish(new HyggProxyStartedEvent(proxy));

            System.out.println("Started '" + proxyName + "' (port: " + proxy.getPort() + ") [" + this.proxies.size() + "/" + this.maxProxies + "].");

            return proxy;
        }
        return null;
    }

    private int getAvailablePort() {
        int availablePort = this.startingPort;

        for (int i = this.startingPort; i < this.startingPort + this.maxProxies; i++) {
            availablePort = i;

            for (HyggProxy proxy : this.proxies.values()) {
                if (proxy.getPort() == i) {
                    availablePort = -1;
                    break;
                }
            }

            if (availablePort != -1) {
                break;
            }
        }
        return availablePort;
    }

    public void updateProxyInfo(HyggProxy proxy, HyggProxyInfoPacket packet) {
        final HyggProxy info = packet.getProxy();

        proxy.setData(info.getData());
        proxy.setState(info.getState());
        proxy.setPlayers(info.getPlayers());
        proxy.setPort(info.getPort());

        this.updateProxy(proxy);
    }

    public void updateProxy(HyggProxy proxy) {
        this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.set(HyggServersRequester.REDIS_KEY + proxy.getName(), HyggdrasilAPI.GSON.toJson(proxy))); // Save proxy in Redis cache
        this.eventBus.publish(new HyggProxyUpdatedEvent(proxy)); // Keep applications aware of the update
    }

    public boolean stopProxy(String name) {
        final HyggProxy proxy = this.getProxyByName(name);

        if (proxy != null) {
            final Runnable action = () -> {
                this.eventBus.publish(new HyggProxyStoppedEvent(proxy));
                this.swarm.removeService(name);
                this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.del(HyggServersRequester.REDIS_KEY + proxy.getName()));

                IOUtil.deleteDirectory(Paths.get(References.PROXIES_FOLDER.toString(), proxy.getName()));

                System.out.println("Stopped '" + name + "'.");
            };
            final HyggResponseCallback callback = response -> {
                final HyggResponse.Type type = response.getType();

                if (type != SUCCESS) {
                    System.err.println("'" + name + "' doesn't want to stop or an error occurred! Response: " + type + ". Forcing it to stop...");
                }

                action.run();
            };

            proxy.setState(HyggProxy.State.SHUTDOWN);

            this.packetProcessor.request(HyggChannel.PROXIES, new HyggStopProxyPacket(name))
                    .withResponseCallback(callback)
                    .withResponseTimeEndCallback(() -> {
                        System.err.println("'" + name + "' didn't respond to the stop packet sent! Forcing it to stop...");

                        action.run();
                    })
                    .exec();

            return true;
        } else {
            System.err.println("Couldn't stop a proxy with the following name: '" + name + "'!");
        }
        return false;
    }

    public HyggProxy getProxyByName(String proxyName) {
        for (HyggProxy proxy : this.proxies.values()) {
            if (proxy.getName().equals(proxyName)) {
                return proxy;
            }
        }
        return null;
    }

    public Set<HyggProxy> getProxies() {
        return Set.copyOf(this.proxies.values());
    }

}
