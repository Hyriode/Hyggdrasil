package fr.hyriode.hyggdrasil.api.protocol.heartbeat;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggHeartbeatPacket;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 22/01/2022 at 18:43
 */
public class HyggHeartbeatTask implements Runnable {

    /** The last heartbeat received from Hyggdrasil. This will only work if the application is not Hyggdrasil itself */
    private long lastHeartbeat;
    /** <code>true</code> if Hyggdrasil is responding to heartbeats */
    private boolean responding = false;
    /** <code>true</code> if Hyggdrasil connection has timed out */
    private boolean timedOut = false;

    /** The {@link HyggdrasilAPI} instance */
    private final HyggdrasilAPI hyggdrasilAPI;

    /**
     * Constructor of {@link HyggHeartbeatTask}
     *
     * @param hyggdrasilAPI The {@link HyggdrasilAPI} instance
     */
    public HyggHeartbeatTask(HyggdrasilAPI hyggdrasilAPI) {
        this.hyggdrasilAPI = hyggdrasilAPI;

        HyggdrasilAPI.log("Starting heartbeat task...");

        this.hyggdrasilAPI.getScheduler().schedule(this, 0, HyggdrasilAPI.HEARTBEAT_TIME, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        this.hyggdrasilAPI.getPacketProcessor().request(HyggChannel.getForApplication(this.hyggdrasilAPI.getEnvironment().getApplication()), new HyggHeartbeatPacket())
                .withResponseCallback(response -> {
                    if (!this.timedOut) {
                        HyggdrasilAPI.log("Hyggdrasil is now responding!");
                    }

                    this.timedOut = false;
                    this.responding = true;

                    this.lastHeartbeat = System.currentTimeMillis();
                })
                .withResponseTimeEndCallback(() -> {
                    if (System.currentTimeMillis() - this.lastHeartbeat >= HyggdrasilAPI.TIMED_OUT_TIME) {
                        HyggdrasilAPI.log(Level.SEVERE, "Hyggdrasil is no longer responding! Waiting for a response...");
                        this.timedOut = true;
                    }
                    this.responding = false;
                })
                .exec();
    }

    /**
     * Get the last heartbeat from Hyggdrasil
     *
     * @return A timestamp (in millis)
     */
    public long getLastHeartbeat() {
        return this.lastHeartbeat;
    }

    /**
     * Check if Hyggdrasil is responding
     *
     * @return <code>true</code> if yes
     */
    public boolean isResponding() {
        return this.responding;
    }

    /**
     * Check if Hyggdrasil connection has timed out
     *
     * @return <code>true</code> if yes
     */
    public boolean isTimedOut() {
        return this.timedOut;
    }


}
