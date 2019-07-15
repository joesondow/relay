package sondow.twitter

import spock.lang.Specification
import twitter4j.Status

class BotSpec extends Specification {

    def "go should make tweeter post a tweet"() {
        setup:
        Tweeter tweeter = Mock()
        Bot bot = new Bot(tweeter)
        Status status = Mock()

        when:
        Status result = bot.go()

        then:
        1 * tweeter.tweet("") >> status
        result == status
        0 * _._
    }
}
