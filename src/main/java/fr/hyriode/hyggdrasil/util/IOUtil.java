package fr.hyriode.hyggdrasil.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 26/01/2022 at 15:46
 */
public class IOUtil {

    public static boolean createDirectory(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static boolean copy(Path source, Path target) {
        try {
            Files.copy(source, target);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean copyContent(Path sourceDirectory, Path targetDirectory) {
        try {
            Files.walk(sourceDirectory).forEach(path -> {
                if (!path.toString().equals(sourceDirectory.toString())) {
                    copy(path, Paths.get(targetDirectory.toString(), path.toString().substring(sourceDirectory.toString().length())));
                }
            });
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean delete(Path path) {
        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteDirectory(Path path) {
        try {
            Files.walk(path).sorted(Comparator.reverseOrder()).forEach(IOUtil::delete);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
