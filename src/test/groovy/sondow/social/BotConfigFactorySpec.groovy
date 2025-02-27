package sondow.social

import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Specification
import twitter4j.conf.Configuration

class BotConfigFactorySpec extends Specification {

    @Rule
    public final EnvironmentVariables envVars = new EnvironmentVariables()

    def "configure should populate a configuration with environment variables"() {
        setup:
        String f = Environment.SPACE_FILLER
        String val = "${f}${f}cartoons,mickeymouse,georgejetson,fredflintstone,bugsbunny${f}${f}"
        envVars.set("cred_twitter_poll_reading", val)
        envVars.set("bluesky_short_handles", "${f}Kramer")
        envVars.set("mastodon_short_handles", "${f}Scooby")
        envVars.set("target_accounts", "Kramer${f}")
        envVars.set("cred_Kramer", "${f}Kramer,a,b,c,d${f}")
        envVars.set("cred_bluesky_Kramer", "${f}${f}bsky.social,kramer,myapppassword")
        envVars.set("cred_mastodon_Scooby", "${f}${f}mas.to,Scooby,myaccesstoken")
        envVars.set("poll_accounts", "")
        Keymaster keymaster = Mock()
        Environment environment = new Environment(keymaster)
        BotConfigFactory factory = new BotConfigFactory(environment)
        BotConfig botConfig = factory.configure()

        when:
        LinkedHashMap<String, BlueskyConfig> blueskyShortHandlesToConfigs =
                botConfig.getBlueskyShortHandlesToConfigs()
        LinkedHashMap<String, MastodonConfig> mastodonShortHandlesToConfigs =
                botConfig.getMastodonShortHandlesToConfigs()

        then:
        blueskyShortHandlesToConfigs["Kramer"].server == 'bsky.social'
        blueskyShortHandlesToConfigs["Kramer"].shortHandle == 'kramer'
        blueskyShortHandlesToConfigs["Kramer"].fullHandle == 'kramer.bsky.social'
        blueskyShortHandlesToConfigs["Kramer"].appPassword == 'myapppassword'
        mastodonShortHandlesToConfigs["Scooby"].mastodonInstanceName == 'mas.to'
        mastodonShortHandlesToConfigs["Scooby"].mastodonAccessToken == 'myaccesstoken'
    }
}
