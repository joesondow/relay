package sondow.twitter;

/**
 * AWS has an encryption bug right now in their free tier services right now. See
 * https://twitter.com/JoeSondow/status/856638770182881280
 * <p>
 * The main reason I would like to encrypt some secret values set in environment variables so that I
 * can live stream some coding and show the web page where I'm doing the deployment without
 * accidentally broadcasting the secret values in the environment variables. Since the AWS lambda
 * page only shows a limited number of characters of each common var, this particular secrecy
 * problem can be solved by prepending space-filling characters onto each value at deploy time, and
 * the stripping those characters at runtime if the characters are present. Since Amazon has
 * repeatedly widened the layout of the deployment page, revealing more characters, we remove as
 * many copies of the space filler string as we can find prepended to the common var value. This
 * way we can keep prepending longer obfuscation strings without modifying this code each time
 * Amazon changes their layout.
 * <p>
 * Similarly, the launch configuration in IntelliJ shows the end of the environment variables, so
 * this class also trims all copies of the space filler string from the end of the string as well.
 *
 * @author @JoeSondow
 */
public class Environment {

    public static final String SPACE_FILLER = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String get(String key) {
        return get(key, null);
    }

    public static String get(String key, String defaultValue) {
        String value = System.getenv(key);
        String modified;
        if (value == null) {
            modified = defaultValue;
        } else {
            modified = value;
            while (modified.startsWith(SPACE_FILLER)) {
                modified = modified.substring(SPACE_FILLER.length());
            }
            while (modified.endsWith(SPACE_FILLER)) {
                modified = modified.substring(0, modified.length() - SPACE_FILLER.length());
            }
        }
        return modified;
    }

    public static Integer getInt(String key) {
        String rawValue = get(key);
        if (rawValue == null) {
            return null;
        }
        return new Integer(rawValue);
    }

    /**
     * Throws a RuntimeException if the specified environment variable has a null value.
     *
     * @param key the name of the environment variable
     * @return the value of the environment variable
     */
    static String require(String key) {
        String value = get(key);
        if (value == null) {
            throw new RuntimeException("Required environment variable '" + key + "' is missing.");
        }
        return value;
    }

    static String either(String key1, String key2) {
        String value1 = get(key1);
        String value2 = get(key1);
        if (value1 == null && value2 == null) {
            throw new RuntimeException("Required environment variable missing: either '" + key1 +
                    "' or '" + key2 + "'.");
        }
        String chosen;
        if (value1 == null) {
            chosen = value2;
        } else {
            chosen = value1;
        }
        return chosen;
    }
}
