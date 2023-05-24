package sondow.social

import spock.lang.Specification

class EncryptorSpec extends Specification {

    def 'encryptor should replace most characters with alternate characters based on key'() {
        when:
        Encryptor encryptor = new Encryptor()
        String encrypted = encryptor.encrypt(source, key)

        then:
        encrypted == result

        where:
        key            | source                 | result
        "TrIwen"       | "moondo'ogypoorMONKEY" | "^6W0h2'6OKt2*9c_RXf;"
        "A"            | "Frank"                | ";RANK"
        "*jn434ifn^hf" | ""                     | ""
        "*jn434ifn^hf" | "jsmith"               | ":2zBLA"
        " "            | "smight"               | "smight"
    }

    def 'should decrypt'() {
        when:
        Encryptor encryptor = new Encryptor()
        String decrypted = encryptor.decrypt(source, key)

        then:
        decrypted == result

        where:
        key            | result                 | source
        "TrIwen"       | "moondo'ogypoorMONKEY" | "^6W0h2'6OKt2*9c_RXf;"
        "A"            | "Frank"                | ";RANK"
        "*jn434ifn^hf" | ""                     | ""
        "*jn434ifn^hf" | "jsmith"               | ":2zBLA"
        " "            | "smight"               | "smight"
        "?"            | "k"                    | "e"
    }
}
