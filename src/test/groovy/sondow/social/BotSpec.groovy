package sondow.social

import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification
import twitter4j.Status

class BotSpec extends Specification {

    @Rule
    public final EnvironmentVariables envVars = new EnvironmentVariables()

    def "go should make retweeter retweet a tweet"() {
        setup:
        String f = Environment.SPACE_FILLER
        envVars.set("cred_twitter_poll_reading", "cartoons,mickeymouse,georgejetson,fredflintstone,bugsbunny")
        envVars.set("target_accounts", "SchoolsOfFish,EmojiPettingZoo")
        envVars.set("cred_SchoolsOfFish", "SchoolsOfFish,a,b,c,d")
        envVars.set("cred_EmojiPettingZoo", "EmojiPettingZoo,a,b,c,d")

        envVars.set("bluesky_short_handles", "SchoolsOfFish,EmojiPettingZoo${f}")
        envVars.set("cred_bluesky_SchoolsOfFish", "bsky.social,schoolsoffish,fishyapppassword")
        envVars.set("cred_bluesky_EmojiPettingZoo", "bsky.social,emojipettingzoo,zooapppassword")

        envVars.set("poll_accounts", "")
        RetweeterFactory retweeterFactory = Mock()
        BlueskyReposterFactory blueskyReposterFactory = Mock()
        Time time = Mock()
        PollTweetChooserFactory pollTweetChooserFactory = Mock()
        Keymaster keymaster = Mock()
        Environment environment = new Environment(keymaster)
        BotConfigFactory botConfigFactory = new BotConfigFactory(environment)
        Bot bot = new Bot(botConfigFactory, retweeterFactory, blueskyReposterFactory, time, pollTweetChooserFactory)
        Retweeter sofRetweeter = Mock()
        Retweeter epzRetweeter = Mock()
        BlueskyReposter sofBlueskyReposter = Mock()
        BlueskyReposter epzBlueskyReposter = Mock()
        Status epzTweet = Mock()
        Status retweet = Mock()

        when:
        Status result = bot.go()

        then:
        1 * retweeterFactory.build({user: 'SchoolsOfFish'}) >> sofRetweeter
        1 * retweeterFactory.build({user: 'EmojiPettingZoo'}) >> epzRetweeter

        1 * blueskyReposterFactory.build({shortHandle: 'schoolsoffish'}) >> sofBlueskyReposter
//        1 * blueskyReposterFactory.build({shortHandle: 'emojipettingzoo'}) >> epzBlueskyReposter

        1 * epzRetweeter.findTargetPopularTweet() >> epzTweet
        1 * sofRetweeter.unretweet()
        1 * epzTweet.getId() >> 33L
        1 * sofRetweeter.retweet(33L) >> retweet

        1 * sofBlueskyReposter.unrepost()

        result == retweet
        0 * _._
    }
}
