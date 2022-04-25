package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.event.HyggEventBus;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyStartedEvent;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyStoppedEvent;
import fr.hyriode.hyggdrasil.api.event.model.proxy.HyggProxyUpdatedEvent;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseCallback;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxyState;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggProxyInfoPacket;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggStopProxyPacket;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerSwarm;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:05
 */
public class HyggProxyManager {

    private static final int MAX_PROXIES = Integer.parseInt(System.getenv("MAX_PROXIES"));
    private static final int STARTING_PORT = Integer.parseInt(System.getenv("PROXIES_STARTING_PORT"));

    public static final DockerImage PROXY_IMAGE = new DockerImage("hygg-proxy", "latest");

    private final List<HyggProxy> proxies;

    private final DockerSwarm swarm;
    private final HyggPacketProcessor packetProcessor;
    private final HyggEventBus eventBus;

    private final Hyggdrasil hyggdrasil;

    public HyggProxyManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.swarm = this.hyggdrasil.getDocker().getSwarm();
        this.packetProcessor = this.hyggdrasil.getAPI().getPacketProcessor();
        this.eventBus = this.hyggdrasil.getAPI().getEventBus();
        this.proxies = new ArrayList<>();

        new HyggProxyBalancer(this.hyggdrasil, this);

        IOUtil.createDirectory(References.PROXIES_COMMON_FOLDER);

        this.hyggdrasil.getDocker().getImageManager().buildImage(Paths.get(References.PROXY_IMAGES_FOLDER.toString(), "Dockerfile").toFile(), PROXY_IMAGE.getName());

        this.removeOldProxies();
    }

    private void removeOldProxies() {
        this.hyggdrasil.getAPI().getScheduler().schedule(() -> {
            System.out.println("Removing old proxies (after 45 seconds of waiting)...");
            try {
                Files.list(References.PROXIES_FOLDER).forEach(path -> {
                    final String pathStr = path.toString();

                    if (!pathStr.equals(References.PROXIES_COMMON_FOLDER.toString())) {
                        final String[] splitPath = path.toString().split("/");
                        final String proxyName = splitPath[splitPath.length - 1];

                        if (this.getProxyByName(proxyName) == null) {
                            IOUtil.deleteDirectory(path);

                            System.out.println("Removed '" + proxyName + "'.");
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 45, TimeUnit.SECONDS);
    }

    public HyggProxy startProxy() {
        if (this.proxies.size() >= MAX_PROXIES) {
            System.err.println("Cannot start a new proxy! Proxies limit has already been reached (" + this.proxies.size() + "/" + MAX_PROXIES + ")!");
            return null;
        }

        final int availablePort = this.getAvailablePort();

        if (availablePort != -1) {
            final HyggProxy proxy = new HyggProxy();
            final Path proxyFolder = Paths.get(References.PROXIES_FOLDER.toString(), proxy.getName());

            if (IOUtil.createDirectory(proxyFolder)) {
                if (!Files.exists(References.PROXIES_COMMON_FOLDER) || !IOUtil.copyContent(References.PROXIES_COMMON_FOLDER, proxyFolder)) {
                    return null;
                }

                proxy.setPort(availablePort);

                this.swarm.runService(new HyggProxyService(this.hyggdrasil, proxy, this.proxies.size() == 0));

                this.proxies.add(proxy);

                this.eventBus.publish(new HyggProxyStartedEvent(proxy));

                System.out.println("Started '" + proxy.getName() + "' (port: " + proxy.getPort() + ") [" + this.proxies.size() + "/" + MAX_PROXIES + "].");

                return proxy;
            }
        }
        return null;
    }

    private int getAvailablePort() {
        int availablePort = STARTING_PORT;

        for (int i = STARTING_PORT; i < STARTING_PORT + MAX_PROXIES; i++) {
            for (HyggProxy proxy : this.proxies) {
                if (proxy.getPort() == i) {
                    availablePort = -1;
                } else {
                    availablePort = i;
                }
            }
        }
        return availablePort;
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

                IOUtil.delete(Paths.get(References.PROXIES_FOLDER.toString(), proxy.getName()));

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

    public HyggProxy getBestProxy() {
        HyggProxy bestProxy = null;
        for (HyggProxy proxy : this.proxies) {
            if (proxy.getState() == HyggProxyState.READY) {
                if (bestProxy == null) {
                    bestProxy = proxy;
                    continue;
                }

                if (proxy.getPlayers() < bestProxy.getPlayers()) {
                    bestProxy = proxy;
                }
            }
        }
        return bestProxy;
    }

    public List<HyggProxy> getProxies() {
        return this.proxies;
    }

}
