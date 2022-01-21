package fr.hyriode.hyggdrasil.api.protocol;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 25/12/2021 at 10:35
 */
public enum HyggChannel {

    QUERY("query"),
    SERVERS("servers"),
    PROXIES("proxies");

    private final String name;

    HyggChannel(String name) {
        this.name = HyggdrasilAPI.PREFIX + name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
