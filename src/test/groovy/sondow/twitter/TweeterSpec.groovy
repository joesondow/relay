package sondow.twitter

import spock.lang.Specification
import twitter4j.Status
import twitter4j.Twitter

class TweeterSpec extends Specification {

    def "attempting to tweet a message calls the twitter api's updateStatus"() {
        setup:
        Twitter twitter = Mock()
        Tweeter tweeter = new Tweeter(twitter)
        Status mockStatus = Mock()

        when:
        Status result = tweeter.tweet("Hey look I'm a tweet")

        then:
        1 * twitter.updateStatus("Hey look I'm a tweet") >> mockStatus
        result == mockStatus
    }
}
