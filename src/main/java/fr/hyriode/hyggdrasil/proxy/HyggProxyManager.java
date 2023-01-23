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
import fr.hyriode.hyggdrasil.api.proxy.HyggProxiesRequester;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggProxyInfoPacket;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggStopProxyPacket;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerSwarm;
import fr.hyriode.hyggdrasil.template.HyggTemplate;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyManager {

    private final int maxProxies = Hyggdrasil.getConfig().getProxies().getMaxProxies();

    private final Map<String, HyggProxy> proxies = new ConcurrentHashMap<>();

    private final HyggTemplate proxyTemplate;
    private final DockerImage proxyImage;

    private final DockerSwarm swarm;
    private final HyggPacketProcessor packetProcessor;
    private final HyggEventBus eventBus;

    private final Hyggdrasil hyggdrasil;

    public HyggProxyManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.swarm = this.hyggdrasil.getDocker().getSwarm();
        this.packetProcessor = this.hyggdrasil.getAPI().getPacketProcessor();
        this.eventBus = this.hyggdrasil.getAPI().getEventBus();
        this.proxyTemplate = this.hyggdrasil.getTemplateManager().getTemplate(Hyggdrasil.getConfig().getProxies().getTemplate());
        this.proxyImage = this.hyggdrasil.getDocker().getImageManager().getImage(Hyggdrasil.getConfig().getProxies().getImage());

        for (HyggProxy proxy : this.hyggdrasil.getAPI().getProxiesRequester().fetchProxies()) {
            this.proxies.put(proxy.getName(), proxy);
        }
    }

    public HyggProxy startProxy(HyggData data) {
        if (this.proxies.size() >= this.maxProxies) {
            System.err.println("Cannot start a new proxy! Proxies limit has already been reached (" + this.proxies.size() + "/" + this.maxProxies + ")!");
            return null;
        }

        final HyggProxy proxy = new HyggProxy(this.generateName(), data);
        final String proxyName = proxy.getName();

        this.hyggdrasil.getTemplateManager().getDownloader().copyFiles(this.proxyTemplate, Paths.get(References.PROXIES_FOLDER.toString(), proxyName));
        this.swarm.runService(new HyggProxyService(proxy, this.proxyImage));
        this.proxies.put(proxyName, proxy);
        this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.set(HyggProxiesRequester.REDIS_KEY + proxy.getName(), HyggdrasilAPI.GSON.toJson(proxy))); // Save proxy in Redis cache
        this.eventBus.publish(new HyggProxyStartedEvent(proxy));

        System.out.println("Started '" + proxyName + "' [" + this.proxies.size() + "/" + this.maxProxies + "].");

        return proxy;
    }

    private String generateName() {
        String name = null;

        for (int i = 0; i < this.maxProxies; i++) {
            name = String.format("proxy%02d", i + 1); // Generate a proxy name with a custom format. E.g. proxy02 / proxy18

            if (this.getProxy(name) == null) {
                return name;
            }
        }
        return name;
    }

    public void updateProxyInfo(HyggProxy proxy, HyggProxyInfoPacket packet) {
        final HyggProxy info = packet.getProxy();

        proxy.setData(info.getData());
        proxy.setState(info.getState());
        proxy.setPlayers(info.getPlayers());

        this.updateProxy(proxy);
    }

    public void updateProxy(HyggProxy proxy) {
        this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.set(HyggProxiesRequester.REDIS_KEY + proxy.getName(), HyggdrasilAPI.GSON.toJson(proxy))); // Save proxy in Redis cache
        this.eventBus.publish(new HyggProxyUpdatedEvent(proxy)); // Keep applications aware of the update
    }

    public boolean stopProxy(String name) {
        final HyggProxy proxy = this.getProxy(name);

        if (proxy != null) {
            final Runnable action = () -> {
                this.eventBus.publish(new HyggProxyStoppedEvent(proxy));
                this.proxies.remove(name);
                this.swarm.removeService(name);
                this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.del(HyggProxiesRequester.REDIS_KEY + proxy.getName()));

                IOUtil.deleteDirectory(Paths.get(References.PROXIES_FOLDER.toString(), proxy.getName()));

                System.out.println("Stopped '" + name + "'.");
            };

            proxy.setState(HyggProxy.State.SHUTDOWN);

            this.packetProcessor.request(HyggChannel.PROXIES, new HyggStopProxyPacket(name))
                    .withResponseCallback(response -> action.run())
                    .withTimeoutCallback(action)
                    .exec();

            return true;
        } else {
            System.err.println("Couldn't stop a proxy with the following name: '" + name + "'!");
        }
        return false;
    }

    public HyggProxy getProxy(String proxyName) {
        return this.proxies.get(proxyName);
    }

    public Set<HyggProxy> getProxies() {
        return Set.copyOf(this.proxies.values());
    }

}
