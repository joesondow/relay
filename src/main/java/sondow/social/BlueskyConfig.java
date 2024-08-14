package sondow.social;

public class BlueskyConfig {

    /** Usually bsky.social */
    private String server;

    /** Assuming shorthandle.bsky.social for now */
    private String shortHandle;

    /** At least until bluesky has oauth */
    private String appPassword;

    public BlueskyConfig(String server, String shortHandle, String appPassword) {
        this.server = server;
        this.shortHandle = shortHandle;
        this.appPassword = appPassword;
    }

    public String getServer() {
        return server;
    }

    public String getShortHandle() {
        return shortHandle;
    }

    public String getFullHandle() {
        return shortHandle.toLowerCase() + "." + server;
    }

    public String getAppPassword() {
        return appPassword;
    }
}
