package sondow.social;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
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

        Configuration pollReaderConf = configureTwitter("cred_twitter_poll_reading");
        String targetAccountsCsv = environment.require("target_accounts");
        String[] targetAccounts = targetAccountsCsv.split(",");
        LinkedHashMap<String, Configuration> screenNamesToConfigs = new LinkedHashMap<>();
        for (String account : targetAccounts) {
            Configuration configuration = configureTwitter("cred_" + account);
            screenNamesToConfigs.put(account, configuration);
        }
        String pollAccountsCsv = environment.require("poll_accounts");
        String[] pollAccountsArray = pollAccountsCsv.split(",");
        List<String> pollAccounts = Arrays.asList(pollAccountsArray);
        return new BotConfig(pollReaderConf, screenNamesToConfigs, pollAccounts) ;
    }

    private Configuration configureTwitter(String envVar) {
        ConfigurationBuilder configBuilder = new ConfigurationBuilder();

        String credentialsCsv = environment.require(envVar);

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
