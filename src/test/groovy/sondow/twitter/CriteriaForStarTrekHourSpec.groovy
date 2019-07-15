package sondow.twitter

import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification
import twitter4j.MediaEntity
import twitter4j.Status
import twitter4j.URLEntity
import twitter4j.User

class CriteriaForStarTrekHourSpec extends Specification {

    @Rule
    public final EnvironmentVariables envVars = new EnvironmentVariables()

    def "should match on correct criteria"() {
        setup:
        Status tweet = Mock()
        User user = Mock()
        URLEntity urlEntity = Mock()
        ZonedDateTime now = ZonedDateTime.parse('2019-07-15T02:36:00Z')
        Date createdAt = Date.from(ZonedDateTime.parse('2019-07-' + day + 'T02:36:00Z').toInstant())
        Time time = Mock()
        CriteriaForStarTrekHour criteria = new CriteriaForStarTrekHour(time)

        when:
        boolean matches = criteria.matches(tweet)

        then:
        expected == matches
        time.nowZonedDateTime() >> now
        tweet.isRetweet() >> isRt
        tweet.getUser() >> user
        user.getScreenName() >> name
        tweet.getCreatedAt() >> createdAt
        tweet.getText() >> text
        tweet.getURLEntities() >> [urlEntity]
        urlEntity.getExpandedURL() >> link
        tweet.getMediaEntities() >> [Mock(MediaEntity)]
        0 * _._

        where:
        isRt  | name           | day  | text    | link                                    | expected
        true  | 'StarTrekHour' | '14' | 'Tues,' | 'https://docs.google.com/forms/theform' | false
        false | 'PicardTips'   | '14' | 'Tues,' | 'https://docs.google.com/forms/theform' | false
        false | 'StarTrekHour' | '03' | 'Tues,' | 'https://docs.google.com/forms/theform' | false
        false | 'StarTrekHour' | '14' | 'Hi'    | 'https://docs.google.com/forms/theform' | false
        false | 'StarTrekHour' | '14' | 'Tues,' | 'https://startrekhour.wordpress.com'    | false
        false | 'StarTrekHour' | '14' | 'Tues,' | 'https://docs.google.com/forms/theform' | true
    }
}
