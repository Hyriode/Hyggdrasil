package fr.hyriode.hyggdrasil.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.core.InvocationBuilder;
import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.service.IHyggService;
import fr.hyriode.hyggdrasil.api.service.IHyggServiceResources;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

public class HyggServiceResources implements IHyggServiceResources {

    private final Hyggdrasil hyggdrasil;
    private final IHyggService service;

    protected long totalCpuUsage;
    protected List<Long> cpuUsages;
    protected long systemCpuUsage;
    protected long availableCpus;

    protected long memoryUsage;
    protected long memoryMax;
    protected long memoryLimit;

    public HyggServiceResources(Hyggdrasil hyggdrasil, IHyggService service) {
        this.hyggdrasil = hyggdrasil;
        this.service = service;
    }

    public IHyggServiceResources fetch() {
        final DockerClient client = this.hyggdrasil.getDocker().getDockerClient();

        InvocationBuilder.AsyncResultCallback<Statistics> callback = new InvocationBuilder.AsyncResultCallback<>();
        client.statsCmd(this.service.getContainerId()).exec(callback);

        try {
            final Statistics stats = callback.awaitResult();
            this.totalCpuUsage = stats.getCpuStats().getCpuUsage().getTotalUsage();
            this.cpuUsages = stats.getCpuStats().getCpuUsage().getPercpuUsage();
            this.systemCpuUsage = stats.getCpuStats().getSystemCpuUsage();
            this.availableCpus = stats.getCpuStats().getOnlineCpus();

            this.memoryUsage = stats.getMemoryStats().getUsage();
            this.memoryMax = stats.getMemoryStats().getMaxUsage();
            this.memoryLimit = stats.getMemoryStats().getLimit();

            callback.close();
        } catch (RuntimeException | IOException e) {
            Hyggdrasil.log(Level.WARNING, "Unable to fetch stats for service " + this.service.getName() + " (" + this.service.getContainerId() + "): " + e.getMessage());
        }

        return this;
    }

    public long getTotalCpuUsage() {
        return this.totalCpuUsage;
    }

    public List<Long> getCpuUsages() {
        return this.cpuUsages;
    }

    public long getSystemCpuUsage() {
        return this.systemCpuUsage;
    }

    public long getAvailableCpus() {
        return this.availableCpus;
    }

    public long getMemoryUsage() {
        return this.memoryUsage;
    }

    public long getMemoryMax() {
        return this.memoryMax;
    }

    public long getMemoryLimit() {
        return this.memoryLimit;
    }
}
