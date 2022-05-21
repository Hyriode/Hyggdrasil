package fr.hyriode.hyggdrasil.rule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.hyriode.hyggdrasil.config.HyggConfig;
import fr.hyriode.hyggdrasil.config.nested.HyggDockerConfig;
import fr.hyriode.hyggdrasil.config.nested.HyggRedisConfig;
import fr.hyriode.hyggdrasil.util.IOUtil;
import fr.hyriode.hyggdrasil.util.References;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/05/2022 at 15:10
 */
public class HyggRules {

    public static final Path RULES_FILE = Paths.get(References.DATA_FOLDER.toString(), "rules.json");

    private final Map<String, HyggServerRule> serverRules;
    private final HyggProxyRule proxyRule;

    public HyggRules() {
        this.serverRules = new HashMap<>();
        this.proxyRule = new HyggProxyRule(1, 1, 20001);

        this.addServerRule("example", new HyggServerRule().addMinimum("example-type", 5));
    }

    public HyggRules addServerRule(String serverType, HyggServerRule rule) {
        this.serverRules.put(serverType, rule);
        return this;
    }

    public Map<String, HyggServerRule> getServerRules() {
        return this.serverRules;
    }

    public HyggProxyRule getProxyRule() {
        return this.proxyRule;
    }

    public static HyggRules load() {
        System.out.println("Loading Hyggdrasil rules (proxies/servers)...");

        final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();

        final String json = IOUtil.loadFile(RULES_FILE);

        if (!json.equals("")) {
            return gson.fromJson(json, HyggRules.class);
        } else {
            final HyggRules rules = new HyggRules();

            IOUtil.save(RULES_FILE, gson.toJson(rules));

            System.err.println("Please fill Hyggdrasil rules file before continue!");
            System.exit(0);

            return rules;
        }
    }

}
