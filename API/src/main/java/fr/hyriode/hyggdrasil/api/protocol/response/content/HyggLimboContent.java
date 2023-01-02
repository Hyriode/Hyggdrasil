package fr.hyriode.hyggdrasil.api.protocol.response.content;

import fr.hyriode.hyggdrasil.api.limbo.HyggLimbo;

/**
 * Created by AstFaster
 * on 14/11/2022 at 20:46
 */
public class HyggLimboContent extends HyggResponseContent {

    private final HyggLimbo limbo;

    public HyggLimboContent(HyggLimbo limbo) {
        this.limbo = limbo;
    }

    public HyggLimbo getLimbo() {
        return this.limbo;
    }

}
