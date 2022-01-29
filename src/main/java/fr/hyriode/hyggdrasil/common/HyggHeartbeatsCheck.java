package fr.hyriode.hyggdrasil.common;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxyState;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.api.server.HyggServerState;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;
import fr.hyriode.hyggdrasil.server.HyggServerManager;

import java.util.concurrent.TimeUnit;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 22/01/2022 at 21:28
 */
public class HyggHeartbeatsCheck implements Runnable {

    private final HyggProxyManager proxyManager;
    private final HyggServerManager serverManager;

    private final Hyggdrasil hyggdrasil;

    public HyggHeartbeatsCheck(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.proxyManager = this.hyggdrasil.getProxyManager();
        this.serverManager = this.hyggdrasil.getServerManager();

        System.out.println("Starting heartbeats check task...");

        this.hyggdrasil.getAPI().getScheduler().schedule(this, 0, HyggdrasilAPI.HEARTBEAT_TIME, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        final long currentTime = System.currentTimeMillis();

        for (HyggServer server : this.serverManager.getServers()) {
            if (server.getState() != HyggServerState.CREATING) {
                final long lastHeartbeat = server.getLastHeartbeat();

                if (!this.isResponding(currentTime, lastHeartbeat)) {
                    final String serverName = server.getName();

                    server.setState(HyggServerState.IDLE);

                    System.err.println("'" + serverName + "' didn't send a heartbeat!");

                    if (this.isTimedOut(currentTime, lastHeartbeat)) {
                        System.err.println("'" + serverName + "' timed out! Killing it...");

                        this.serverManager.stopServer(serverName);
                    }
                }
            }
        }

        for (HyggProxy proxy : this.proxyManager.getProxies()) {
            if (proxy.getState() != HyggProxyState.CREATING) {
                final long lastHeartbeat = proxy.getLastHeartbeat();

                if (!this.isResponding(currentTime, lastHeartbeat)) {
                    final String proxyName = proxy.getName();

                    proxy.setState(HyggProxyState.IDLE);

                    System.err.println("'" + proxyName + "' didn't send a heartbeat!");

                    if (this.isTimedOut(currentTime, lastHeartbeat)) {
                        System.err.println("'" + proxyName + "' timed out! Killing it...");

                        this.proxyManager.stopProxy(proxyName);
                    }
                }
            }
        }
    }

    private boolean isTimedOut(long currentTime, long lastHeartbeat) {
        return currentTime - lastHeartbeat >= HyggdrasilAPI.TIMED_OUT_TIME;
    }

    private boolean isResponding(long currentTime, long lastHeartbeat) {
        return currentTime - lastHeartbeat >= HyggdrasilAPI.HEARTBEAT_TIME;
    }

}
