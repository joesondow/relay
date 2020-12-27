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
        envVars.set("cred_twitter_poll_reading", val)
        envVars.set("target_accounts", "Kramer")
        envVars.set("cred_Kramer", "Kramer,a,b,c,d")
        envVars.set("poll_accounts", "")
        Keymaster keymaster = Mock()
        Environment environment = new Environment(keymaster)
        BotConfigFactory factory = new BotConfigFactory(environment)
        BotConfig botConfig = factory.configure()

        when:
        Configuration twitterConfig = botConfig.getPollReadingTwitterConfig()

        then:
        with(twitterConfig) {
            OAuthConsumerKey == 'mickeymouse'
            OAuthConsumerSecret == 'georgejetson'
            OAuthAccessToken == 'fredflintstone'
            OAuthAccessTokenSecret == 'bugsbunny'
        }
    }
}
