package fr.hyriode.hyggdrasil.queue;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;
import fr.hyriode.hyggdrasil.api.queue.HyggQueueGroup;
import fr.hyriode.hyggdrasil.api.queue.HyggQueueInfo;
import fr.hyriode.hyggdrasil.api.queue.HyggQueuePlayer;
import fr.hyriode.hyggdrasil.api.queue.packet.HyggQueueInfoPacket;
import fr.hyriode.hyggdrasil.api.scheduler.HyggScheduler;
import fr.hyriode.hyggdrasil.api.scheduler.HyggTask;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerOptions;
import fr.hyriode.hyggdrasil.api.server.HyggServerState;
import fr.hyriode.hyggdrasil.server.HyggServerManager;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 09:14
 */
public class HyggQueue {

    private final HyggQueueInfo info;

    private final HyggPriorityQueue queue;

    private final HyggTask processingTask;
    private final HyggTask infoTask;

    private final HyggServerManager serverManager;
    private final Hyggdrasil hyggdrasil;

    public HyggQueue(Hyggdrasil hyggdrasil, HyggQueueInfo info) {
        this.hyggdrasil = hyggdrasil;
        this.info = info;
        this.serverManager = this.hyggdrasil.getServerManager();
        this.queue = new HyggPriorityQueue();

        final HyggScheduler scheduler = this.hyggdrasil.getAPI().getScheduler();

        this.processingTask = scheduler.schedule(this::process, 0, 500, TimeUnit.MILLISECONDS);
        this.infoTask = scheduler.schedule(this::sendQueueInfo, 0, 10, TimeUnit.SECONDS);
    }

    private void process() {
        final List<HyggServer> availableServers = this.getAvailableServers();

        availableServers.sort(Comparator.comparingInt(server -> server.getPlayingPlayers().size()));
        Collections.reverse(availableServers);

        this.anticipateServers(availableServers);

        for (HyggServer server : availableServers) {
            final int slots = server.getSlots();
            final int players = server.getPlayingPlayers().size();

            if (server.getState() == HyggServerState.READY && players < slots) {
                final List<HyggQueueGroup> groups = new ArrayList<>();

                this.queue.drainGroups(groups, slots - players);

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
                this.sendQueueInfo(player, group, i + 1);
            }
        }
    }

    private void sendQueueInfo(HyggQueuePlayer player, HyggQueueGroup group, int place) {
        final HyggQueueInfoPacket packet = new HyggQueueInfoPacket(player, group, this.info, place);

        this.hyggdrasil.getAPI().getPacketProcessor().request(HyggChannel.QUEUE, packet).exec();
    }

    public static void main(String[] args) {
        final int slots = 2;
        final int players = 4;
        final int queueSize = 3;
        final int totalServers = 2;
        final int neededServers = (players + queueSize) / slots + 2;

        System.out.println(neededServers);
    }

    private void anticipateServers(List<HyggServer> currentServers) {
        final int slots = currentServers.isEmpty() ? -1 : currentServers.get(0).getSlots();

        int currentPlayers = this.getSize();
        for (HyggServer server : currentServers) {
            currentPlayers += server.getPlayingPlayers().size();
        }

        int neededServers = currentPlayers / slots + 2;

        if (currentPlayers == 0 && currentServers.size() >= 2) {
            return;
        }

        if (currentServers.size() < 2) {
            neededServers = 2 - currentServers.size();
        }

        for (int i = 0; i < neededServers - currentServers.size(); i++ ) {
            final HyggData data = new HyggData();

            data.add(HyggServer.GAME_TYPE_KEY, this.info.getGameType());

            if (this.info.getMap() != null) {
                data.add(HyggServer.MAP_KEY, this.info.getMap());
            }

            this.serverManager.startServer(this.info.getGame(), new HyggServerOptions(), data, slots);
        }
    }

    private List<HyggServer> getAvailableServers() {
        return this.info.getMap() != null ? this.serverManager.getAvailableServers(this.info.getGame(), this.info.getGameType(), this.info.getMap()) : this.serverManager.getAvailableServers(this.info.getGame(), this.info.getGameType());
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

    public HyggQueueInfo getInfo() {
        return this.info;
    }

}
