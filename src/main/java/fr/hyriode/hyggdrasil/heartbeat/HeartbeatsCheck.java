package fr.hyriode.hyggdrasil.heartbeat;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;
import fr.hyriode.hyggdrasil.server.HyggServerManager;

import java.util.concurrent.TimeUnit;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 22/01/2022 at 21:28
 */
public class HeartbeatsCheck implements Runnable {

    private final HyggProxyManager proxyManager;
    private final HyggServerManager serverManager;

    public HeartbeatsCheck(Hyggdrasil hyggdrasil) {
        this.proxyManager = hyggdrasil.getProxyManager();
        this.serverManager = hyggdrasil.getServerManager();

        System.out.println("Starting heartbeats check task...");

        hyggdrasil.getAPI().getExecutorService().scheduleAtFixedRate(this, 0, HyggdrasilAPI.HEARTBEAT_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        final long currentTime = System.currentTimeMillis();

        for (HyggServer server : this.serverManager.getServers()) {
            final HyggServer.State state = server.getState();
            final long lastHeartbeat = server.getLastHeartbeat();

            if (state == HyggServer.State.SHUTDOWN || lastHeartbeat == -1 || this.isResponding(currentTime, lastHeartbeat)) {
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
            final HyggProxy.State state = proxy.getState();

            if (state == HyggProxy.State.SHUTDOWN  || lastHeartbeat == -1 || this.isResponding(currentTime, lastHeartbeat)) {
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
    }

    private boolean isTimedOut(long currentTime, long lastHeartbeat) {
        return currentTime - lastHeartbeat >= HyggdrasilAPI.TIMED_OUT_TIME;
    }

    private boolean isResponding(long currentTime, long lastHeartbeat) {
        return currentTime - lastHeartbeat <= HyggdrasilAPI.HEARTBEAT_TIME;
    }

}
