package fr.hyriode.hyggdrasil.proxy;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 17/04/2022 at 10:45
 */
public class HyggProxyBalancer {

    private final HyggProxyManager proxyManager;

    public HyggProxyBalancer(Hyggdrasil hyggdrasil, HyggProxyManager proxyManager) {
        this.proxyManager = proxyManager;

        hyggdrasil.getAPI().getScheduler().schedule(this::anticipateProxies, 0, 5, TimeUnit.SECONDS);
    }

    private void anticipateProxies() {
        final List<HyggProxy> proxies = this.proxyManager.getProxies();

        int players = 0;
        for (HyggProxy proxy : proxies) {
            players += proxy.getPlayers();
        }

        final int neededProxies = (int) (Math.ceil((double) players * 1.2 / HyggProxy.MAX_PLAYERS));
        final int currentProxies = proxies.size();

        if (neededProxies > currentProxies) {
            for (int i = 0; i < neededProxies - currentProxies; i++) {
                this.proxyManager.startProxy();
            }
        }
    }

}
