package sondow.social

import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification

class EnvironmentSpec extends Specification {

    @Rule
    public final EnvironmentVariables envVars = new EnvironmentVariables()

    def "Environment.get should strip prefix if present once"() {
        setup:
        envVars.set("twitter_access_id", "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZmoondoggy")
        envVars.set("twitter_handle", "SchoolsOfFish")
        Keymaster keymaster = Mock()
        Environment environment = new Environment(keymaster)

        expect:
        "moondoggy" == environment.get("twitter_access_id")
        "SchoolsOfFish" == environment.get("twitter_handle")
    }

    def "Environment.get should strip prefix if present twice"() {
        setup:
        envVars.set("twitter_access_id",
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZmoondoggy")
        envVars.set("twitter_handle", "SchoolsOfFish")
        Keymaster keymaster = Mock()
        Environment environment = new Environment(keymaster)

        expect:
        "moondoggy" == environment.get("twitter_access_id")
        "SchoolsOfFish" == environment.get("twitter_handle")
    }

    def "Environment.get should strip prefix if present thrice"() {
        setup:
        envVars.set("twitter_access_id",
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                        "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZmoondoggy")
        envVars.set("twitter_handle", "SchoolsOfFish")
        Keymaster keymaster = Mock()
        Environment environment = new Environment(keymaster)

        expect:
        "moondoggy" == environment.get("twitter_access_id")
        "SchoolsOfFish" == environment.get("twitter_handle")
    }

    def "Environment.get should strip prefix if present quarce"() {
        setup:
        envVars.set("twitter_access_id",
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                        "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                        "moondoggy")
        envVars.set("twitter_handle", "SchoolsOfFish")
        Keymaster keymaster = Mock()
        Environment environment = new Environment(keymaster)

        expect:
        "moondoggy" == environment.get("twitter_access_id")
        "SchoolsOfFish" == environment.get("twitter_handle")
    }

    def "Environment.get should use default value if variable not set"() {
        setup:
        Keymaster keymaster = Mock()
        Environment environment = new Environment(keymaster)

        expect:
        "sunny" == environment.get("nothing_here", "sunny")
    }

    def "getInt should return default value if variable not set"() {
        setup:
        envVars.set("width", "1400")
        Keymaster keymaster = Mock()
        Environment environment = new Environment(keymaster)

        expect:
        1400 == environment.getInt("width", 800)
        600 == environment.getInt("height", 600)
    }

    def "getInt should throw NumberFormatException if value is not an integer"() {
        setup:
        envVars.set("width", "wide")
        Keymaster keymaster = Mock()
        Environment environment = new Environment(keymaster)

        when:
        environment.getInt("width", 800)

        then:
        thrown NumberFormatException
    }
}
