package fr.hyriode.hyggdrasil;

import fr.hyriode.hyggdrasil.util.References;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/10/2021 at 10:11
 */
public class HyggBootstrap {

    public static void main(String[] args) {
        if (Float.parseFloat(System.getProperty("java.class.version")) < 60.0D) {
            System.err.println("*** ERROR *** " + References.NAME + " requires Java >= 16 to function!");
            return;
        }

        new Hyggdrasil().start();
    }

}
