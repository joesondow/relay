package sondow.twitter;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Builds a BotConfig based on environment variables.
 */
public class BotConfigFactory {

    public BotConfig configure() {
        Configuration twitterConf = configureTwitter();
        return new BotConfig(twitterConf);
    }

    /**
     * AWS Lambda only allows underscores in environment variables, not dots, so the default ways
     * twitter4j finds keys aren't possible. Instead, this custom code gets the configuration either
     * from Lambda-friendly environment variables or else allows Twitter4J to look in its default
     * locations like twitter4j.properties file at the project root, or on the classpath, or in
     * WEB-INF.
     * <p>
     * The config from this method also includes keys for accessing the Twitter API, as well as
     * some domain-specific variables.
     *
     * @return configuration containing AWS and Twitter authentication strings and other variables
     */
    private Configuration configureTwitter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        // Override with a specific account if available. This mechanism allows us to provide
        // multiple key sets in the AWS Lambda configuration, and switch which Twitter account
        // to target by retyping just the target Twitter account name in the configuration.
        String account = Environment.require("twitter_account");
        builder.setUser(account);

        String consumKeyEnvKey = "twitter4j_oauth_consumerKey";
        String consSecEnvKey = "twitter4j_oauth_consumerSecret";
        String accTokEnvKey = "twitter4j_oauth_accessToken";
        String atsEnvKey = "twitter4j_oauth_accessTokenSecret";

        String consumerKey = Environment.either(account + "_" + consumKeyEnvKey, consumKeyEnvKey);
        String consumerSecret = Environment.either(account + "_" + consSecEnvKey, consSecEnvKey);
        String accessToken = Environment.either(account + "_" + accTokEnvKey, accTokEnvKey);
        String accessTokenSecret = Environment.either(account + "_" + atsEnvKey, atsEnvKey);

        builder.setOAuthConsumerKey(consumerKey);
        builder.setOAuthConsumerSecret(consumerSecret);
        builder.setOAuthAccessToken(accessToken);
        builder.setOAuthAccessTokenSecret(accessTokenSecret);

        return builder.setTrimUserEnabled(true).build();
    }
}
