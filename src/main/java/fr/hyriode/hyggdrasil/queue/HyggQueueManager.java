package fr.hyriode.hyggdrasil.queue;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.queue.HyggQueueGroup;
import fr.hyriode.hyggdrasil.api.queue.HyggQueuePlayer;
import fr.hyriode.hyggdrasil.api.queue.packet.*;
import fr.hyriode.hyggdrasil.server.HyggServerManager;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 09:10
 */
public class HyggQueueManager {

    private final Map<String, HyggQueue> queues;

    private final HyggServerManager serverManager;
    private final Hyggdrasil hyggdrasil;

    public HyggQueueManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.serverManager = this.hyggdrasil.getServerManager();
        this.queues = new ConcurrentHashMap<>();
        this.hyggdrasil.getAPI().getPacketProcessor().registerReceiver(HyggChannel.QUEUE, new HyggQueueReceiver(this));
    }

    public void disable() {
        for (HyggQueue queue : this.queues.values()) {
            queue.disable();
        }
    }

    public HyggResponse handlePacket(HyggQueueAddPlayerPacket packet) {
        final HyggQueuePlayer player = packet.getPlayer();

        return this.handleAddPacket(packet, new HyggQueueGroup(player.getUniqueId().toString(), player, new ArrayList<>()));
    }

    public HyggResponse handlePacket(HyggQueueAddGroupPacket packet) {
        return this.handleAddPacket(packet, packet.getGroup());
    }

    private HyggResponse handleAddPacket(HyggQueueAddPacket packet, HyggQueueGroup group) {
        final HyggResponse response = new HyggResponse(HyggResponse.Type.SUCCESS);
        final HyggQueue queue = this.getQueue(packet.getGame(), packet.getGameType(), packet.getMap());
        final HyggQueue currentQueue = this.getCurrentQueue(group.getId());

        if (queue.equals(currentQueue)) {
            response.withType(HyggResponse.Type.ERROR).withContent(HyggQueueAddPacket.Response.ALREADY_IN.asContent());
        } else if (!this.serverManager.isTypeExisting(packet.getGame())) {
            response.withType(HyggResponse.Type.ERROR).withContent(HyggQueueAddPacket.Response.INVALID_TYPE.asContent());
        } else {
            queue.addGroup(group);

            response.withContent(HyggQueueAddPacket.Response.ADDED.asContent());
        }
        return response;
    }

    public HyggResponse handlePacket(HyggQueueRemovePlayerPacket packet) {
        final UUID playerId = packet.getPlayerId();
        final HyggResponse response = new HyggResponse(HyggResponse.Type.SUCCESS).withContent(HyggQueueRemovePacket.Response.REMOVED.asContent());
        final HyggQueue queue = this.getCurrentQueue(playerId);

        if (queue == null) {
            response.withType(HyggResponse.Type.ERROR).withContent(HyggQueueRemovePacket.Response.NOT_IN_QUEUE.asContent());
        } else if (!queue.removePlayer(playerId)) {
            response.withType(HyggResponse.Type.ERROR).withContent(HyggQueueRemovePacket.Response.UNKNOWN.asContent());
        }
        return response;
    }

    public HyggResponse handlePacket(HyggQueueRemoveGroupPacket packet) {
        final String groupId = packet.getGroupId();
        final HyggResponse response = new HyggResponse(HyggResponse.Type.SUCCESS).withContent(HyggQueueRemovePacket.Response.REMOVED.asContent());
        final HyggQueue queue = this.getCurrentQueue(groupId);

        if (queue == null) {
            response.withType(HyggResponse.Type.ERROR).withContent(HyggQueueRemovePacket.Response.NOT_IN_QUEUE.asContent());
        } else if (!queue.removeGroup(groupId)) {
            response.withType(HyggResponse.Type.ERROR).withContent(HyggQueueRemovePacket.Response.UNKNOWN.asContent());
        }
        return response;
    }

    private HyggQueue getCurrentQueue(String groupId) {
        for (HyggQueue queue : this.queues.values()) {
            if (queue.containsGroup(groupId)) {
                return queue;
            }
        }
        return null;
    }

    private HyggQueue getCurrentQueue(UUID playerId) {
        for (HyggQueue queue : this.queues.values()) {
            if (queue.containsPlayer(playerId)) {
                return queue;
            }
        }
        return null;
    }

    private HyggQueue getQueue(String game, String gameType, String map) {
        final String queueName = this.createQueueName(game, gameType, map);

        HyggQueue queue = this.queues.get(queueName);

        if (queue == null) {
            queue = this.createQueue(game, gameType, map);
        }

        return queue;
    }

    public HyggQueue createQueue(String game, String gameType, String map) {
        final HyggQueue queue = new HyggQueue(this.hyggdrasil, game, gameType, map);

        this.queues.put(this.createQueueName(game, gameType, map), queue);

        return queue;
    }

    private String createQueueName(String game, String gameType, String map) {
        return game + "@" + gameType + "@" + map;
    }

}
