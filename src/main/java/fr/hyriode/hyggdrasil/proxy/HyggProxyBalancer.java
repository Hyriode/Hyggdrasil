package fr.hyriode.hyggdrasil.proxy;

import com.google.gson.JsonObject;
import eu.roboflax.cloudflare.CloudflareAccess;
import eu.roboflax.cloudflare.CloudflareRequest;
import eu.roboflax.cloudflare.http.HttpMethod;
import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.proxy.HyggProxy;
import fr.hyriode.hyggdrasil.proxy.HyggProxyManager;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 17/04/2022 at 10:45
 */
public class HyggProxyBalancer {

    private static final String ZONE_ID = System.getenv("CLOUDFLARE_ZONE_ID");
    private static final String DNS_ID = System.getenv("CLOUDFLARE_DNS_ID");
    private static final String DOMAIN_NAME = System.getenv("DOMAIN_NAME");

    private String currentProxy;

    private final HyggProxyManager proxyManager;
    private final CloudflareAccess cloudflareAccess;

    public HyggProxyBalancer(Hyggdrasil hyggdrasil, HyggProxyManager proxyManager) {
        this.proxyManager = proxyManager;
        this.cloudflareAccess = new CloudflareAccess(System.getenv("CLOUDFLARE_TOKEN"), Executors.newCachedThreadPool());

        hyggdrasil.getAPI().getScheduler().schedule(this::balance, 0, 5, TimeUnit.SECONDS);
    }

    public void balance() {
        this.anticipateProxies();

        final HyggProxy proxy = this.proxyManager.getBestProxy();

        if (proxy != null) {
            if (!proxy.equals(this.proxyManager.getProxyByName(this.currentProxy))) {
                System.out.println("Switching proxy: " + this.currentProxy + " -> " + proxy.getName());

                this.currentProxy = proxy.getName();

                this.updateDNS(proxy.getPort());
            }
        }
    }

    public void stop() {
        System.out.println("Stopping proxy balancer...");

        this.cloudflareAccess.close();
    }

    private void anticipateProxies() {
        final List<HyggProxy> proxies = this.proxyManager.getProxies();

        int players = 0;
        for (HyggProxy proxy : proxies) {
            players += proxy.getPlayers();
        }

        final int neededProxies = (int) (Math.ceil((double) players * 1.3 / HyggProxy.MAX_PLAYERS));
        final int currentProxies = proxies.size();

        if (neededProxies > currentProxies) {
            for (int i = 0; i < neededProxies - currentProxies; i++) {
                this.proxyManager.startProxy();
            }
        }
    }

    private void updateDNS(int port) {
        new CloudflareRequest("zones/" + ZONE_ID + "/dns_records/" + DNS_ID, this.cloudflareAccess)
                .httpMethod(HttpMethod.PUT)
                .body("type", "SRV")
                .body("data", this.createDNSData(port))
                .sendAsync();
        System.out.println();
    }

    private JsonObject createDNSData(int port) {
        final JsonObject object = new JsonObject();

        object.addProperty("service", "_minecraft");
        object.addProperty("proto", "_tcp");
        object.addProperty("name", DOMAIN_NAME);
        object.addProperty("priority", 1);
        object.addProperty("weight", 5);
        object.addProperty("port", port);
        object.addProperty("target", DOMAIN_NAME);

        return object;
    }

    public String getCurrentProxy() {
        return this.currentProxy;
    }

}
