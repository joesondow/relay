package sondow.social

import java.time.ZonedDateTime
import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification
import spock.lang.Unroll
import twitter4j.conf.Configuration

class AccountChooserSpec extends Specification {

    @Rule
    public final EnvironmentVariables envVars = new EnvironmentVariables()

    @Unroll("promoter=#promoter target=#target on #date")
    def "should choose target and promoter based on day"() {
        setup:
        Configuration tc = Mock()
        BlueskyConfig b = Mock()
        MastodonConfig m = Mock()
        BotConfig botConfig = new BotConfig(
                [Chidi: b, Eleanor: b, Jason: b, Tahani: b, Michael: b, Janet: b, Derek:
                        b] as LinkedHashMap,
                [Dylan: m, Mark: m, Helly: m, Irving: m, Burt: m, Milchick: m, Cobel:
                        m] as LinkedHashMap,
                null,
                [Scrooge: tc, Launchpad: tc, Darkwing: tc, Gosling: tc, Huey: tc, Duey: tc, Louie:
                        tc] as LinkedHashMap, [])
        Time time = Mock()
        AccountChooser accountChooser = new AccountChooser(botConfig, time)

        when:
        PromoterAndTarget blueskyPromoterAndTarget = accountChooser.chooseBlueskyPromoterAndTarget()


        then:
        blueskyPromoterAndTarget.getPromoter() == promoter
        blueskyPromoterAndTarget.getTarget() == target
        1 * time.nowUtc() >> ZonedDateTime.parse('2020-' + date + 'T02:36:00Z')

        where:
        promoter  | target    | date
        'Eleanor' | 'Jason'   | '01-01'
        'Jason'   | 'Tahani'  | '01-02'
        'Tahani'  | 'Michael' | '01-03'
        'Michael' | 'Janet'   | '01-04'
        'Janet'   | 'Derek'   | '01-05'
        'Derek'   | 'Chidi'   | '01-06'
        'Eleanor' | 'Chidi'   | '01-07'
        'Jason'   | 'Eleanor' | '01-08'
        'Tahani'  | 'Jason'   | '01-09'
        'Michael' | 'Tahani'  | '01-10'
        'Janet'   | 'Michael' | '01-11'
        'Derek'   | 'Janet'   | '01-12'
        'Chidi'   | 'Derek'   | '01-13'
        'Jason'   | 'Chidi'   | '01-14'
        'Tahani'  | 'Eleanor' | '01-15'
        'Michael' | 'Jason'   | '01-16'
        'Janet'   | 'Tahani'  | '01-17'
        'Derek'   | 'Michael' | '01-18'
        'Chidi'   | 'Janet'   | '01-19'
        'Eleanor' | 'Derek'   | '01-20'
        'Tahani'  | 'Chidi'   | '01-21'
        'Michael' | 'Eleanor' | '01-22'
        'Janet'   | 'Jason'   | '01-23'
        'Derek'   | 'Tahani'  | '01-24'
        'Chidi'   | 'Michael' | '01-25'
        'Eleanor' | 'Janet'   | '01-26'
        'Jason'   | 'Derek'   | '01-27'
        'Michael' | 'Chidi'   | '01-28'
        'Janet'   | 'Eleanor' | '01-29'
        'Derek'   | 'Jason'   | '01-30'
        'Chidi'   | 'Tahani'  | '01-31'
        'Eleanor' | 'Michael' | '02-01'
        'Jason'   | 'Janet'   | '02-02'
        'Tahani'  | 'Derek'   | '02-03'
        'Janet'   | 'Chidi'   | '02-04'
        'Derek'   | 'Eleanor' | '02-05'
        'Chidi'   | 'Jason'   | '02-06'
        'Eleanor' | 'Tahani'  | '02-07'
        'Jason'   | 'Michael' | '02-08'
        'Tahani'  | 'Janet'   | '02-09'
        'Michael' | 'Derek'   | '02-10'
        'Derek'   | 'Chidi'   | '02-11'
    }
}
