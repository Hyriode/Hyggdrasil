package fr.hyriode.hyggdrasil.api.queue;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 10/05/2022 at 16:32
 */
public class HyggQueueInfo {

    private final String game;
    private final String gameType;
    private final String map;

    private final int totalGroups;
    private final int totalPlayers;

    public HyggQueueInfo(String game, String gameType, String map, int totalGroups, int totalPlayers) {
        this.game = game;
        this.gameType = gameType;
        this.map = map;
        this.totalGroups = totalGroups;
        this.totalPlayers = totalPlayers;
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

    public int getTotalGroups() {
        return this.totalGroups;
    }

    public int getTotalPlayers() {
        return this.totalPlayers;
    }

}
