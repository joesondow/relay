package sondow.twitter

import java.time.ZonedDateTime
import spock.lang.Specification
import twitter4j.Paging
import twitter4j.ResponseList
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.User
import twitter4j.conf.Configuration

class RetweeterSpec extends Specification {

    def 'should retweet'() {
        setup:
        Time time = Mock()
        Configuration config = Mock()
        Twitter twitter = Mock()
        Retweeter retweeter = new Retweeter(time, config, twitter)
        Status tweet = Mock()

        when:
        Status result = retweeter.retweet(1234L)

        then:
        result.is(tweet)
        1 * twitter.retweetStatus(1234L) >> tweet
        0 * _._
    }

    def 'should unretweet recent tweets of target account'() {
        setup:
        Time time = Mock()
        Configuration config = Mock()
        Twitter twitter = Mock()
        Retweeter retweeter = new Retweeter(time, config, twitter)
        ResponseList<Status> userTimeline1 = Mock()
        Status tweet0 = Mock()
        long tweet0Id = 60300
        Date tweet0CreatedAt = Date.from(ZonedDateTime.parse('2019-06-19T05:15:20Z').toInstant())
        Status tweet1 = Mock()
        Status retweeted = Mock()
        User user = Mock()
        long tweet1Id = 39500
        Date tweet1CreatedAt = Date.from(ZonedDateTime.parse('2019-06-17T16:40:10Z').toInstant())
        Status tweet2 = Mock()
        long tweet2Id = 20500
        Date tweet2CreatedAt = Date.from(ZonedDateTime.parse('2019-06-16T16:40:10Z').toInstant())
        ResponseList<Status> userTimeline2 = Mock()

        when:
        retweeter.unretweet("EmojiTetra")

        then:
        1 * time.nowZonedDateTime() >> ZonedDateTime.parse('2019-06-19T08:34:55Z')
        1 * twitter.getUserTimeline(new Paging()) >> userTimeline1
        1 * userTimeline1.size() >> 3

        1 * userTimeline1.get(0) >> tweet0
        1 * tweet0.isRetweet() >> false
        1 * tweet0.getId() >> tweet0Id
        1 * tweet0.getCreatedAt() >> tweet0CreatedAt

        1 * userTimeline1.get(1) >> tweet1
        1 * tweet1.isRetweet() >> true
        1 * tweet1.getRetweetedStatus() >> retweeted
        1 * retweeted.getUser() >> user
        1 * user.getScreenName() >> 'EmojiTetra'
        2 * tweet1.getId() >> tweet1Id
        1 * tweet1.getCreatedAt() >> tweet1CreatedAt

        1 * userTimeline1.get(2) >> tweet2
        1 * tweet2.isRetweet() >> false
        1 * tweet2.getId() >> tweet2Id
        1 * tweet2.getCreatedAt() >> tweet2CreatedAt

        1 * twitter.getUserTimeline(new Paging(maxId: 20499L)) >> userTimeline2
        1 * userTimeline2.size() >> 0

        1 * tweet1.getRetweetedStatus() >> retweeted
        1 * retweeted.getId() >> 19000L
        1 * config.getUser() >> 'PicardTips'
        1 * twitter.destroyStatus(tweet1Id)
        0 * _._
    }

    def 'should unretweet nothing if there are no tweets'() {
        setup:
        Time time = Mock()
        Configuration config = Mock()
        Twitter twitter = Mock()
        Retweeter retweeter = new Retweeter(time, config, twitter)
        ResponseList<Status> userTimeline = Mock()

        when:
        retweeter.unretweet("EmojiTetra")

        then:
        1 * time.nowZonedDateTime() >> ZonedDateTime.parse('2019-06-19T08:34:55Z')
        1 * twitter.getUserTimeline(new Paging()) >> userTimeline
        1 * userTimeline.size() >> 0
        0 * _._
    }

    def 'should unretweet nothing if there are no retweets'() {
        setup:
        Time time = Mock()
        Configuration config = Mock()
        Twitter twitter = Mock()
        Retweeter retweeter = new Retweeter(time, config, twitter)
        ResponseList<Status> userTimeline1 = Mock()
        Status tweet0 = Mock()
        long tweet0Id = 60300
        Date tweet0CreatedAt = Date.from(ZonedDateTime.parse('2019-06-19T05:15:20Z').toInstant())
        Status tweet1 = Mock()
        long tweet1Id = 39500
        Date tweet1CreatedAt = Date.from(ZonedDateTime.parse('2019-06-17T16:40:10Z').toInstant())
        ResponseList<Status> userTimeline2 = Mock()

        when:
        retweeter.unretweet("EmojiTetra")

        then:
        1 * time.nowZonedDateTime() >> ZonedDateTime.parse('2019-06-19T08:34:55Z')
        1 * twitter.getUserTimeline(new Paging()) >> userTimeline1
        1 * userTimeline1.size() >> 2

        1 * userTimeline1.get(0) >> tweet0
        1 * tweet0.isRetweet() >> false
        1 * tweet0.getId() >> tweet0Id
        1 * tweet0.getCreatedAt() >> tweet0CreatedAt

        1 * userTimeline1.get(1) >> tweet1
        1 * tweet1.isRetweet() >> false
        1 * tweet1.getId() >> tweet1Id
        1 * tweet1.getCreatedAt() >> tweet1CreatedAt

        1 * twitter.getUserTimeline(new Paging(maxId: 39499L)) >> userTimeline2
        1 * userTimeline2.size() >> 0

        0 * _._
    }

    def 'should unretweet nothing if there are no relevant retweets'() {
        setup:
        Time time = Mock()
        Configuration config = Mock()
        Twitter twitter = Mock()
        Retweeter retweeter = new Retweeter(time, config, twitter)
        ResponseList<Status> userTimeline1 = Mock()
        Status tweet0 = Mock()
        long tweet0Id = 60300
        Date tweet0CreatedAt = Date.from(ZonedDateTime.parse('2019-06-19T05:15:20Z').toInstant())
        Status tweet1 = Mock()
        Status retweeted = Mock()
        User user = Mock()
        long tweet1Id = 39500
        Date tweet1CreatedAt = Date.from(ZonedDateTime.parse('2019-06-17T16:40:10Z').toInstant())
        Status tweet2 = Mock()
        long tweet2Id = 20500
        Date tweet2CreatedAt = Date.from(ZonedDateTime.parse('2019-06-16T16:40:10Z').toInstant())
        ResponseList<Status> userTimeline2 = Mock()

        when:
        retweeter.unretweet("EmojiTetra")

        then:
        1 * time.nowZonedDateTime() >> ZonedDateTime.parse('2019-06-19T08:34:55Z')
        1 * twitter.getUserTimeline(new Paging()) >> userTimeline1
        1 * userTimeline1.size() >> 3

        1 * userTimeline1.get(0) >> tweet0
        1 * tweet0.isRetweet() >> false
        1 * tweet0.getId() >> tweet0Id
        1 * tweet0.getCreatedAt() >> tweet0CreatedAt

        1 * userTimeline1.get(1) >> tweet1
        1 * tweet1.isRetweet() >> true
        1 * tweet1.getRetweetedStatus() >> retweeted
        1 * retweeted.getUser() >> user
        1 * user.getScreenName() >> 'SomeoneElse'
        1 * tweet1.getId() >> tweet1Id
        1 * tweet1.getCreatedAt() >> tweet1CreatedAt

        1 * userTimeline1.get(2) >> tweet2
        1 * tweet2.isRetweet() >> false
        1 * tweet2.getId() >> tweet2Id
        1 * tweet2.getCreatedAt() >> tweet2CreatedAt

        1 * twitter.getUserTimeline(new Paging(maxId: 20499L)) >> userTimeline2
        1 * userTimeline2.size() >> 0

        0 * _._
    }

    def 'should stop collecting tweets after reaching old dates'() {
        setup:
        Time time = Mock()
        Configuration config = Mock()
        Twitter twitter = Mock()
        Retweeter retweeter = new Retweeter(time, config, twitter)
        ResponseList<Status> userTimeline1 = Mock()
        Status tweet0 = Mock()
        long tweet0Id = 60300
        Date tweet0CreatedAt = Date.from(ZonedDateTime.parse('2019-02-19T05:15:20Z').toInstant())

        when:
        retweeter.unretweet("EmojiTetra")

        then:
        1 * time.nowZonedDateTime() >> ZonedDateTime.parse('2019-06-19T08:34:55Z')
        1 * twitter.getUserTimeline(new Paging()) >> userTimeline1
        1 * userTimeline1.size() >> 20

        1 * userTimeline1.get(0) >> tweet0
        1 * tweet0.isRetweet() >> false
        1 * tweet0.getId() >> tweet0Id
        1 * tweet0.getCreatedAt() >> tweet0CreatedAt

        0 * _._
    }

    def 'should stop collecting data after a large number of pages'() {
        setup:
        Time time = Mock()
        Twitter twitter = Mock()
        Retweeter retweeter = new Retweeter(time, Mock(Configuration), twitter)
        ResponseList<Status> userTimeline1 = Mock()
        Status tweet0 = Mock()
        Date tweet0CreatedAt = Date.from(ZonedDateTime.parse('2019-06-19T05:15:20Z').toInstant())

        when:
        retweeter.unretweet("EmojiTetra")

        then:
        1 * time.nowZonedDateTime() >> ZonedDateTime.parse('2019-06-19T08:34:55Z')
        24 * twitter.getUserTimeline(_) >> userTimeline1
        24 * userTimeline1.size() >> 20
        480 * userTimeline1.get(_) >> tweet0

        480 * tweet0.isRetweet() >> false
        480 * tweet0.getId() >> 60013
        480 * tweet0.getCreatedAt() >> tweet0CreatedAt

        0 * _._
    }

//    def 'should find target tweet'() {
//        setup:
//        Retweeter retweeter = new Retweeter()
//    }
}
