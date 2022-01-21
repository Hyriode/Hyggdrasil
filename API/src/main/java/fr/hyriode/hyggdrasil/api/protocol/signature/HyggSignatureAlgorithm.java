package fr.hyriode.hyggdrasil.api.protocol.signature;

/**
 * Project: Hyggdrasil
 * Created by AstFaster
 * on 21/01/2022 at 21:51
 */
public enum HyggSignatureAlgorithm {

    /** JWA algorithm name for RSASSA-PKCS-v1_5 using SHA-256 */
    RS256("RS256", "RSA", "SHA256withRSA", 2048);

    /** The algorithm value */
    private final String value;
    /** The algorithm family name */
    private final String familyName;
    /** The algorithm jca name */
    private final String jcaName;
    /** The minimal length for keys */
    private final int minimalKeyLength;

    /**
     * Constructor of {@link HyggSignatureAlgorithm}
     *
     * @param value The value
     * @param familyName The family name
     * @param jcaName The jca name
     * @param minimalKeyLength The minimal length for keys
     */
    HyggSignatureAlgorithm(String value, String familyName, String jcaName, int minimalKeyLength) {
        this.value = value;
        this.familyName = familyName;
        this.jcaName = jcaName;
        this.minimalKeyLength = minimalKeyLength;
    }

    /**
     * Get the algorithm value
     *
     * @return A value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Get the algorithm family name
     *
     * @return A family name
     */
    public String getFamilyName() {
        return this.familyName;
    }

    /**
     * Get the algorithm Java Cryptography Architecture name
     *
     * @return A jca name
     */
    public String getJcaName() {
        return this.jcaName;
    }

    /**
     * Get the minimal accepted length for keys
     *
     * @return A length
     */
    public int getMinimalKeyLength() {
        return this.minimalKeyLength;
    }

}
