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
        envVars.set("cred_twitter_poll_reading", "cartoons,mickeymouse,georgejetson,fredflintstone,bugsbunny")
        envVars.set("target_accounts", "SchoolsOfFish,EmojiPettingZoo")
        envVars.set("cred_SchoolsOfFish", "SchoolsOfFish,a,b,c,d")
        envVars.set("cred_EmojiPettingZoo", "EmojiPettingZoo,a,b,c,d")
        envVars.set("poll_accounts", "")
        RetweeterFactory retweeterFactory = Mock()
        Time time = Mock()
        PollTweetChooserFactory pollTweetChooserFactory = Mock()
        Bot bot = new Bot(retweeterFactory, time, pollTweetChooserFactory)
        Retweeter sofRetweeter = Mock()
        Retweeter epzRetweeter = Mock()
        Status epzTweet = Mock()
        Status retweet = Mock()

        when:
        Status result = bot.go()

        then:
        1 * retweeterFactory.build({user: 'SchoolsOfFish'}) >> sofRetweeter
        1 * retweeterFactory.build({user: 'EmojiPettingZoo'}) >> epzRetweeter
        1 * epzRetweeter.findTargetPopularTweet() >> epzTweet
        1 * sofRetweeter.unretweet()
        1 * epzTweet.getId() >> 33L
        1 * sofRetweeter.retweet(33L) >> retweet
        result == retweet
        0 * _._
    }
}
