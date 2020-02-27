package sondow.twitter;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Builds a BotConfig based on environment variables.
 */
public class BotConfigFactory {

    private Environment environment;

    public BotConfigFactory(Environment environment) {
        this.environment = environment;
    }

    public BotConfigFactory() {
        this(new Environment());
    }

    public BotConfig configure() {
        Configuration twitterConf = configureTwitter();
        return new BotConfig(twitterConf);
    }

    private Configuration configureTwitter() {
        ConfigurationBuilder configBuilder = new ConfigurationBuilder();

        String credentialsCsv = environment.require("twitter_credentials");

        String[] tokens = credentialsCsv.split(",");
        String screenName = tokens[0];
        String consumerKey = tokens[1];
        String consumerSecret = tokens[2];
        String accessToken = tokens[3];
        String accessTokenSecret = tokens[4];

        configBuilder.setUser(screenName);
        configBuilder.setOAuthConsumerKey(consumerKey);
        configBuilder.setOAuthConsumerSecret(consumerSecret);
        configBuilder.setOAuthAccessToken(accessToken);
        configBuilder.setOAuthAccessTokenSecret(accessTokenSecret);

        return configBuilder.setTrimUserEnabled(false).setTweetModeExtended(true).build();
    }
}
