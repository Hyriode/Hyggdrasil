package fr.hyriode.hyggdrasil.server;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.event.HyggEventBus;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerStartedEvent;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerStoppedEvent;
import fr.hyriode.hyggdrasil.api.event.model.server.HyggServerUpdatedEvent;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseCallback;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerCreationInfo;
import fr.hyriode.hyggdrasil.api.server.HyggServersRequester;
import fr.hyriode.hyggdrasil.api.server.packet.HyggServerInfoPacket;
import fr.hyriode.hyggdrasil.api.server.packet.HyggStopServerPacket;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerSwarm;
import fr.hyriode.hyggdrasil.template.HyggTemplate;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/12/2021 at 14:06
 */
public class HyggServerManager {

    private final Map<String, HyggServer> servers = new ConcurrentHashMap<>();

    private final DockerImage proxyImage;

    private final DockerSwarm swarm;
    private final HyggPacketProcessor packetProcessor;
    private final HyggEventBus eventBus;

    private final Hyggdrasil hyggdrasil;

    public HyggServerManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.swarm = this.hyggdrasil.getDocker().getSwarm();
        this.packetProcessor = this.hyggdrasil.getAPI().getPacketProcessor();
        this.eventBus = this.hyggdrasil.getAPI().getEventBus();
        this.proxyImage = this.hyggdrasil.getDocker().getImageManager().getImage(Hyggdrasil.getConfig().getServers().getImage());

        for (HyggServer server : this.hyggdrasil.getAPI().getServersRequester().fetchServers()) {
            this.servers.put(server.getName(), server);
        }
    }

    public HyggServer startServer(HyggServerCreationInfo info) {
        final String type = info.getType();
        final HyggTemplate template = this.hyggdrasil.getTemplateManager().getTemplate(type);

        if (template != null) {
            final HyggServer server = new HyggServer(type, info.getGameType(), info.getMap(), info.getAccessibility(), info.getProcess(), info.getData(), info.getSlots());
            final String serverName = server.getName();

            this.hyggdrasil.getTemplateManager().getDownloader().copyFiles(template, Paths.get(References.SERVERS_FOLDER.toString(), serverName));
            this.swarm.runService(new HyggServerService(server, template, this.proxyImage));
            this.servers.put(serverName, server);
            this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.set(HyggServersRequester.REDIS_KEY + server.getName(), HyggdrasilAPI.GSON.toJson(server))); // Save server in Redis cache
            this.eventBus.publish(new HyggServerStartedEvent(server));

            System.out.println("Started '" + serverName + "' (" + type + (server.getGameType() != null ? "#" + server.getGameType() : "") + (server.getMap() != null ? " with map: " + server.getMap() : "") + ").");

            return server;
        }
        return null;
    }

    public void updateServerInfo(HyggServer server, HyggServerInfoPacket packet) {
        final HyggServer info = packet.getServer();

        server.setMap(info.getMap());
        server.setAccessibility(info.getAccessibility());
        server.setProcess(info.getProcess());
        server.setState(info.getState());
        server.setData(info.getData());
        server.setPlayers(info.getPlayers());
        server.setPlayingPlayers(info.getPlayingPlayers());
        server.setSlots(info.getSlots());

        this.updateServer(server);
    }

    public void updateServer(HyggServer server) {
        this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.set(HyggServersRequester.REDIS_KEY + server.getName(), HyggdrasilAPI.GSON.toJson(server))); // Save server in Redis cache
        this.eventBus.publish(new HyggServerUpdatedEvent(server)); // Keep applications aware of the update
    }

    public boolean stopServer(String name) {
        final HyggServer server = this.getServer(name);

        if (server != null) {
            this.eventBus.publish(new HyggServerStoppedEvent(server));
            this.servers.remove(name);
            this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.del(HyggServersRequester.REDIS_KEY + server.getName())); // Delete server from Redis cache
            this.swarm.removeService(name);

            IOUtil.deleteDirectory(Paths.get(References.SERVERS_FOLDER.toString(), name));

            System.out.println("Stopped '" + name + "'.");
            return true;
        } else {
            System.err.println("Couldn't stop a server with the following name: '" + name + "'!");
        }
        return false;
    }

    public HyggServer getServer(String serverName) {
        return this.servers.get(serverName);
    }

    public Set<HyggServer> getServersByType(String serverType) {
        final Set<HyggServer> result = new HashSet<>();

        for (HyggServer server : this.servers.values()) {
            if (server.getType().equals(serverType)) {
                result.add(server);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    public Set<HyggServer> getServers() {
        return Set.copyOf(this.servers.values());
    }

}
