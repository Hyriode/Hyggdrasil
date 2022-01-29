package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggProxyServerActionPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggStopProxyPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.server.HyggStopServerPacket;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseCallback;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxyState;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerState;
import fr.hyriode.hyggdrasil.docker.swarm.DockerSwarm;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggProxyServerActionPacket.Action.REMOVE;
import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyManager {

    private final Set<HyggProxy> proxies;

    private final DockerSwarm swarm;
    private final HyggPacketProcessor packetProcessor;

    private final Hyggdrasil hyggdrasil;

    public HyggProxyManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.swarm = this.hyggdrasil.getDocker().getSwarm();
        this.packetProcessor = this.hyggdrasil.getAPI().getPacketProcessor();
        this.proxies = new HashSet<>();

        IOUtil.createDirectory(References.PROXIES_PLUGINS_FOLDER);
    }

    public String startProxy() {
        final HyggProxy proxy = new HyggProxy();

        this.swarm.runService(new HyggProxyService(this.hyggdrasil, proxy));

        this.proxies.add(proxy);

        System.out.println("Started '" + proxy.getName() + "' (port: " + proxy.getPort() + ").");

        return proxy.getName();
    }

    public boolean stopProxy(String name) {
        final HyggProxy proxy = this.getProxyByName(name);

        if (proxy != null) {
            final Runnable action = () -> {
                System.out.println("Stopping '" + name + "'...");

                this.swarm.removeService(name);
            };
            final HyggResponseCallback callback = response -> {
                final HyggResponse.Type type = response.getType();

                if (type != SUCCESS) {
                    System.err.println("'" + name + "' doesn't want to stop or an error occurred! Response: " + type + ". Reason: " + response.getMessage() + ". Forcing it to stop...");
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
