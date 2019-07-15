package sondow.twitter;

import twitter4j.conf.Configuration;

/**
 * The place to store all the bot's configuration variables, mostly from environment variables read
 * in BotConfigFactory.
 */
public class BotConfig {

    /**
     * The configuration for Twitter.
     */
    private Configuration twitterConfig;

    BotConfig(Configuration twitterConfig) {
        this.twitterConfig = twitterConfig;
    }

    public Configuration getTwitterConfig() {
        return twitterConfig;
    }
}
