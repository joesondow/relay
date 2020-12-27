package sondow.twitter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import twitter4j.conf.Configuration;

/**
 * The place to store all the bot's configuration variables, mostly from environment variables read
 * in BotConfigFactory.
 */
public class BotConfig {

    /**
     * The configuration for the Twitter account used to read polls.
     */
    private final Configuration pollReadingTwitterConfig;

    /**
     * Twitter configurations for all the screen names.
     */
    private final LinkedHashMap<String, Configuration> screenNamesToTwitterConfigs;

    /** Screen names of accounts where the tweets to retweet are live polls */
    private final List<String> pollAccounts;

    BotConfig(Configuration pollReadingTwitterConfig, LinkedHashMap<String, Configuration> screenNamesToTwitterConfigs, List<String> pollAccounts) {
        this.pollReadingTwitterConfig = pollReadingTwitterConfig;
        this.screenNamesToTwitterConfigs = screenNamesToTwitterConfigs;
        this.pollAccounts = pollAccounts;
    }

    public Configuration getPollReadingTwitterConfig() {
        return pollReadingTwitterConfig;
    }

    public Configuration getConfig(String screenName) {
        return screenNamesToTwitterConfigs.get(screenName);
    }

    public boolean isPollAccount(String targetScreenName) {
        return pollAccounts.contains(targetScreenName);
    }

    public List<String> getAccounts() {
        return new ArrayList<>(screenNamesToTwitterConfigs.keySet());
    }
}
