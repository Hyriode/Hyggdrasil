package fr.hyriode.hyggdrasil;

import fr.hyriode.hyggdrasil.util.References;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 10:11
 */
public class HyggBootstrap {

    public static void main(String[] args) {
        if (Float.parseFloat(System.getProperty("java.class.version")) < 62.0F) {
            System.err.println("*** ERROR *** " + References.NAME + " requires Java >= 18 to work!");
            return;
        }

        new Hyggdrasil().start();
    }

}
