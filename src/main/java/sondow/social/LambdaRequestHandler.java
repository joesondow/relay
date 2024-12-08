package sondow.social;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import net.socialhub.http.HttpClientImpl;
import net.socialhub.logger.Logger;

/**
 * The function that AWS Lambda will invoke.
 *
 * @author @JoeSondow
 */
public class LambdaRequestHandler implements RequestHandler<Object, Object> {

    private final Bot bot;

    /**
     * This constructor gets called by AWS Lambda.
     */
    @SuppressWarnings("unused") public LambdaRequestHandler() {
        this(new Bot());
    }

    /**
     * This constructor gets called by a unit test.
     *
     * @param bot the bot to run
     */
    LambdaRequestHandler(Bot bot) {
        this.bot = bot;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.amazonaws.services.lambda.runtime.RequestHandler#handleRequest(java. lang.Object,
     * com.amazonaws.services.lambda.runtime.Context)
     */
    @Override
    public Object handleRequest(Object input, Context context) {

        // bsky4j logs session credentials, which is bad security. Turn it off.
        Logger.getLogger(HttpClientImpl.class).setLogLevel(Logger.LogLevel.ERROR);

        bot.go();
        return null;
    }

    public static void main(String[] args) {
        new LambdaRequestHandler().handleRequest(null, null);
    }
}
