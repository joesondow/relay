package sondow.twitter

import java.time.ZonedDateTime
import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification
import twitter4j.conf.Configuration

class AccountChooserSpec extends Specification {

    @Rule
    public final EnvironmentVariables envVars = new EnvironmentVariables()

    def "should choose target based on day"() {
        setup:
        Configuration tc = Mock()
        BotConfig botConfig = new BotConfig(null,
                [Scrooge: tc, Launchpad: tc, Darkwing: tc, Gosling: tc, Huey: tc, Duey: tc, Louie:
                        tc] as LinkedHashMap, [])
        Time time = Mock()
        AccountChooser accountChooser = new AccountChooser(botConfig, time)

        when:
        String target = accountChooser.chooseTarget()

        then:
        target == result
        1 * time.now() >> ZonedDateTime.parse('2020-' + date + 'T02:36:00Z')

        where:
        result      | date
        'Launchpad' | '01-01'
        'Darkwing'  | '01-02'
        'Gosling'   | '01-03'
        'Huey'      | '01-04'
        'Duey'      | '01-05'
        'Louie'     | '01-06'
        'Scrooge'   | '01-07'
        'Launchpad' | '01-08'
        'Darkwing'  | '01-09'
        'Scrooge'   | '06-09'
        'Launchpad' | '06-10'
        'Darkwing'  | '06-11'
        'Gosling'   | '06-12'
        'Launchpad' | '12-30'
        'Darkwing'  | '12-31'
    }
}
