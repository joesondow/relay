package sondow.twitter

import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification
import twitter4j.conf.Configuration

class BotConfigFactorySpec extends Specification {

    @Rule
    public final EnvironmentVariables envVars = new EnvironmentVariables()

    def "configure should populate a configuration with environment variables"() {
        setup:
        String filler = Environment.SPACE_FILLER
        envVars.set("twitter_account", "cartoons")
        envVars.set("cartoons_twitter4j_oauth_consumerKey", "${filler}mickeymouse")
        envVars.set("cartoons_twitter4j_oauth_consumerSecret", "${filler}georgejetson")
        envVars.set("cartoons_twitter4j_oauth_accessToken", "${filler}fredflintstone")
        envVars.set("cartoons_twitter4j_oauth_accessTokenSecret", "${filler}bugsbunny")

        when:
        BotConfig tetraConfig = new BotConfigFactory().configure()
        Configuration twitterConfig = tetraConfig.getTwitterConfig()

        then:
        with(twitterConfig) {
            OAuthConsumerKey == 'mickeymouse'
            OAuthConsumerSecret == 'georgejetson'
            OAuthAccessToken == 'fredflintstone'
            OAuthAccessTokenSecret == 'bugsbunny'
        }
    }

    def "configure should populate a configuration with account specific env vars"() {
        setup:
        String filler = Environment.SPACE_FILLER
        envVars.set("twitter_account", "picardtips")
        envVars.set("picardtips_twitter4j_oauth_consumerKey", "${filler}flute")
        envVars.set("picardtips_twitter4j_oauth_consumerSecret", "${filler}makeitso")
        envVars.set("picardtips_twitter4j_oauth_accessToken", "${filler}therearefourlight")
        envVars.set("picardtips_twitter4j_oauth_accessTokenSecret", "${filler}engage")
        envVars.set("rikergoogling_twitter4j_oauth_consumerKey", "${filler}whatthehell")
        envVars.set("rikergoogling_twitter4j_oauth_consumerSecret", "${filler}minuet")
        envVars.set("rikergoogling_twitter4j_oauth_accessToken", "${filler}jazz")
        envVars.set("rikergoogling_twitter4j_oauth_accessTokenSecret", "${filler}beard")

        when:
        Configuration config = new BotConfigFactory().configure().getTwitterConfig()

        then:
        with(config) {
            OAuthConsumerKey == 'flute'
            OAuthConsumerSecret == 'makeitso'
            OAuthAccessToken == 'therearefourlight'
            OAuthAccessTokenSecret == 'engage'
        }
    }
}
