package fr.hyriode.hyggdrasil.server;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.event.HyggEventBus;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerStartedEvent;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerStoppedEvent;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerUpdatedEvent;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseCallback;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggProxyServerActionPacket;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerOptions;
import fr.hyriode.hyggdrasil.api.server.HyggServerState;
import fr.hyriode.hyggdrasil.api.server.packet.HyggServerInfoPacket;
import fr.hyriode.hyggdrasil.api.server.packet.HyggStopServerPacket;
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
import java.util.function.BiFunction;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;
import static fr.hyriode.hyggdrasil.api.proxy.packet.HyggProxyServerActionPacket.Action.ADD;
import static fr.hyriode.hyggdrasil.api.proxy.packet.HyggProxyServerActionPacket.Action.REMOVE;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 14:06
 */
public class HyggServerManager {

    private static final BiFunction<Path, Path, Boolean> COPY = (from, to) -> !Files.exists(from) || IOUtil.copyContent(from, to);

    public static final DockerImage SERVER_IMAGE = new DockerImage("hygg-server", "latest");

    private static final String PLUGINS = "plugins";

    private final List<HyggServer> servers;

    private final DockerSwarm swarm;
    private final HyggPacketProcessor packetProcessor;
    private final HyggEventBus eventBus;

    private final Hyggdrasil hyggdrasil;

    public HyggServerManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.swarm = this.hyggdrasil.getDocker().getSwarm();
        this.packetProcessor = this.hyggdrasil.getAPI().getPacketProcessor();
        this.eventBus = this.hyggdrasil.getAPI().getEventBus();
        this.servers = new ArrayList<>();

        IOUtil.createDirectory(References.SERVERS_COMMON_FOLDER);
        IOUtil.createDirectory(References.SERVERS_TYPES_FOLDER);

        this.hyggdrasil.getDocker().getImageManager().buildImage(Paths.get(References.SERVER_IMAGES_FOLDER.toString(), "Dockerfile").toFile(), SERVER_IMAGE.getName());

        this.removeOldServers();
    }

    private void removeOldServers() {
        this.hyggdrasil.getAPI().getScheduler().schedule(() -> {
            System.out.println("Removing old servers (after 45 seconds of waiting)...");
            try {
                Files.list(References.SERVERS_FOLDER).forEach(path -> {
                    final String pathStr = path.toString();

                    if (!pathStr.equals(References.SERVERS_COMMON_FOLDER.toString()) && !pathStr.equals(References.SERVERS_TYPES_FOLDER.toString())) {
                        final String[] splitPath = path.toString().split("/");
                        final String serverName = splitPath[splitPath.length - 1];

                        if (this.getServerByName(serverName) == null) {
                            IOUtil.deleteDirectory(path);

                            System.out.println("Removed '" + serverName + "'.");
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 45, TimeUnit.SECONDS);
    }

    public HyggServer startServer(String type, HyggServerOptions options, HyggData data, int slots) {
        final Path typeFolder = this.getTypeFolder(type);

        if (typeFolder != null) {
            final HyggServer server = new HyggServer(type, options, data);
            final Path serverFolder = Paths.get(References.SERVERS_FOLDER.toString(), server.getName());

            if (IOUtil.createDirectory(serverFolder)) {
                if (IOUtil.copyContent(References.SERVERS_COMMON_FOLDER, serverFolder)) {
                    if (!COPY.apply(Paths.get(typeFolder.toString(), PLUGINS), Paths.get(serverFolder.toString(), PLUGINS))) {
                        return null;
                    }

                    server.setSlots(slots);

                    this.swarm.runService(new HyggServerService(this.hyggdrasil, server));

                    this.addServerToProxies(server);

                    this.servers.add(server);

                    this.eventBus.publish(new HyggServerStartedEvent(server));

                    System.out.println("Started '" + server.getName() + "'.");

                    return server;
                }
            }
        }
        return null;
    }

    public void updateServer(HyggServer server, HyggServerInfoPacket info) {
        server.setPlayers(info.getPlayers());
        server.setState(info.getState());
        server.setSlots(info.getSlots());

        this.eventBus.publish(new HyggServerUpdatedEvent(server));

        this.hyggdrasil.getLobbyBalancer().onUpdate(server);
    }

    public boolean stopServer(String name, long waitingTime) {
        final HyggServer server = this.getServerByName(name);

        if (server != null) {
            final Runnable action = () -> this.hyggdrasil.getAPI().getScheduler().schedule(() -> {
                this.removeServerFromProxies(server);

                this.swarm.removeService(name);

                IOUtil.delete(Paths.get(References.SERVERS_FOLDER.toString(), server.getName()));

                System.out.println("Stopped '" + name + "'.");
            }, waitingTime, TimeUnit.SECONDS);
            final HyggResponseCallback callback = response -> {
                final HyggResponse.Type type = response.getType();

                if (type != SUCCESS) {
                    System.err.println("'" + name + "' doesn't want to stop or an error occurred! Response: " + type + ". Forcing it to stop...");
                }

                action.run();
            };

            server.setState(HyggServerState.SHUTDOWN);

            this.hyggdrasil.getLobbyBalancer().onStop(server);

            this.eventBus.publish(new HyggServerStoppedEvent(server));

            this.packetProcessor.request(HyggChannel.SERVERS, new HyggStopServerPacket(name))
                    .withResponseCallback(callback)
                    .withResponseTimeEndCallback(() -> {
                        System.err.println("'" + name + "' didn't respond to the stop packet sent! Forcing it to stop...");

                        action.run();
                    })
                    .exec();

            return true;
        } else {
            System.err.println("Couldn't stop a server with the following name: '" + name + "'!");
        }
        return false;
    }

    public boolean stopServer(String name) {
       return this.stopServer(name, 0);
    }

    public void addServerToProxies(HyggServer server) {
        this.actionOnProxies(ADD, server);
    }

    public void addServersToProxies() {
        this.servers.forEach(this::addServerToProxies);
    }

    public void removeServerFromProxies(HyggServer server) {
        this.actionOnProxies(REMOVE, server);
    }

    private void actionOnProxies(HyggProxyServerActionPacket.Action action, HyggServer server) {
        this.packetProcessor.request(HyggChannel.PROXIES, new HyggProxyServerActionPacket(action, server.getName()))
                .withMaxResponses(this.hyggdrasil.getProxyManager().getProxies().size())
                .exec();
    }

    public boolean isTypeExisting(String type) {
        return this.getTypeFolder(type) != null;
    }

    private Path getTypeFolder(String type) {
        try {
            for (Path path : Files.list(References.SERVERS_TYPES_FOLDER).toList()) {
                if (path.getFileName().toString().equals(type)) {
                    return path;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<HyggServer> getAvailableServers(String game, String gameType, String map) {
        final List<HyggServer> servers = new ArrayList<>();

        for (HyggServer server : this.servers) {
            if (server.getType().equals(game) && server.getGameType().equals(gameType) && server.getMap().equals(map)) {
                servers.add(server);
            }
        }
        return servers;
    }

    public List<HyggServer> getAvailableServers(String game, String gameType) {
        final List<HyggServer> servers = new ArrayList<>();

        for (HyggServer server : this.servers) {
            if (server.getType().equals(game) && server.getGameType().equals(gameType)) {
                servers.add(server);
            }
        }
        return servers;
    }

    public HyggServer getServerByName(String serverName) {
        for (HyggServer server : this.servers) {
            if (server.getName().equals(serverName)) {
                return server;
            }
        }
        return null;
    }

    public List<HyggServer> getServersByType(String serverType) {
        final List<HyggServer> result = new ArrayList<>();

        for (HyggServer server : this.servers) {
            if (server.getType().equals(serverType)) {
                result.add(server);
            }
        }
        return result;
    }

    public List<HyggServer> getServers() {
        return this.servers;
    }

}
