package fr.hyriode.hyggdrasil.limbo;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.event.HyggEventBus;
import fr.hyriode.hyggdrasil.api.event.model.limbo.HyggLimboStartedEvent;
import fr.hyriode.hyggdrasil.api.event.model.limbo.HyggLimboStoppedEvent;
import fr.hyriode.hyggdrasil.api.event.model.limbo.HyggLimboUpdatedEvent;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;
import fr.hyriode.hyggdrasil.api.limbo.HyggLimbosRequester;
import fr.hyriode.hyggdrasil.api.limbo.packet.HyggLimboInfoPacket;
import fr.hyriode.hyggdrasil.api.limbo.packet.HyggStopLimboPacket;
import fr.hyriode.hyggdrasil.api.protocol.HyggChannel;
import fr.hyriode.hyggdrasil.api.protocol.data.HyggData;
import fr.hyriode.hyggdrasil.api.protocol.packet.HyggPacketProcessor;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse;
import fr.hyriode.hyggdrasil.api.protocol.response.HyggResponseCallback;
import fr.hyriode.hyggdrasil.docker.image.DockerImage;
import fr.hyriode.hyggdrasil.docker.swarm.DockerSwarm;
import fr.hyriode.hyggdrasil.template.HyggTemplate;
import fr.hyriode.hyggdrasil.util.References;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static fr.hyriode.hyggdrasil.api.protocol.response.HyggResponse.Type.SUCCESS;

/**
 * Created by AstFaster
 * on 31/12/2022 at 18:00
 */
public class HyggLimboManager {

    private final Map<String, HyggLimbo> limbos = new ConcurrentHashMap<>();

    private final HyggTemplate limboTemplate;
    private final DockerImage limboImage;

    private final DockerSwarm swarm;
    private final HyggPacketProcessor packetProcessor;
    private final HyggEventBus eventBus;

    private final Hyggdrasil hyggdrasil;

    public HyggLimboManager(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.swarm = this.hyggdrasil.getDocker().getSwarm();
        this.packetProcessor = this.hyggdrasil.getAPI().getPacketProcessor();
        this.eventBus = this.hyggdrasil.getAPI().getEventBus();
        this.limboTemplate = this.hyggdrasil.getTemplateManager().getTemplate(Hyggdrasil.getConfig().getLimbos().getTemplate());
        this.limboImage = this.hyggdrasil.getDocker().getImageManager().getImage(Hyggdrasil.getConfig().getLimbos().getImage());

        for (HyggLimbo limbo : this.hyggdrasil.getAPI().getLimbosRequester().fetchLimbos()) {
            this.limbos.put(limbo.getName(), limbo);
        }
    }

    public HyggLimbo startLimbo(HyggLimbo.Type type, HyggData data) {
        final HyggLimbo limbo = new HyggLimbo(type, data);
        final String limboName = limbo.getName();

        this.hyggdrasil.getTemplateManager().getDownloader().copyFiles(this.limboTemplate, Paths.get(References.LIMBOS_FOLDER.toString(), limboName));
        this.swarm.runService(new HyggLimboService(limbo, this.limboImage));
        this.limbos.put(limboName, limbo);
        this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.set(HyggLimbosRequester.REDIS_KEY + limbo.getName(), HyggdrasilAPI.GSON.toJson(limbo))); // Save limbo in Redis cache
        this.eventBus.publish(new HyggLimboStartedEvent(limbo));

        System.out.println("Started '" + limboName + "'.");

        return limbo;
    }

    public void updateLimboInfo(HyggLimbo limbo, HyggLimboInfoPacket packet) {
        final HyggLimbo info = packet.getLimbo();

        limbo.setData(info.getData());
        limbo.setState(info.getState());
        limbo.setPlayers(info.getPlayers());

        this.updateLimbo(limbo);
    }

    public void updateLimbo(HyggLimbo limbo) {
        this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.set(HyggLimbosRequester.REDIS_KEY + limbo.getName(), HyggdrasilAPI.GSON.toJson(limbo))); // Save limbo in Redis cache
        this.eventBus.publish(new HyggLimboUpdatedEvent(limbo)); // Keep applications aware of the update
    }

    public boolean stopLimbo(String name) {
        final HyggLimbo limbo = this.getLimbo(name);

        if (limbo != null) {
            final Runnable action = () -> {
                this.eventBus.publish(new HyggLimboStoppedEvent(limbo));
                this.swarm.removeService(name);
                this.hyggdrasil.getAPI().redisProcess(jedis -> jedis.del(HyggLimbosRequester.REDIS_KEY + limbo.getName()));

                System.out.println("Stopped '" + name + "'.");
            };
            final HyggResponseCallback callback = response -> {
                final HyggResponse.Type type = response.getType();

                if (type != SUCCESS) {
                    System.err.println("'" + name + "' doesn't want to stop or an error occurred! Response: " + type + ". Forcing it to stop...");
                }

                action.run();
            };

            limbo.setState(HyggLimbo.State.SHUTDOWN);

            this.packetProcessor.request(HyggChannel.PROXIES, new HyggStopLimboPacket(name))
                    .withResponseCallback(callback)
                    .withResponseTimeEndCallback(() -> {
                        System.err.println("'" + name + "' didn't respond to the stop packet sent! Forcing it to stop...");

                        action.run();
                    })
                    .exec();

            return true;
        } else {
            System.err.println("Couldn't stop a limbo with the following name: '" + name + "'!");
        }
        return false;
    }

    public HyggLimbo getLimbo(String limbo) {
        return this.limbos.get(limbo);
    }

    public Set<HyggLimbo> getLimbos() {
        return Set.copyOf(this.limbos.values());
    }

}
