package fr.hyriode.hyggdrasil.api.protocol.environment;

import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 21/01/2022 at 20:14
 */
public class HyggKeys {

    /** A simple function that applies a {@link String} value and returns it with a prefix and in uppercase */
    private static final Function<String, String> ENV = value -> (HyggdrasilAPI.PREFIX + "_key_" + value).toUpperCase();
    /** The public key environment variable key */
    public static final String PUBLIC_KEY_ENV = ENV.apply("public");

    /** The key used to verify the received messages */
    private final PublicKey publicKey;
    /** The key used to sign the messages */
    private final PrivateKey privateKey;

    /**
     * Constructor of {@link HyggKeys}
     *
     * @param publicKey The public key
     * @param privateKey The private key
     */
    public HyggKeys(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    /**
     * Get the public key
     *
     * @return A {@link PublicKey} object
     */
    public PublicKey getPublic() {
        return this.publicKey;
    }

    /**
     * Get the private key
     *
     * @return A {@link PrivateKey} object
     */
    public PrivateKey getPrivate() {
        return this.privateKey;
    }

    /**
     * Load keys from environment variables if they are set.<br>
     * In most cases, Hyggdrasil will automatically provide them if the application was started by it.
     *
     * @return {@link HyggApplication} object
     */
    static HyggKeys loadFromEnvironmentVariables() {
        HyggdrasilAPI.log("Loading keys from environment variables...");
        HyggdrasilAPI.log("Reading public key...");

        try {
            return new HyggKeys(KeyFactory.getInstance(HyggdrasilAPI.ALGORITHM.getFamilyName()).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(System.getenv(PUBLIC_KEY_ENV)))), null);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            HyggdrasilAPI.log(Level.SEVERE, "Failed to read public key!");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create environment variables list from keys
     *
     * @return A list of string
     */
    List<String> createEnvironmentVariables() {
        final List<String> variables = new ArrayList<>();

        variables.add(PUBLIC_KEY_ENV + "=" + Base64.getEncoder().encodeToString(this.publicKey.getEncoded()));

        return variables;
    }

}
