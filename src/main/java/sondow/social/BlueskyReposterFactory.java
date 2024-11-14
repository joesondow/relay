package sondow.social;


public class BlueskyReposterFactory {

    public BlueskyReposter build(BlueskyConfig targetConfig) {
        return new BlueskyReposter(targetConfig);
    }
}
