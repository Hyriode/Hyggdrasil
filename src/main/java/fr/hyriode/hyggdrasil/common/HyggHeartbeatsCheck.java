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

    public HyggHeartbeatsCheck(Hyggdrasil hyggdrasil) {
        this.proxyManager = hyggdrasil.getProxyManager();
        this.serverManager = hyggdrasil.getServerManager();

        System.out.println("Starting heartbeats check task...");

        hyggdrasil.getAPI().getScheduler().schedule(this, 0, HyggdrasilAPI.HEARTBEAT_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        final long currentTime = System.currentTimeMillis();

        for (HyggServer server : this.serverManager.getServers()) {
            final long lastHeartbeat = server.getLastHeartbeat();

            if (server.getState() == HyggServerState.CREATING || lastHeartbeat == -1 || this.isResponding(currentTime, lastHeartbeat)) {
                continue;
            }

            final String serverName = server.getName();

            server.setState(HyggServerState.IDLE);

            this.serverManager.updateServer(server);

            System.err.println("'" + serverName + "' didn't send a heartbeat!");

            if (this.isTimedOut(currentTime, lastHeartbeat)) {
                System.err.println("'" + serverName + "' timed out! Killing it...");

                this.serverManager.stopServer(serverName);
            }
        }

        for (HyggProxy proxy : this.proxyManager.getProxies()) {
            final long lastHeartbeat = proxy.getLastHeartbeat();

            if (proxy.getState() == HyggProxyState.CREATING || this.isResponding(currentTime, lastHeartbeat)) {
                continue;
            }

            final String proxyName = proxy.getName();

            proxy.setState(HyggProxyState.IDLE);

            this.proxyManager.updateProxy(proxy);

            System.err.println("'" + proxyName + "' didn't send a heartbeat!");

            if (this.isTimedOut(currentTime, lastHeartbeat)) {
                System.err.println("'" + proxyName + "' timed out! Killing it...");

                this.proxyManager.stopProxy(proxyName);
            }
        }
    }

    private boolean isTimedOut(long currentTime, long lastHeartbeat) {
        return currentTime - lastHeartbeat >= HyggdrasilAPI.TIMED_OUT_TIME;
    }

    private boolean isResponding(long currentTime, long lastHeartbeat) {
        return currentTime - lastHeartbeat <= HyggdrasilAPI.HEARTBEAT_TIME;
    }

}
