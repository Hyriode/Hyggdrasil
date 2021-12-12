package fr.hyriode.hyggdrasil.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 11/12/2021 at 16:04
 */
public class PortUtil {

    public static int nextAvailablePort(int from, int to) {
        int port = randomPort(from, to);

        while (!isPortAvailable(port)) {
            port = randomPort(from, to);
        }

        return port;
    }

    private static int randomPort(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to);
    }

    public static boolean isPortAvailable(int port) {
        try {
            new ServerSocket(port).close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
