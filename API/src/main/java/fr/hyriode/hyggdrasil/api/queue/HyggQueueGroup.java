package fr.hyriode.hyggdrasil.api.queue;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.queue.packet.HyggQueueTransferPacket;
import fr.hyriode.hyggdrasil.api.queue.packet.HyggQueueUpdateGroupPacket;
import fr.hyriode.hyggdrasil.api.server.HyggServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 09:19
 */
public class HyggQueueGroup {

    private final String id;
    private HyggQueuePlayer leader;
    private List<HyggQueuePlayer> players;

    private int priority;

    public HyggQueueGroup(String id, HyggQueuePlayer leader, List<HyggQueuePlayer> players) {
        this.id = id;
        this.leader = leader;
        this.players = players;
        this.priority = leader.getPriority();

        this.players.add(this.leader);

        this.calculatePriority();
    }

    public void update(HyggQueueUpdateGroupPacket packet) {
        final HyggQueueGroup group = packet.getGroup();

        this.leader = group.getLeader();
        this.players = group.getPlayers();

        this.calculatePriority();
    }

    public boolean contains(UUID playerId) {
        for (HyggQueuePlayer player : this.players) {
            if (player.getUniqueId().equals(playerId)) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(HyggQueuePlayer player) {
        return this.players.contains(player);
    }

    public boolean addPlayer(HyggQueuePlayer player) {
        if (this.contains(player)) {
            return false;
        }

        try {
            return this.players.contains(player);
        } finally {
            this.calculatePriority();
        }
    }

    public boolean removePlayer(UUID playerId) {
        final HyggQueuePlayer player = this.getPlayer(playerId);

        return player != null && this.removePlayer(player);
    }

    public boolean removePlayer(HyggQueuePlayer player) {
        if (this.leader != null && this.leader.getUniqueId().equals(player.getUniqueId())) {
            this.leader = null;
        }

        try {
            return this.players.remove(player);
        } finally {
            this.calculatePriority();
        }
    }

    public String getId() {
        return this.id;
    }

    public HyggQueuePlayer getLeader() {
        return this.leader;
    }

    public HyggQueuePlayer getPlayer(UUID uniqueId) {
        for (HyggQueuePlayer player : this.players) {
            if (player.getUniqueId().equals(uniqueId)) {
                return player;
            }
        }
        return null;
    }

    public List<HyggQueuePlayer> getPlayers() {
        return this.players;
    }

    public List<UUID> getPlayersIds() {
        final List<UUID> ids = new ArrayList<>();

        for (HyggQueuePlayer player : this.players) {
            ids.add(player.getUniqueId());
        }
        return ids;
    }

    public int getSize() {
        return this.players.size();
    }

    public int getPriority() {
        return this.priority;
    }

    private void calculatePriority() {
        for (HyggQueuePlayer player : this.players) {
            this.priority = Math.min(player.getPriority(), this.priority);
        }
    }

    public void send(HyggdrasilAPI hyggdrasilAPI, HyggServer server) {
        for (HyggQueuePlayer player : this.players) {
            hyggdrasilAPI.getPacketProcessor().request(HyggChannel.QUEUE, new HyggQueueTransferPacket(player.getUniqueId(), server.getName())).exec();
        }
    }

}
