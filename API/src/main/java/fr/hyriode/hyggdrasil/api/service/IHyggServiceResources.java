package fr.hyriode.hyggdrasil.api.service;

import java.util.List;

public interface IHyggServiceResources {

    IHyggServiceResources fetch();

    long getTotalCpuUsage();
    List<Long> getCpuUsages();
    long getSystemCpuUsage();
    long getAvailableCpus();

    long getMemoryUsage();
    long getMemoryMax();
    long getMemoryLimit();
}
