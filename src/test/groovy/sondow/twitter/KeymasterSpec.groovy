package sondow.twitter

import spock.lang.Specification

class KeymasterSpec extends Specification {

    def "should get null encryption key if no json file found"() {
        setup:
        FileClerk fileClerk = Mock()
        Keymaster keymaster = new Keymaster(fileClerk)
        String buildPropertiesContents = 'root.project.name=castle'

        when:
        String key = keymaster.getCryptoKey("twitch_client_id")

        then:
        key == null
        1 * fileClerk.readTextFile('build.properties') >> buildPropertiesContents
        1 * fileClerk.readTextFile('castle-encryption-keys.json') >> null
        1 * fileClerk.readTextFile('../cipher/castle-encryption-keys.json') >> null
        0 * _._
    }

    def "should get null encryption key if internal json file lacks named key"() {
        setup:
        FileClerk fileClerk = Mock()
        Keymaster keymaster = new Keymaster(fileClerk)
        String buildPropertiesContents = 'root.project.name=castle'
        String keyFileContents = '{"secret_password":"AS#d8rgm(e4tpojf"}'

        when:
        String key = keymaster.getCryptoKey("twitch_client_id")

        then:
        key == null
        1 * fileClerk.readTextFile('build.properties') >> buildPropertiesContents
        1 * fileClerk.readTextFile('castle-encryption-keys.json') >> keyFileContents
        0 * _._
    }

    def "should get null encryption key if external json file lacks named key"() {
        setup:
        FileClerk fileClerk = Mock()
        Keymaster keymaster = new Keymaster(fileClerk)
        String buildPropertiesContents = 'root.project.name=castle'
        String keyFileContents = '{"secret_password":"AS#d8rgm(e4tpojf"}'

        when:
        String key = keymaster.getCryptoKey("twitch_client_id")

        then:
        key == null
        1 * fileClerk.readTextFile('build.properties') >> buildPropertiesContents
        1 * fileClerk.readTextFile('castle-encryption-keys.json') >> null
        1 * fileClerk.readTextFile('../cipher/castle-encryption-keys.json') >> keyFileContents
        0 * _._
    }

    def "should get encryption key if internal json file has named key"() {
        setup:
        FileClerk fileClerk = Mock()
        Keymaster keymaster = new Keymaster(fileClerk)
        String buildPropertiesContents = 'root.project.name=castle'
        String keyFileContents = '{"secret_password":"AS#d8rgm(e4tpojf",' +
                '"twitch_client_id":"dssdfj3jf84"}'

        when:
        String key = keymaster.getCryptoKey("twitch_client_id")

        then:
        key == "dssdfj3jf84"
        1 * fileClerk.readTextFile('build.properties') >> buildPropertiesContents
        1 * fileClerk.readTextFile('castle-encryption-keys.json') >> keyFileContents
        0 * _._
    }

    def "should get encryption key if external json file has named key"() {
        setup:
        FileClerk fileClerk = Mock()
        Keymaster keymaster = new Keymaster(fileClerk)
        String buildPropertiesContents = 'root.project.name=castle'
        String keyFileContents = '{"secret_password":"AS#d8rgm(e4tpojf",' +
                '"twitch_client_id":"dssdfj3jf84"}'

        when:
        String key = keymaster.getCryptoKey("twitch_client_id")

        then:
        key == "dssdfj3jf84"
        1 * fileClerk.readTextFile('build.properties') >> buildPropertiesContents
        1 * fileClerk.readTextFile('castle-encryption-keys.json') >> null
        1 * fileClerk.readTextFile('../cipher/castle-encryption-keys.json') >> keyFileContents
        0 * _._
    }

    def "should get only load keys json file once"() {
        setup:
        FileClerk fileClerk = Mock()
        Keymaster keymaster = new Keymaster(fileClerk)
        String buildPropertiesContents = 'root.project.name=castle'
        String keyFileContents = '{"secret_password":"AS#d8rgm(e4tpojf",' +
                '"twitch_client_id":"dssdfj3jf84"}'

        when:
        String key1 = keymaster.getCryptoKey("twitch_client_id")
        String key2 = keymaster.getCryptoKey("secret_password")

        then:
        key1 == "dssdfj3jf84"
        key2 == "AS#d8rgm(e4tpojf"
        1 * fileClerk.readTextFile('build.properties') >> buildPropertiesContents
        1 * fileClerk.readTextFile('castle-encryption-keys.json') >> null
        1 * fileClerk.readTextFile('../cipher/castle-encryption-keys.json') >> keyFileContents
        0 * _._
    }
}
