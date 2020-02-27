package sondow.twitter;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

public class Keymaster {

    private FileClerk fileClerk;
    private Map<String, String> envVarNamesToCryptoKeys = null;
    private boolean cryptoFileUnavailable = false;

    public Keymaster() {
        this(new FileClerk());
    }

    public Keymaster(FileClerk fileClerk) {
        this.fileClerk = fileClerk;
    }

    public String getCryptoKey(String key) {
        ensureCryptoFileIsParsed();
        String cryptoKey;
        if (envVarNamesToCryptoKeys == null) {
            cryptoKey = null;
        } else {
            cryptoKey = envVarNamesToCryptoKeys.get(key);
        }
        return cryptoKey;
    }

    private void ensureCryptoFileIsParsed() {
        if (!cryptoFileUnavailable && envVarNamesToCryptoKeys == null) {
            String cryptoFileName = getCryptoFileName();
            String cryptoJson = fileClerk.readTextFile(cryptoFileName);
            if (cryptoJson == null) {
                cryptoJson = fileClerk.readTextFile("../cipher/" + cryptoFileName);
            }
            if (cryptoJson == null) {
                cryptoFileUnavailable = true;
            } else {
                try {
                    envVarNamesToCryptoKeys = parseCryptoJson(cryptoJson);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String getCryptoFileName() {
        Properties properties = new Properties();
        String propsText = fileClerk.readTextFile("build.properties");
        try {
            properties.load(new StringReader(propsText));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String rootProjectName = properties.getProperty("root.project.name");
        return rootProjectName + "-encryption-keys.json";
    }

    private Map<String, String> parseCryptoJson(String cryptoJson) throws JSONException {
        HashMap<String, String> namesToCryptoKeys = new HashMap<>();
        JSONObject obj = new JSONObject(cryptoJson);
        JSONArray names = obj.names();
        for (int i = 0; i < names.length(); i++) {
            String name = names.getString(i);
            namesToCryptoKeys.put(name, obj.getString(name));
        }
        return namesToCryptoKeys;
    }
}
