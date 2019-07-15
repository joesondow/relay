package sondow.twitter;

import twitter4j.Status;

public interface Criteria {

    boolean matches(Status tweet);

    String screenName();
}
