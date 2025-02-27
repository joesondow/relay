package sondow.social;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import twitter4j.conf.Configuration;

/**
 * The place to store all the bot's configuration variables, mostly from environment variables read
 * in BotConfigFactory.
 */
public class BotConfig {

    /** Credentials for logging into Bluesky */
    private final LinkedHashMap<String, BlueskyConfig> blueskyShortHandlesToConfigs;

    /** Credentials for logging into Mastodon */
    private final LinkedHashMap<String, MastodonConfig> mastodonUsernamesToConfigs;

    /**
     * The configuration for the Twitter account used to read polls.
     */
    private final Configuration pollReadingTwitterConfig;

    /**
     * Twitter configurations for all the screen names.
     */
    private final LinkedHashMap<String, Configuration> screenNamesToTwitterConfigs;

    /** Screen names of accounts where the tweets to retweet are live polls */
    private final List<String> twitterPollAccounts;

    BotConfig(LinkedHashMap<String, BlueskyConfig> blueskyShortHandlesToConfigs, Configuration pollReadingTwitterConfig, LinkedHashMap<String, Configuration> screenNamesToTwitterConfigs, List<String> twitterPollAccounts) {
        this.blueskyShortHandlesToConfigs = blueskyShortHandlesToConfigs;
        this.mastodonUsernamesToConfigs = null;
        this.pollReadingTwitterConfig = pollReadingTwitterConfig;
        this.screenNamesToTwitterConfigs = screenNamesToTwitterConfigs;
        this.twitterPollAccounts = twitterPollAccounts;
    }

    public LinkedHashMap<String, BlueskyConfig> getBlueskyShortHandlesToConfigs() {
        return blueskyShortHandlesToConfigs;
    }

    public Configuration getPollReadingTwitterConfig() {
        return pollReadingTwitterConfig;
    }

    public Configuration getTwitterConfig(String screenName) {
        return screenNamesToTwitterConfigs.get(screenName);
    }
    public BlueskyConfig getBlueskyConfig(String shortName) {
        return blueskyShortHandlesToConfigs.get(shortName);
    }

    public boolean isPollAccount(String targetScreenName) {
        return twitterPollAccounts.contains(targetScreenName);
    }

    public List<String> getTwitterAccounts() {
        return new ArrayList<>(screenNamesToTwitterConfigs.keySet());
    }
}
