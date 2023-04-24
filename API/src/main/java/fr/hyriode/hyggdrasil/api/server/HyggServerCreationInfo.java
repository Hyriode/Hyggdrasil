package fr.hyriode.hyggdrasil.api.server;

import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/04/2022 at 12:08.<br>
 *
 * The information used to create a server.
 */
public class HyggServerCreationInfo {

    private String type;
    private String gameType = null;
    private String map = null;

    private HyggServer.Accessibility accessibility = HyggServer.Accessibility.PUBLIC;
    private HyggServer.Process process = HyggServer.Process.PERMANENT;

    private HyggData data = new HyggData();

    private int slots = -1;

    private String maxMemory = null;
    private String minMemory = null;
    private double cpus = -1.0D;

    public HyggServerCreationInfo(@NotNull String type) {
        this.type = type;
    }

    @NotNull
    public String getType() {
        return this.type;
    }

    public HyggServerCreationInfo withType(@NotNull String type) {
        this.type = type;
        return this;
    }

    @Nullable
    public String getGameType() {
        return this.gameType;
    }

    public HyggServerCreationInfo withGameType(@Nullable String gameType) {
        this.gameType = gameType;
        return this;
    }

    @Nullable
    public String getMap() {
        return this.map;
    }

    public HyggServerCreationInfo withMap(@Nullable String map) {
        this.map = map;
        return this;
    }

    @NotNull
    public HyggServer.Accessibility getAccessibility() {
        return this.accessibility;
    }

    public HyggServerCreationInfo withAccessibility(@NotNull HyggServer.Accessibility accessibility) {
        this.accessibility = accessibility;
        return this;
    }

    @NotNull
    public HyggServer.Process getProcess() {
        return this.process;
    }

    public HyggServerCreationInfo withProcess(@NotNull HyggServer.Process process) {
        this.process = process;
        return this;
    }

    @NotNull
    public HyggData getData() {
        return this.data;
    }

    public HyggServerCreationInfo withData(@NotNull HyggData data) {
        this.data = data;
        return this;
    }

    public int getSlots() {
        return this.slots;
    }

    public HyggServerCreationInfo withSlots(int slots) {
        this.slots = slots;
        return this;
    }

    public String getMaxMemory() {
        return this.maxMemory;
    }

    public HyggServerCreationInfo withMaxMemory(String maxMemory) {
        this.maxMemory = maxMemory;
        return this;
    }

    public String getMinMemory() {
        return this.minMemory;
    }

    public HyggServerCreationInfo withMinMemory(String minMemory) {
        this.minMemory = minMemory;
        return this;
    }

    public double getCpus() {
        return this.cpus;
    }

    public HyggServerCreationInfo withCpus(double cpus) {
        this.cpus = cpus;
        return this;
    }

}
