package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.event.HyggEventBus;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyStartedEvent;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyStoppedEvent;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyUpdatedEvent;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggProxyInfoPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggStopProxyPacket;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseCallback;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxyState;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerSwarm;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyManager {

    public static final DockerImage PROXY_IMAGE = new DockerImage("hygg-proxy", "latest");

    private final Set<HyggProxy> proxies;

    private final DockerSwarm swarm;
    private final HyggPacketProcessor packetProcessor;
    private final HyggEventBus eventBus;

    private final Hyggdrasil hyggdrasil;

    public HyggProxyManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.swarm = this.hyggdrasil.getDocker().getSwarm();
        this.packetProcessor = this.hyggdrasil.getAPI().getPacketProcessor();
        this.eventBus = this.hyggdrasil.getAPI().getEventBus();
        this.proxies = new HashSet<>();

        IOUtil.createDirectory(References.PROXIES_PLUGINS_FOLDER);

        this.hyggdrasil.getDocker().getImageManager().buildImage(Paths.get(References.PROXY_IMAGES_FOLDER.toString(), "Dockerfile").toFile(), PROXY_IMAGE.getName());
    }

    public HyggProxy startProxy() {
        final HyggProxy proxy = new HyggProxy();

        this.swarm.runService(new HyggProxyService(this.hyggdrasil, proxy));

        this.proxies.add(proxy);

        this.eventBus.publish(new HyggProxyStartedEvent(proxy));

        System.out.println("Started '" + proxy.getName() + "' (port: " + proxy.getPort() + ").");

        return proxy;
    }

    public void updateProxy(HyggProxy proxy, HyggProxyInfoPacket info) {
        proxy.setPlayers(info.getPlayers());
        proxy.setState(info.getState());

        this.eventBus.publish(new HyggProxyUpdatedEvent(proxy));
    }

    public boolean stopProxy(String name) {
        final HyggProxy proxy = this.getProxyByName(name);

        if (proxy != null) {
            final Runnable action = () -> {
                this.swarm.removeService(name);

                this.eventBus.publish(new HyggProxyStoppedEvent(proxy));

                System.out.println("Stopped '" + name + "'.");
            };
            final HyggResponseCallback callback = response -> {
                final HyggResponse.Type type = response.getType();

                if (type != SUCCESS) {
                    System.err.println("'" + name + "' doesn't want to stop or an error occurred! Response: " + type + ". Forcing it to stop...");
                }

                action.run();
            };

            proxy.setState(HyggProxyState.SHUTDOWN);

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
        for (HyggProxy proxy : this.proxies) {
            if (proxy.getName().equals(proxyName)) {
                return proxy;
            }
        }
        return null;
    }

    public Set<HyggProxy> getProxies() {
        return this.proxies;
    }

}
