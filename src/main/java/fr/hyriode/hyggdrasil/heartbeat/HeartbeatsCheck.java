package fr.hyriode.hyggdrasil.heartbeat;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.limbo.HyggLimboManager;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;
import fr.hyriode.hyggdrasil.server.HyggServerManager;

import java.util.concurrent.TimeUnit;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 22/01/2022 at 21:28
 */
public class HeartbeatsCheck implements Runnable {

    /** The maximum amount of time to wait for the first heartbeat of an application */
    private static final int MAX_FIRST_HEARTBEAT = 60 * 1000;

    private final HyggProxyManager proxyManager;
    private final HyggServerManager serverManager;
    private final HyggLimboManager limboManager;

    public HeartbeatsCheck(Hyggdrasil hyggdrasil) {
        this.proxyManager = hyggdrasil.getProxyManager();
        this.serverManager = hyggdrasil.getServerManager();
        this.limboManager = hyggdrasil.getLimboManager();

        System.out.println("Starting heartbeats check task...");

        hyggdrasil.getAPI().getExecutorService().scheduleAtFixedRate(this, HyggdrasilAPI.HEARTBEAT_TIME, HyggdrasilAPI.HEARTBEAT_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        final long currentTime = System.currentTimeMillis();

        for (HyggServer server : this.serverManager.getServers()) {
            final long lastHeartbeat = server.getLastHeartbeat();

            if ((lastHeartbeat == -1 && currentTime - server.getStartedTime() <= MAX_FIRST_HEARTBEAT) || this.isResponding(currentTime, lastHeartbeat)) {
                continue;
            }

            final String serverName = server.getName();

            server.setState(HyggServer.State.IDLE);

            this.serverManager.updateServer(server);

            if (this.isTimedOut(currentTime, lastHeartbeat)) {
                System.err.println("'" + serverName + "' timed out! Killing it...");

                this.serverManager.stopServer(serverName);
            }
        }

        for (HyggProxy proxy : this.proxyManager.getProxies()) {
            final long lastHeartbeat = proxy.getLastHeartbeat();

            if ((lastHeartbeat == -1 && currentTime - proxy.getStartedTime() <= MAX_FIRST_HEARTBEAT) || this.isResponding(currentTime, lastHeartbeat)) {
                continue;
            }

            final String proxyName = proxy.getName();

            proxy.setState(HyggProxy.State.IDLE);

            this.proxyManager.updateProxy(proxy);

            if (this.isTimedOut(currentTime, lastHeartbeat)) {
                System.err.println("'" + proxyName + "' timed out! Killing it...");

                this.proxyManager.stopProxy(proxyName);
            }
        }

        for (HyggLimbo limbo : this.limboManager.getLimbos()) {
            final long lastHeartbeat = limbo.getLastHeartbeat();

            if ((lastHeartbeat == -1 && currentTime - limbo.getStartedTime() <= MAX_FIRST_HEARTBEAT) || this.isResponding(currentTime, lastHeartbeat)) {
                continue;
            }

            final String limboName = limbo.getName();

            limbo.setState(HyggLimbo.State.IDLE);

            this.limboManager.updateLimbo(limbo);

            if (this.isTimedOut(currentTime, lastHeartbeat)) {
                System.err.println("'" + limboName + "' timed out! Killing it...");

                this.limboManager.stopLimbo(limboName);
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
