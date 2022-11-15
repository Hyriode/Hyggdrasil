package fr.hyriode.hyggdrasil.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

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
        try (final Stream<Path> stream = Files.walk(sourceDirectory)) {
            stream.forEach(path -> {
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
        if (!Files.exists(path)) {
            return false;
        }

        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteDirectory(Path path) {
        if (!Files.exists(path)) {
            return false;
        }

        try (final Stream<Path> stream = Files.walk(path)) {
            stream.sorted(Comparator.reverseOrder()).forEach(IOUtil::delete);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void save(Path path, String content) {
        try {
            Files.createFile(path);
            Files.writeString(path, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadFile(Path path) {
        final StringBuilder builder = new StringBuilder();

        if (Files.exists(path)) {
            try (final BufferedReader reader = Files.newBufferedReader(path)) {
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

    public static String toHexString(byte[] bytes) {
        final StringBuilder result = new StringBuilder();

        for (byte value : bytes) {
            final String hex = Integer.toHexString(0xFF & value);

            if (hex.length() == 1) {
                result.append('0');
            }

            result.append(hex);
        }
        return result.toString();
    }

}
