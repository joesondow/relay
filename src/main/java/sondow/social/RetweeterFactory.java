package sondow.social;

import twitter4j.conf.Configuration;

/**
 * This helps make the code testable.
 */
public class RetweeterFactory {

    public Retweeter build(Configuration targetConfig) {
        return new Retweeter(targetConfig);
    }
}
