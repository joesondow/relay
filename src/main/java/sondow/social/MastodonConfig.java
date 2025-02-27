package sondow.social;

public class MastodonConfig {

    /** Example value: "mast.to" */
    private String mastodonInstanceName;

    /**
     * Example value: "TinyPettingZoo"
     * Probably not used but helpful to have for authentication documentation clarity.
     */
    private String shortHandle;

    /** Example value: "PEjvQMK0w37kx1cYiRNLCQ58kBHSNzPCfNnekWwSEFF" */
    private String mastodonAccessToken;

    public MastodonConfig(String mastodonInstanceName, String shortHandle, String mastodonAccessToken) {
        this.mastodonInstanceName = mastodonInstanceName;
        this.shortHandle = shortHandle;
        this.mastodonAccessToken = mastodonAccessToken;
    }

    public String getMastodonInstanceName() {
        return mastodonInstanceName;
    }

    public String getMastodonAccessToken() {
        return mastodonAccessToken;
    }
}
