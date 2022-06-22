package fr.hyriode.hyggdrasil.lobby;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.lobby.HyggLobbyAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;
import fr.hyriode.hyggdrasil.api.proxy.packet.HyggEvacuatePacket;
import fr.hyriode.hyggdrasil.api.scheduler.HyggScheduler;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerOptions;
import fr.hyriode.hyggdrasil.api.server.HyggServerState;
import fr.hyriode.hyggdrasil.rule.HyggServerRule;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 18/04/2022 at 19:27
 */
public class HyggLobbyBalancer {

    private static final int MIN_PLAYERS = 5;

    private final int minimumLobbies;

    private final List<String> lobbies;
    private final List<String> startedLobbies;

    private final Hyggdrasil hyggdrasil;

    public HyggLobbyBalancer(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.lobbies = new CopyOnWriteArrayList<>();
        this.startedLobbies = new ArrayList<>();
        this.minimumLobbies = this.hyggdrasil.getRules().getServerRules().getOrDefault(HyggLobbyAPI.TYPE, new HyggServerRule()).getMinimums().getOrDefault("default", 0);

        this.startTasks();
    }

    private void startTasks() {
        System.out.println("Starting lobby balancing tasks...");

        final HyggScheduler scheduler = this.hyggdrasil.getAPI().getScheduler();

        try (final Jedis jedis = this.hyggdrasil.getRedis().getJedis()) {
            System.out.println("Removing old lobbies from balancer...");

            for (String lobby : jedis.zrange(HyggLobbyAPI.REDIS_KEY, 0, -1)) {
                jedis.zrem(HyggLobbyAPI.REDIS_KEY, lobby);
            }
        }

        scheduler.schedule(() -> {
            try (final Jedis jedis = this.hyggdrasil.getRedis().getJedis()) {
                for (String lobby : this.lobbies) {
                    final HyggServer server = this.hyggdrasil.getServerManager().getServerByName(lobby);

                    jedis.zadd(HyggLobbyAPI.REDIS_KEY, server.getPlayers().size(), lobby);
                }
            }
        }, 800, 800, TimeUnit.MILLISECONDS);

        scheduler.schedule(this::process, 500, 500, TimeUnit.MILLISECONDS);
    }

    public void onUpdate(HyggServer server) {
        if (server.getType().equals(HyggLobbyAPI.TYPE)) {
            final String serverName = server.getName();

            if (server.getState() == HyggServerState.READY) {
                this.startedLobbies.remove(serverName);

                this.addLobby(serverName);
            } else {
                this.removeLobby(server);
            }
        }
    }

    public void onStop(HyggServer server) {
        if (server.getType().equals(HyggLobbyAPI.TYPE)) {
            this.removeLobby(server);
        }
    }

    private void process() {
        final int lobbiesNumber = this.getLobbiesNumber();
        final int neededLobbies = this.neededLobbies();

        if (lobbiesNumber < neededLobbies) {
            for (int i = neededLobbies - lobbiesNumber; i > 0; i--) {
                this.startLobby();
            }
        } else if (lobbiesNumber > neededLobbies && lobbiesNumber > this.minimumLobbies) {
            for (String serverName : this.lobbies) {
                final HyggServer server = this.hyggdrasil.getServerManager().getServerByName(serverName);

                if (server.getState() != HyggServerState.READY) {
                    continue;
                }

                if (server.getPlayers().size() <= MIN_PLAYERS && this.lobbies.size() > 1) {
                    final String bestLobby = this.hyggdrasil.getAPI().getLobbyAPI().getBestLobby();

                    if (bestLobby.equals(server.getName())) {
                        return;
                    }

                    this.hyggdrasil.getServerManager().stopServer(server.getName(), 15);
                    this.hyggdrasil.getAPI().getPacketProcessor().request(HyggChannel.PROXIES, new HyggEvacuatePacket(serverName, bestLobby)).exec();
                }
            }
        }
    }

    private void startLobby() {
        final HyggData data = new HyggData();

        data.add(HyggServer.MAP_KEY, this.hyggdrasil.getAPI().getLobbyAPI().getCurrentMap());
        data.add(HyggServer.GAME_TYPE_KEY, HyggLobbyAPI.GAME_TYPE);

        final HyggServer lobby = this.hyggdrasil.getServerManager().startServer(HyggLobbyAPI.TYPE, new HyggServerOptions(), data, HyggLobbyAPI.MAX_PLAYERS);

        if (lobby != null) {
            this.startedLobbies.add(lobby.getName());
        }
    }

    private int neededLobbies() {
        return (int) Math.ceil((double) this.getPlayersOnLobbies() * 1.1 / HyggLobbyAPI.MAX_PLAYERS);
    }

    public void addLobby(String name) {
        if (!this.lobbies.contains(name)) {
            this.lobbies.add(name);
        }
    }

    public void removeLobby(HyggServer server) {
        try (final Jedis jedis = this.hyggdrasil.getRedis().getJedis()) {
            jedis.zrem(HyggLobbyAPI.REDIS_KEY, server.getName());
        }

        this.lobbies.remove(server.getName());
    }

    private int getPlayersOnLobbies() {
        int players = 0;

        for (String serverName : this.lobbies) {
            final HyggServer server = this.hyggdrasil.getServerManager().getServerByName(serverName);

            players += server.getPlayers().size();
        }

        return players;
    }

    private int getLobbiesNumber() {
        return this.lobbies.size() + this.startedLobbies.size();
    }

}
