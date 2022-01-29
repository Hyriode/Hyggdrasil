package fr.hyriode.hyggdrasil.server;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggProxyServerActionPacket;
import fr.hyriode.hyggdrasil.api.protocol.packet.model.server.HyggStopServerPacket;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseCallback;
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
import java.util.stream.Collectors;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.*;
import static fr.hyriode.hyggdrasil.api.protocol.packet.model.proxy.HyggProxyServerActionPacket.Action.*;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 14:06
 */
public class HyggServerManager {

    private final Set<HyggServer> servers;

    private final DockerSwarm swarm;
    private final HyggPacketProcessor packetProcessor;

    private final Hyggdrasil hyggdrasil;

    public HyggServerManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.swarm = this.hyggdrasil.getDocker().getSwarm();
        this.packetProcessor = this.hyggdrasil.getAPI().getPacketProcessor();
        this.servers = new HashSet<>();

        IOUtil.createDirectory(References.SERVERS_COMMON_FOLDER);
        IOUtil.createDirectory(References.SERVERS_TYPES_FOLDER);
    }

    public String startServer(String type) {
        final Path typeFolder = this.getTypeFolder(type);

        if (typeFolder != null) {
            final HyggServer server = new HyggServer(type);
            final Path serverFolder = Paths.get(References.SERVERS_FOLDER.toString(), server.getName());
            final Path pluginsFolder = Paths.get(serverFolder.toString(), "plugins");
            final Path worldFolder = Paths.get(serverFolder.toString(), "world");

            if (IOUtil.createDirectory(pluginsFolder) && IOUtil.createDirectory(worldFolder)) {
                if (IOUtil.copyContent(References.SERVERS_COMMON_FOLDER, pluginsFolder) && IOUtil.copyContent(Paths.get(typeFolder.toString(), "plugins"), pluginsFolder) && IOUtil.copyContent(Paths.get(typeFolder.toString(), "world"), worldFolder)) {
                    final HyggProxyServerActionPacket packet = new HyggProxyServerActionPacket(ADD, server.getName());

                    this.swarm.runService(new HyggServerService(server));

                    this.packetProcessor.request(HyggChannel.PROXIES, packet)
                            .withMaxResponses(this.hyggdrasil.getProxyManager().getProxies().size())
                            .exec();

                    this.servers.add(server);

                    System.out.println("Started '" + server.getName() + "'.");

                    return server.getName();
                }
            }
        }
        return null;
    }

    public boolean stopServer(String name) {
        final HyggServer server = this.getServerByName(name);

        if (server != null) {
            final Runnable action = () -> {
                System.out.println("Stopping '" + name + "'...");

                this.packetProcessor.request(HyggChannel.PROXIES, new HyggProxyServerActionPacket(REMOVE, name)).exec();

                this.swarm.removeService(name);

                IOUtil.delete(Paths.get(References.SERVERS_FOLDER.toString(), server.getName()));
            };
            final HyggResponseCallback callback = response -> {
                final HyggResponse.Type type = response.getType();

                if (type != SUCCESS) {
                    System.err.println("'" + name + "' doesn't want to stop or an error occurred! Response: " + type + ". Reason: " + response.getMessage() + ". Forcing it to stop...");
                }

                action.run();
            };

            server.setState(HyggServerState.SHUTDOWN);

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

    public HyggServer getServerByName(String serverName) {
        for (HyggServer server : this.servers) {
            if (server.getName().equals(serverName)) {
                return server;
            }
        }
        return null;
    }

    public Set<HyggServer> getServers() {
        return this.servers;
    }

}
