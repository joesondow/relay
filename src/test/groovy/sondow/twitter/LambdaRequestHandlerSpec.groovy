package sondow.twitter

import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables

import com.amazonaws.services.lambda.runtime.Context

import spock.lang.Specification
import twitter4j.conf.Configuration

class LambdaRequestHandlerSpec extends Specification {

    @Rule
    public final EnvironmentVariables envVars = new EnvironmentVariables()

    def "handleRequest should make the bot go"() {
        setup:
        Bot bot = Mock()
        Context context = Mock()
        LambdaRequestHandler handler = new LambdaRequestHandler(bot)

        when:
        handler.handleRequest(new Object(), context)

        then:
        1 * bot.go()
        0 * _._
    }
}
