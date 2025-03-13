package sondow.social;


public class MastodonReposterFactory {

    public MastodonReposter build(MastodonConfig targetConfig) {
        return new MastodonReposter(targetConfig);
    }
}
