package fr.hyriode.hyggdrasil.rule;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 14/05/2022 at 14:57
 */
public class HyggServerRule {

    private final Map<String, Integer> minimums;

    public HyggServerRule() {
        this.minimums = new HashMap<>();
    }

    public HyggServerRule addMinimum(String type, int minimum) {
        this.minimums.put(type, minimum);
        return this;
    }

    public Map<String, Integer> getMinimums() {
        return this.minimums;
    }

}
