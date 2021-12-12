package fr.hyriode.hyggdrasil.api.server;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 16:19
 */
public class HyggServerOptions {

    /** All the options */
    private boolean pvp = true;
    private boolean nether = false;
    private boolean broadcastAchievements = false;
    private boolean flight = false;
    private String difficulty = "normal";
    private int spawnProtection = 0;

    /**
     * Getters and setters of options
     */

    public boolean isPvp() {
        return this.pvp;
    }

    public void setPvp(boolean pvp) {
        this.pvp = pvp;
    }

    public boolean isNether() {
        return this.nether;
    }

    public void setNether(boolean nether) {
        this.nether = nether;
    }

    public boolean isBroadcastAchievements() {
        return this.broadcastAchievements;
    }

    public void setBroadcastAchievements(boolean broadcastAchievements) {
        this.broadcastAchievements = broadcastAchievements;
    }

    public boolean isFlight() {
        return this.flight;
    }

    public void setFlight(boolean flight) {
        this.flight = flight;
    }

    public String getDifficulty() {
        return this.difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getSpawnProtection() {
        return this.spawnProtection;
    }

    public void setSpawnProtection(int spawnProtection) {
        this.spawnProtection = spawnProtection;
    }

}
