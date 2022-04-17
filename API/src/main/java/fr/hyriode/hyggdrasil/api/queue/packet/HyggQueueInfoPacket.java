package fr.hyriode.hyggdrasil.api.queue.packet;

import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacket;
import fr.hyriode.hyggdrasil.api.queue.HyggQueuePlayer;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 17/04/2022 at 10:16
 */
public class HyggQueueInfoPacket extends HyggPacket {

    private final HyggQueuePlayer player;
    private final String game;
    private final String gameType;
    private final String map;

    private int place;
    private int groupSize;
    private int queueSize;
    private int groupsInQueue;

    public HyggQueueInfoPacket(HyggQueuePlayer player, String game, String gameType, String map) {
        this.player = player;
        this.game = game;
        this.gameType = gameType;
        this.map = map;
    }

    public HyggQueuePlayer getPlayer() {
        return this.player;
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

    public int getPlace() {
        return this.place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public int getGroupSize() {
        return this.groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getGroupsInQueue() {
        return this.groupsInQueue;
    }

    public void setGroupsInQueue(int groupsInQueue) {
        this.groupsInQueue = groupsInQueue;
    }

}
