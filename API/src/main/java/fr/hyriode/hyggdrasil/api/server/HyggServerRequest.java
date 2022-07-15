package fr.hyriode.hyggdrasil.api.server;

import fr.hyriode.hyggdrasil.api.protocol.environment.HyggData;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 12:08
 */
public class HyggServerRequest {

    private String serverType;
    private String gameType;
    private String map;

    private HyggServerOptions serverOptions;
    private HyggData serverData;

    public HyggServerRequest(String serverType, String gameType, String map, HyggData serverData) {
        this.serverType = serverType;
        this.gameType = gameType;
        this.map = map;
        this.serverOptions = new HyggServerOptions();
        this.serverData = serverData;

        this.serverData.add(HyggServer.SUB_TYPE_KEY, this.gameType);
        this.serverData.add(HyggServer.MAP_KEY, this.map);
    }

    public HyggServerRequest(String serverType, String gameType, String map) {
        this(serverType, gameType, map, new HyggData());
    }

    public HyggServerRequest(String type, String map) {
        this(type, null, map);
    }

    public HyggServerRequest() {}

    public String getServerType() {
        return this.serverType;
    }

    public HyggServerRequest withServerType(String serverType) {
        this.serverType = serverType;
        return this;
    }

    public String getGameType() {
        return this.gameType;
    }

    public HyggServerRequest withGameType(String gameType) {
        this.gameType = gameType;
        this.serverData.add(HyggServer.SUB_TYPE_KEY, this.gameType);
        return this;
    }

    public String getMap() {
        return this.map;
    }

    public HyggServerRequest withMap(String map) {
        this.map = map;
        this.serverData.add(HyggServer.MAP_KEY, this.map);
        return this;
    }

    public HyggServerOptions getServerOptions() {
        return this.serverOptions;
    }

    public HyggServerRequest withServerOptions(HyggServerOptions serverOptions) {
        this.serverOptions = serverOptions;
        return this;
    }

    public HyggData getServerData() {
        return this.serverData;
    }

    public HyggServerRequest withServerData(HyggData serverData) {
        this.serverData = serverData;
        return this;
    }

}
