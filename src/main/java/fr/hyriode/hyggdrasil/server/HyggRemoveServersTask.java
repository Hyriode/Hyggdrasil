package fr.hyriode.hyggdrasil.server;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.server.HyggServer;
import fr.hyriode.hyggdrasil.rule.HyggServerRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/05/2022 at 20:51
 *
 * This task is used to remove servers with no players on them.<br>
 * It will decrease resources usage if many servers are started in surplus
 */
public class HyggRemoveServersTask implements Runnable {

    private final Hyggdrasil hyggdrasil;
    private final HyggServerManager serverManager;

    public HyggRemoveServersTask(Hyggdrasil hyggdrasil) {
        this.hyggdrasil = hyggdrasil;
        this.serverManager = this.hyggdrasil.getServerManager();

        System.out.println("Starting removing servers task (to avoid servers surplus)");
    }

    @Override
    public void run() {
        /*final Map<String, Map<String, List<HyggServer>>> toShutdown = new HashMap<>();

        for (HyggServer server : this.serverManager.getServers()) {
            if (server.getPlayers().size() != 0) {
                continue;
            }

            final String serverType = server.getType();
            final String gameType = server.getGameType();
            final Map<String, List<HyggServer>> gameTypeServers = toShutdown.getOrDefault(serverType, new HashMap<>());
            final List<HyggServer> servers = gameTypeServers.getOrDefault(gameType, new ArrayList<>());

            servers.add(server);

            gameTypeServers.put(gameType, servers);
            toShutdown.put(serverType, gameTypeServers);
        }

        for (Map.Entry<String, Map<String, List<HyggServer>>> toShutdownEntry : toShutdown.entrySet()) {
            final String serverType = toShutdownEntry.getKey();
            final HyggServerRule serverRule = this.hyggdrasil.getRules().getServerRules().get(serverType);

            for (Map.Entry<String, List<HyggServer>> entry : toShutdownEntry.getValue().entrySet()) {
                final List<HyggServer> servers = entry.getValue();

                if (servers.size() <= 1 || (serverRule != null && serverRule.getMinimums().get(entry.getKey()) <= servers.size())) {
                    continue;
                }

                for (HyggServer server : servers) {
                    if (!server.isAccessible() || server.getPlayers().size() != 0) {
                        continue;
                    }

                    this.serverManager.stopServer(server.getName());
                }
            }
        }*/
    }

}
