package sondow.social;

public class MastodonConfig {

    private String mastodonInstanceName;
    private String mastodonAccessToken;

    public MastodonConfig(String mastodonInstanceName, String mastodonAccessToken) {
        this.mastodonInstanceName = mastodonInstanceName;
        this.mastodonAccessToken = mastodonAccessToken;
    }

    public String getMastodonInstanceName() {
        return mastodonInstanceName;
    }

    public String getMastodonAccessToken() {
        return mastodonAccessToken;
    }
}
