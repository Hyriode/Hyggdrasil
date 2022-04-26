package fr.hyriode.hyggdrasil.queue;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;
import fr.hyriode.hyggdrasil.api.queue.HyggQueueGroup;
import fr.hyriode.hyggdrasil.api.queue.HyggQueuePlayer;
import fr.hyriode.hyggdrasil.api.queue.packet.HyggQueueInfoPacket;
import fr.hyriode.hyggdrasil.api.scheduler.HyggScheduler;
import fr.hyriode.hyggdrasil.api.scheduler.HyggTask;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerOptions;
import fr.hyriode.hyggdrasil.api.server.HyggServerState;
import fr.hyriode.hyggdrasil.server.HyggServerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 09:14
 */
public class HyggQueue {

    private final String game;
    private final String gameType;
    private final String map;

    private final HyggPriorityQueue queue;

    private final HyggTask processingTask;
    private final HyggTask infoTask;

    private final HyggServerManager serverManager;
    private final Hyggdrasil hyggdrasil;

    private boolean started;

    public HyggQueue(Hyggdrasil hyggdrasil, String game, String gameType, String map) {
        this.hyggdrasil = hyggdrasil;
        this.serverManager = this.hyggdrasil.getServerManager();
        this.game = game;
        this.gameType = gameType;
        this.map = map;
        this.queue = new HyggPriorityQueue();

        final HyggScheduler scheduler = this.hyggdrasil.getAPI().getScheduler();

        this.processingTask = scheduler.schedule(this::process, 0, 500, TimeUnit.MILLISECONDS);
        this.infoTask = scheduler.schedule(this::sendQueueInfo, 0, 10, TimeUnit.SECONDS);
    }

    private void process() {
        final List<HyggServer> availableServers = this.getAvailableServers();

        this.anticipateServers(availableServers);

        for (HyggServer server : availableServers) {
            if (server.getState() == HyggServerState.READY) {
                final List<HyggQueueGroup> groups = new ArrayList<>();

                this.queue.drainGroups(groups, server.getSlots() - server.getPlayers().size());

                for (HyggQueueGroup group : groups) {
                    if (this.queue.remove(group)) {
                        group.send(this.hyggdrasil.getAPI(), server);
                    }
                }
            }
        }
    }

    private void sendQueueInfo() {
        final List<HyggQueueGroup> groups = new ArrayList<>(this.queue);

        for (int i = 0; i < groups.size(); i++) {
            final HyggQueueGroup group = groups.get(i);

            for (HyggQueuePlayer player : group.getPlayers()) {
                this.sendQueueInfo(player, i + 1, group.getSize());
            }
        }
    }

    private void sendQueueInfo(HyggQueuePlayer player, int place, int groupSize) {
        final HyggQueueInfoPacket packet = new HyggQueueInfoPacket(player, this.game, this.gameType, this.map);

        packet.setQueueSize(this.getSize());
        packet.setGroupsInQueue(this.queue.size());
        packet.setPlace(place);
        packet.setGroupSize(groupSize);
    }

    private void anticipateServers(List<HyggServer> currentServers) {
        final int slots = currentServers.isEmpty() ? -1 : currentServers.get(0).getSlots();

        int currentPlayers = this.getSize();
        for (HyggServer server : currentServers) {
            currentPlayers += server.getPlayingPlayers().size();
        }

        final int neededServers = (int) ((currentPlayers) * 1.2 / slots + 2);

        if (currentPlayers == 0 && neededServers >= 2) {
            return;
        }

        for (int i = 0; i < neededServers - currentServers.size(); i++ ) {
            final HyggData data = new HyggData();

            data.add(HyggServer.GAME_TYPE_KEY, this.gameType);

            if (this.map != null) {
                data.add(HyggServer.MAP_KEY, this.map);
            }

            this.serverManager.startServer(this.game, new HyggServerOptions(), data, slots);
        }
    }

    private List<HyggServer> getAvailableServers() {
        return this.map != null ? this.serverManager.getAvailableServers(this.game, this.gameType, this.map) : this.serverManager.getAvailableServers(this.game, this.gameType);
    }

    public void disable() {
        this.processingTask.cancel();
        this.infoTask.cancel();
    }

    public boolean addGroup(HyggQueueGroup group) {
        return this.queue.add(group);
    }

    public boolean removeGroup(HyggQueueGroup group) {
        return this.queue.remove(group);
    }

    public boolean removeGroup(UUID groupId) {
        for (HyggQueueGroup group : this.queue) {
            if (group.getId().equals(groupId)) {
                return this.removeGroup(group);
            }
        }
        return false;
    }

    public boolean removePlayer(UUID playerId) {
        final HyggQueueGroup group = this.getPlayerGroup(playerId);

        if (group != null) {
            this.removeGroup(group);

            final boolean result = group.removePlayer(playerId);

            if (group.getLeader() != null) {
                this.addGroup(group);
            }
            return result;
        }
        return false;
    }

    public boolean containsGroup(UUID groupId) {
        for (HyggQueueGroup group : this.queue) {
            if (group.getId().equals(groupId)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsPlayer(UUID player) {
        for (HyggQueueGroup group : this.queue) {
            if (group.contains(player)) {
                return true;
            }
        }
        return false;
    }

    public HyggQueueGroup getGroup(UUID groupId) {
        for (HyggQueueGroup group : this.queue) {
            if (group.getId().equals(groupId)) {
                return group;
            }
        }
        return null;
    }

    public HyggQueueGroup getPlayerGroup(UUID playerId) {
        for (HyggQueueGroup group : this.queue) {
            if (group.contains(playerId)) {
                return group;
            }
        }
        return null;
    }

    public int getSize() {
        int size = 0;

        for (HyggQueueGroup group : this.queue) {
            size += group.getSize();
        }

        return size;
    }

    public String getGame() {
        return this.game;
    }

    public String getGameType() {
        return this.gameType;
    }

    public String getMap() {
        return this.map;
    }

}
