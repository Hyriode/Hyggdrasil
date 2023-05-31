package fr.hyriode.hyggdrasil.util;

import java.nio.file.Path;
import java.nio.file.Paths;

public class References {

    /** Global */
    public static final String NAME = "Hyggdrasil";

    /** Files */
    public static final Path LOG_FOLDER = Paths.get("logs");
    public static final Path LOG_FILE = Paths.get(LOG_FOLDER.toString(), "hyggdrasil.log");

    public static final Path TMP_FOLDER = Paths.get("tmp");

    public static final Path TEMPLATES_FOLDER = Paths.get("templates");

    public static final Path SERVERS_FOLDER = Paths.get("servers");
    public static final Path PROXIES_FOLDER = Paths.get("proxies");
    public static final Path LIMBOS_FOLDER = Paths.get("limbos");


}
