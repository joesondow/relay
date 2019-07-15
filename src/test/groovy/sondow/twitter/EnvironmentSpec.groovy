package sondow.twitter

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

        expect:
        "moondoggy" == Environment.get("twitter_access_id")
        "SchoolsOfFish" == Environment.get("twitter_handle")
    }

    def "Environment.get should strip prefix if present twice"() {
        setup:
        envVars.set("twitter_access_id",
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZmoondoggy")
        envVars.set("twitter_handle", "SchoolsOfFish")

        expect:
        "moondoggy" == Environment.get("twitter_access_id")
        "SchoolsOfFish" == Environment.get("twitter_handle")
    }

    def "Environment.get should strip prefix if present thrice"() {
        setup:
        envVars.set("twitter_access_id",
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                        "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZmoondoggy")
        envVars.set("twitter_handle", "SchoolsOfFish")

        expect:
        "moondoggy" == Environment.get("twitter_access_id")
        "SchoolsOfFish" == Environment.get("twitter_handle")
    }

    def "Environment.get should strip prefix if present quarce"() {
        setup:
        envVars.set("twitter_access_id",
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                        "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                        "moondoggy")
        envVars.set("twitter_handle", "SchoolsOfFish")

        expect:
        "moondoggy" == Environment.get("twitter_access_id")
        "SchoolsOfFish" == Environment.get("twitter_handle")
    }

    def "Environment.get should strip suffix if present once"() {
        setup:
        envVars.set("twitter_access_id", "moondoggy1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ")
        envVars.set("twitter_handle", "SchoolsOfFish")

        expect:
        "moondoggy" == Environment.get("twitter_access_id")
        "SchoolsOfFish" == Environment.get("twitter_handle")
    }

    def "Environment.get should strip suffix if present twice"() {
        setup:
        envVars.set("twitter_access_id",
                "moondoggy1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ")
        envVars.set("twitter_handle", "SchoolsOfFish")

        expect:
        "moondoggy" == Environment.get("twitter_access_id")
        "SchoolsOfFish" == Environment.get("twitter_handle")
    }

    def "Environment.get should strip suffix if present quarce"() {
        setup:
        envVars.set("twitter_access_id", "moondoggy1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ")
        envVars.set("twitter_handle", "SchoolsOfFish")

        expect:
        "moondoggy" == Environment.get("twitter_access_id")
        "SchoolsOfFish" == Environment.get("twitter_handle")
    }

    def "Environment.get should strip prefix and suffix if present"() {
        setup:
        envVars.set("twitter_access_id",
                "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                        "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZmoondoggy" +
                        "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                        "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ")
        envVars.set("twitter_handle", "SchoolsOfFish")

        expect:
        "moondoggy" == Environment.get("twitter_access_id")
        "SchoolsOfFish" == Environment.get("twitter_handle")
    }

    def "Environment.get should use default value if variable not set"() {

        expect:
        "sunny" == Environment.get("nothing_here", "sunny")
    }

    def "getInt should return null if variable not set"() {
        setup:
        envVars.set("width", "1400")

        expect:
        Environment.getInt("width") == 1400
        Environment.getInt("height") == null
    }

    def "getInt should throw NumberFormatException if value is not an integer"() {
        setup:
        envVars.set("width", "wide")

        when:
        Environment.getInt("width")

        then:
        thrown NumberFormatException
    }
}
