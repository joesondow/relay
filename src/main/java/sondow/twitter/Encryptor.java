package sondow.twitter;

import java.util.HashMap;
import java.util.Map;

/**
 * This encrypts strings without making them longer. The secret key for encrypting is a string of letters, numbers, and special characters. Each character in the secret key represents an offset
 * number, which is the number of places to advance the value character at that position. Offsets cycle through an extended alphabet of expected characters.
 * <p>
 * To be clear, this is not strong encryption in any way. The purpose of this algorithm is to allow me to stream coding sessions while working on this program and checking my data, without risk of
 * accidentally showing my own sensitive data to viewers, without increasing the length of the data stored in the database because more data costs more money. As long as I use encryption keys that I
 * don't display to viewers, this level of security is adequate for my purposes. This is not intended for protecting other people's secrets. Shamir's Secret Sharing Scheme, with encrypted secret
 * shares distributed across cloud databases owned by different companies, is more suitable for protecting other people's secrets.
 */
public class Encryptor {

    /**
     * 90 characters long.
     */
    public final static String BIG_ALPHABET = "abcdefghijklmnopqrstuvwxyz" +
            "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#%^&*()_+`-=;:,./<>?[]{}|";

    public static void main(String[] args) {

        Map<String, String> sourcesToKeys = new HashMap<>();

        for (String source : sourcesToKeys.keySet()) {
            System.out.println(source.substring(0, 20) + "\n" + new Encryptor()
                    .encrypt(source,
                            sourcesToKeys.get(source)) + "\n\n");
        }
    }

    public String decrypt(String encrypted, String encryptionKey) {
        return replaceCharacters(encrypted, Algorithm.DECRYPT, encryptionKey);
    }

    public String encrypt(String source, String key) {
        return replaceCharacters(source, Algorithm.ENCRYPT, key);
    }

    private String replaceCharacters(String source, Algorithm algorithm, String key) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            String srcCharacter = source.substring(i, i + 1);
            int encryptionKeyLength = key.length();
            int keyIndex = i % encryptionKeyLength;
            String keyChar = key.substring(keyIndex, keyIndex + 1);
            int alphabetIndexOfKeyChar = BIG_ALPHABET.indexOf(keyChar);
            String replacement;
            String message = i + " " + srcCharacter + " - enc key idx " + keyIndex + " " + keyChar +
                    " alphabet idx of key char " + alphabetIndexOfKeyChar;
            if (BIG_ALPHABET.contains(srcCharacter) && BIG_ALPHABET.contains(keyChar)) {
                // Replace character with alternate character.
                // Get index of raw character in master alphabet.
                int alphabetIndexOfSrcChar = BIG_ALPHABET.indexOf(srcCharacter);
                message += " alphabet index of src char " + alphabetIndexOfSrcChar;
                int newIndex;
                if (algorithm == Algorithm.DECRYPT) {
                    newIndex = alphabetIndexOfSrcChar - alphabetIndexOfKeyChar;
                    if (newIndex < 0) {
                        newIndex = BIG_ALPHABET.length() + newIndex;
                    }
                } else {
                    newIndex = alphabetIndexOfSrcChar + alphabetIndexOfKeyChar;
                }
                int modifiedIndex = newIndex % BIG_ALPHABET.length();
                message += " modified index " + modifiedIndex;
                replacement = BIG_ALPHABET.substring(modifiedIndex, modifiedIndex + 1);
            } else {
                replacement = srcCharacter; // Unexpected character. Do not replace.
            }
            //            System.out.println(message + " replacement " + replacement);
            builder.append(replacement);
        }
        return builder.toString();
    }

    private enum Algorithm {
        ENCRYPT,
        DECRYPT
    }
}
