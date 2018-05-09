import org.apache.activemq.artemis.utils.DefaultSensitiveStringCodec;

import java.util.HashMap;
import java.util.Map;

public final class Codec {

    private final DefaultSensitiveStringCodec codec;

    public Codec(final boolean reversible) throws Exception {

        // Create the default codec used by artemis
        this.codec = new DefaultSensitiveStringCodec();

        // Create parameters for the codec
        Map<String, String> params = new HashMap<String, String>();
        params.put(
                DefaultSensitiveStringCodec.ALGORITHM,
                reversible ? DefaultSensitiveStringCodec.TWO_WAY : DefaultSensitiveStringCodec.ONE_WAY
        );

        try {
            // Initialize the codec
            this.codec.init(params);
        } catch (Exception e) {
            throw new Exception("Error initializing artemis2-mask encoder.", e);
        }
    }

    public final String encode(final String secret) throws Exception {

        if (secret == null || "".equals(secret)) {
            throw new IllegalArgumentException("Error encoding secret, argument secret cannot be null or empty.");
        }

        try {
            // Encode the secret
            return this.codec.encode(secret);
        } catch (Exception e) {
            throw new Exception("Error occurred while encoding the secret.", e);
        }
    }

    public final String decode(final String secret) throws Exception {

        if (secret == null || "".equals(secret)) {
            throw new IllegalArgumentException("Error decoding secret, argument secret cannot be null or empty.");
        } else if(secret.startsWith("ENC")) {
            throw new IllegalArgumentException("Error decoding secret, please pass the secret without ENC(..).");
        }

        try {
            // Decode the secret
            return this.codec.decode(secret);
        } catch (Exception e) {
            throw new Exception("Error occurred while decoding the secret.", e);
        }
    }

    public final boolean verify(final String plainSecret, final String maskSecret) throws Exception {

        if (plainSecret == null || "".equals(plainSecret)) {
            throw new IllegalArgumentException("Error verifying the secret, argument plainSecret cannot be null or empty.");
        }

        if (maskSecret == null || "".equals(maskSecret)) {
            throw new IllegalArgumentException("Error verifying the secret, argument maskSecret cannot be null or empty.");
        } else if(maskSecret.startsWith("ENC")) {
            throw new IllegalArgumentException("Error verifying the secret, please pass the masked secret without ENC(..).");
        }

        try {
            return this.codec.verify(plainSecret.toCharArray(), maskSecret);
        } catch (Exception e) {
            throw new Exception("Error occurred while verifying the secret.", e);
        }
    }
}