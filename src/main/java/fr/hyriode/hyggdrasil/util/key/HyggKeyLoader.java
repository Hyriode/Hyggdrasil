package fr.hyriode.hyggdrasil.util.key;

import fr.hyriode.hyggdrasil.Hyggdrasil;
import fr.hyriode.hyggdrasil.api.HyggdrasilAPI;
import fr.hyriode.hyggdrasil.api.protocol.environment.HyggKeys;
import fr.hyriode.hyggdrasil.api.protocol.signature.HyggSignatureAlgorithm;
import fr.hyriode.hyggdrasil.util.References;

import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.logging.Level;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 16/02/2022 at 10:41
 */
public class HyggKeyLoader {

    public static HyggKeys loadKeys() {
        final HyggSignatureAlgorithm algorithm = HyggdrasilAPI.ALGORITHM;

        PrivateKey privateKey = null;
        PublicKey publicKey = null;
        if (Files.exists(References.PRIVATE_KEY_FILE)) {
            try {
                final KeyFactory keyFactory = KeyFactory.getInstance(algorithm.getFamilyName());

                privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Files.readAllBytes(References.PRIVATE_KEY_FILE)));

                System.out.println("Private key read from its file.");

                final RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey) privateKey;
                final RSAPublicKeySpec keySpec = new RSAPublicKeySpec(rsaPrivateCrtKey.getModulus(), rsaPrivateCrtKey.getPublicExponent());

                publicKey = keyFactory.generatePublic(keySpec);

                System.out.println("Public key generated from the private one.");
            } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
                Hyggdrasil.log(Level.SEVERE, "An error occurred while reading private key file! Deleting file...");

                try {
                    Files.delete(References.PRIVATE_KEY_FILE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                e.printStackTrace();
            }
        } else {
            System.out.println("Generating key pair...");

            try {
                final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm.getFamilyName());

                keyPairGenerator.initialize(algorithm.getMinimalKeyLength());

                final KeyPair keyPair = keyPairGenerator.generateKeyPair();

                privateKey = keyPair.getPrivate();
                publicKey = keyPair.getPublic();

                Files.write(References.PRIVATE_KEY_FILE, privateKey.getEncoded());
            } catch (NoSuchAlgorithmException e) {
                Hyggdrasil.log(Level.SEVERE, "An error occurred while generating new key pair!");
                e.printStackTrace();
            } catch (IOException e) {
                Hyggdrasil.log(Level.SEVERE, "An error occurred while writing private key in file!");
                e.printStackTrace();
            }
        }
        return new HyggKeys(publicKey, privateKey);
    }

}
