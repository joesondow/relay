package sondow.social;

/**
 * AWS KMS is an option but there's no free tier to use it, so this class uses my custom Encryptor
 * scheme, with keys that are not in a public repo but are stored either in the deployed jar file or
 * on the local development file system in an adjacent project called "cipher".
 * <p>
 * The main reason I encrypt some secret values set in environment variables is so that I can live
 * stream some coding and show the web page where I'm doing the deployment without accidentally
 * broadcasting the secret values in the environment variables.
 *
 * @author @JoeSondow
 */
public class Environment {

    public static final String SPACE_FILLER = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private Keymaster keymaster;
    private Encryptor encryptor = new Encryptor();

    public Environment() {
        this(new Keymaster());
    }

    public Environment(Keymaster keymaster) {
        this.keymaster = keymaster;
    }

    public String get(String name) {
        return get(name, null);
    }

    public String get(String name, String defaultValue) {
        String rawValue = getValueFromEnvOrEncryptedValuesFile(name);
        String modified;
        if (rawValue == null) {
            modified = defaultValue;
        } else {
            modified = rawValue;
            while (modified.startsWith(SPACE_FILLER)) {
                modified = modified.substring(SPACE_FILLER.length());
            }
            while (modified.endsWith(SPACE_FILLER)) {
                modified = modified.substring(0, modified.length() - SPACE_FILLER.length());
            }
        }
        if (modified != null) {
            String cryptoKey = keymaster.getCryptoKey(name);
            if (cryptoKey != null) {
                modified = encryptor.decrypt(modified, cryptoKey);
            }
        }
        return modified;
    }

    private String getValueFromEnvOrEncryptedValuesFile(String name) {
        String value = System.getenv(name);
        if (value == null) {
            value = keymaster.getValue(name);
        }
        return value;
    }

    public Integer getInt(String name, int defaultValue) {
        String rawValue = get(name);
        if (rawValue == null) {
            return defaultValue;
        }
        return new Integer(rawValue);
    }

    /**
     * Throws a RuntimeException if the specified environment variable has a null value.
     *
     * @param name the name of the environment variable
     * @return the value of the environment variable
     */
    public String require(String name) {
        String value = get(name);
        if (value == null) {
            throw new RuntimeException("Required environment variable '" + name + "' is missing.");
        }
        return value;
    }
}
