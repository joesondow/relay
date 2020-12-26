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
    private String projectName = null;

    private Map<String, String> envVarNamesToCryptoKeys = null;
    private boolean encryptionKeysFileUnavailable = false;

    private Map<String, String> envVarNamesToEncryptedValues = null;
    private boolean encryptedValuesFileUnavailable = false;

    public Keymaster() {
        this(new FileClerk());
    }

    public Keymaster(FileClerk fileClerk) {
        this.fileClerk = fileClerk;
    }

    public String getCryptoKey(String name) {
        ensureEncryptionKeysFileIsParsed();
        String cryptoKey;
        if (envVarNamesToCryptoKeys == null) {
            cryptoKey = null;
        } else {
            cryptoKey = envVarNamesToCryptoKeys.get(name);
        }
        return cryptoKey;
    }

    public String getValue(String name) {
        ensureEncryptedValuesFileIsParsed();
        String encryptedValue;
        if (envVarNamesToEncryptedValues == null) {
            encryptedValue = null;
        } else {
            encryptedValue = envVarNamesToEncryptedValues.get(name);
        }
        return encryptedValue;
    }

    private void ensureEncryptionKeysFileIsParsed() {
        if (!encryptionKeysFileUnavailable && envVarNamesToCryptoKeys == null) {
            String encryptionKeysFileName = getEncryptionKeysFileName();
            String cryptoJson = readTextFile(encryptionKeysFileName, "cipher");
            if (cryptoJson == null) {
                encryptionKeysFileUnavailable = true;
            } else {
                try {
                    envVarNamesToCryptoKeys = parseJsonObjectIntoStringMap(cryptoJson);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void ensureEncryptedValuesFileIsParsed() {
        if (!encryptedValuesFileUnavailable && envVarNamesToEncryptedValues == null) {
            String encryptedValuesFileName = getEncryptedValuesFileName();
            String cryptoJson = readTextFile(encryptedValuesFileName, "crypt");
            if (cryptoJson == null) {
                encryptedValuesFileUnavailable = true;
            } else {
                try {
                    envVarNamesToEncryptedValues = parseJsonObjectIntoStringMap(cryptoJson);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String readTextFile(String fileName, String neighboringFolderName) {
        String cryptoJson = fileClerk.readTextFile(fileName);
        if (cryptoJson == null) {
            cryptoJson = fileClerk.readTextFile("../" + neighboringFolderName + "/" + fileName);
        }
        return cryptoJson;
    }

    private String getProjectName() {
        if (projectName == null) {
            Properties properties = new Properties();
            String propsText = fileClerk.readTextFile("build.properties");
            try {
                properties.load(new StringReader(propsText));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            projectName = properties.getProperty("root.project.name");
        }
        return projectName;
    }

    private String getEncryptionKeysFileName() {
        return getProjectName() + "-encryption-keys.json";
    }

    private String getEncryptedValuesFileName() {
        return getProjectName() + "-values.json";
    }

    private Map<String, String> parseJsonObjectIntoStringMap(String json) throws JSONException {
        HashMap<String, String> namesToValues = new HashMap<>();
        JSONObject obj = new JSONObject(json);
        JSONArray names = obj.names();
        for (int i = 0; i < names.length(); i++) {
            String name = names.getString(i);
            namesToValues.put(name, obj.getString(name));
        }
        return namesToValues;
    }
}
