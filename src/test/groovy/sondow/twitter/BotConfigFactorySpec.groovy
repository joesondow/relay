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
        String f = Environment.SPACE_FILLER
        String val = "${f}${f}cartoons,mickeymouse,georgejetson,fredflintstone,bugsbunny${f}${f}"
        envVars.set("twitter_credentials", val)

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
}
