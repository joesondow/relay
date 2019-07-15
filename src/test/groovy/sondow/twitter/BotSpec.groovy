package sondow.twitter

import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification
import twitter4j.Status

class BotSpec extends Specification {

    @Rule
    public final EnvironmentVariables envVars = new EnvironmentVariables()


    def "go should make tweeter post a tweet"() {
        setup:
        envVars.set("twitter_access_id", "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZmoondoggy")
        envVars.set("twitter_handle", "SchoolsOfFish")
        Retweeter retweeter = Mock()
        Bot bot = new Bot(retweeter)

        when:
        Status result = bot.go()

        then:
        1 * retweeter.unretweet('EmojiTetra')
        0 * _._
    }
}
