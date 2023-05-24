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
        BotConfig botConfig = new BotConfig(null,
                [Scrooge: tc, Launchpad: tc, Darkwing: tc, Gosling: tc, Huey: tc, Duey: tc, Louie:
                        tc] as LinkedHashMap, [])
        Time time = Mock()
        AccountChooser accountChooser = new AccountChooser(botConfig, time)

        when:
        PromoterAndTarget promoterAndTarget = accountChooser.choosePromoterAndTarget()

        then:
        promoterAndTarget.getPromoter() == promoter
        promoterAndTarget.getTarget() == target
        1 * time.nowUtc() >> ZonedDateTime.parse('2020-' + date + 'T02:36:00Z')

        where:
        promoter    | target      | date
        'Launchpad' | 'Darkwing'  | '01-01'
        'Darkwing'  | 'Gosling'   | '01-02'
        'Gosling'   | 'Huey'      | '01-03'
        'Huey'      | 'Duey'      | '01-04'
        'Duey'      | 'Louie'     | '01-05'
        'Louie'     | 'Scrooge'   | '01-06'
        'Launchpad' | 'Scrooge'   | '01-07'
        'Darkwing'  | 'Launchpad' | '01-08'
        'Gosling'   | 'Darkwing'  | '01-09'
        'Huey'      | 'Gosling'   | '01-10'
        'Duey'      | 'Huey'      | '01-11'
        'Louie'     | 'Duey'      | '01-12'
        'Scrooge'   | 'Louie'     | '01-13'
        'Darkwing'  | 'Scrooge'   | '01-14'
        'Gosling'   | 'Launchpad' | '01-15'
        'Huey'      | 'Darkwing'  | '01-16'
        'Duey'      | 'Gosling'   | '01-17'
        'Louie'     | 'Huey'      | '01-18'
        'Scrooge'   | 'Duey'      | '01-19'
        'Launchpad' | 'Louie'     | '01-20'
        'Gosling'   | 'Scrooge'   | '01-21'
        'Huey'      | 'Launchpad' | '01-22'
        'Duey'      | 'Darkwing'  | '01-23'
        'Louie'     | 'Gosling'   | '01-24'
        'Scrooge'   | 'Huey'      | '01-25'
        'Launchpad' | 'Duey'      | '01-26'
        'Darkwing'  | 'Louie'     | '01-27'
        'Huey'      | 'Scrooge'   | '01-28'
        'Duey'      | 'Launchpad' | '01-29'
        'Louie'     | 'Darkwing'  | '01-30'
        'Scrooge'   | 'Gosling'   | '01-31'
        'Launchpad' | 'Huey'      | '02-01'
        'Darkwing'  | 'Duey'      | '02-02'
        'Gosling'   | 'Louie'     | '02-03'
        'Duey'      | 'Scrooge'   | '02-04'
        'Louie'     | 'Launchpad' | '02-05'
        'Scrooge'   | 'Darkwing'  | '02-06'
        'Launchpad' | 'Gosling'   | '02-07'
        'Darkwing'  | 'Huey'      | '02-08'
        'Gosling'   | 'Duey'      | '02-09'
        'Huey'      | 'Louie'     | '02-10'
        'Louie'     | 'Scrooge'   | '02-11'
    }
}
