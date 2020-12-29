package twitter4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import twitter4j.auth.Authorization;
import twitter4j.conf.Configuration;

public class CardsTwitterImpl extends TwitterImpl {

    private static final long serialVersionUID = -7163515040560472682L;
    final String IMPLICIT_PARAMS_STR;
    final HttpParameter[] IMPLICIT_PARAMS;
    final HttpParameter INCLUDE_MY_RETWEET;

    static final ConcurrentHashMap<Configuration, HttpParameter[]> implicitParamsMap = new ConcurrentHashMap<Configuration, HttpParameter[]>();
    static final ConcurrentHashMap<Configuration, String> implicitParamsStrMap = new ConcurrentHashMap<Configuration, String>();

    public CardsTwitterImpl(Configuration conf, Authorization auth) {
        super(conf, auth);
        // This following bit is copied wholesale from the parent class, as a shortcut.
        INCLUDE_MY_RETWEET = new HttpParameter("include_my_retweet", conf.isIncludeMyRetweetEnabled());
        if (implicitParamsMap.containsKey(conf)) {
            this.IMPLICIT_PARAMS = implicitParamsMap.get(conf);
            this.IMPLICIT_PARAMS_STR = implicitParamsStrMap.get(conf);
        } else {
            String implicitParamsStr = conf.isIncludeEntitiesEnabled() ? "include_entities=" + true : "";
            boolean contributorsEnabled = conf.getContributingTo() != -1L;
            if (contributorsEnabled) {
                if (!"".equals(implicitParamsStr)) {
                    implicitParamsStr += "&";
                }
                implicitParamsStr += "contributingto=" + conf.getContributingTo();
            }

            if (conf.isTweetModeExtended()) {
                if (!"".equals(implicitParamsStr)) {
                    implicitParamsStr += "&";
                }
                implicitParamsStr += "tweet_mode=extended";
            }

            List<HttpParameter> params = new ArrayList<HttpParameter>(3);
            if (conf.isIncludeEntitiesEnabled()) {
                params.add(new HttpParameter("include_entities", "true"));
            }
            if (contributorsEnabled) {
                params.add(new HttpParameter("contributingto", conf.getContributingTo()));
            }
            if (conf.isTrimUserEnabled()) {
                params.add(new HttpParameter("trim_user", "1"));
            }
            if (conf.isIncludeExtAltTextEnabled()) {
                params.add(new HttpParameter("include_ext_alt_text", "true"));
            }
            if (conf.isTweetModeExtended()) {
                params.add(new HttpParameter("tweet_mode", "extended"));
            }
            HttpParameter[] implicitParams = params.toArray(new HttpParameter[params.size()]);

            // implicitParamsMap.containsKey() is evaluated in the above if clause.
            // thus implicitParamsStrMap needs to be initialized first
            implicitParamsStrMap.putIfAbsent(conf, implicitParamsStr);
            implicitParamsMap.putIfAbsent(conf, implicitParams);

            this.IMPLICIT_PARAMS = implicitParams;
            this.IMPLICIT_PARAMS_STR = implicitParamsStr;
        }

        spoofClientThatIsAllowedToMakeCards();
    }

    public StatusWithCard showStatusWithCard(long id) throws TwitterException {
        HttpResponse response = get(conf.getRestBaseURL() + "statuses/show/" + id + ".json",
                new HttpParameter[] { INCLUDE_MY_RETWEET, new HttpParameter("cards_platform", "iPhone-13"),
                        new HttpParameter("include_cards", "1") });

        return new StatusWithCardJSONImpl(response, conf);
    }

    private void spoofClientThatIsAllowedToMakeCards() {
        http.addDefaultRequestHeader("Accept", "*/*");
        http.addDefaultRequestHeader("User-Agent", "Twitter-iPhone/6.45 iOS/9.0.2 (Apple;iPhone8,2;;;;;1)");
        http.addDefaultRequestHeader("X-Twitter-Client", "Twitter-iPhone");
        http.addDefaultRequestHeader("X-Twitter-API-Version", "5");
        http.addDefaultRequestHeader("X-Twitter-Client-Language", "en");
        http.addDefaultRequestHeader("X-Twitter-Client-Version", "6.45");
    }

    public CardLight createCard(Map<String, String> params) throws TwitterException {
        JSONObject jsonObject = new JSONObject(params);

        HttpResponse response = post("https://caps.twitter.com/v2/" + "cards/create",
                new HttpParameter[] { new HttpParameter("card_data", jsonObject.toString()) });

        return new CardLightJSONImpl(response, conf);
    }

    public Status updateStatus(StatusUpdateWithCard status) throws TwitterException {
        String url = conf.getRestBaseURL() + "statuses/update.json";
        HttpResponse response = post(url, status.asHttpParameterArray());
//        System.out.print("Success... ");
//        new Debug().logHttpResponse(response);
        return factory.createStatus(response);
    }

    // Copied from parent class
    private HttpResponse get(String url, HttpParameter... params) throws TwitterException {
        ensureAuthorizationEnabled();
        if (!conf.isMBeanEnabled()) {
            return http.get(url, mergeImplicitParams(params), auth, this);
        } else {
            // intercept HTTP call for monitoring purposes
            HttpResponse response = null;
            long start = System.currentTimeMillis();
            try {
                response = http.get(url, mergeImplicitParams(params), auth, this);
            } finally {
                long elapsedTime = System.currentTimeMillis() - start;
                TwitterAPIMonitor.getInstance().methodCalled(url, elapsedTime, isOk(response));
            }
            return response;
        }
    }

    // Copied from parent class.
    HttpResponse post(String url, HttpParameter... params) throws TwitterException {
        ensureAuthorizationEnabled();
        if (!conf.isMBeanEnabled()) {
            return http.post(url, mergeImplicitParams(params), auth, this);
        } else {
            // intercept HTTP call for monitoring purposes
            HttpResponse response = null;
            long start = System.currentTimeMillis();
            try {
                response = http.post(url, mergeImplicitParams(params), auth, this);
            } finally {
                long elapsedTime = System.currentTimeMillis() - start;
                TwitterAPIMonitor.getInstance().methodCalled(url, elapsedTime, isOk(response));
            }
            return response;
        }
    }

    // Copied from parent class.
    HttpParameter[] mergeImplicitParams(HttpParameter... params) {
        return mergeParameters(params, IMPLICIT_PARAMS);
    }

    // Copied from parent class.
    HttpParameter[] mergeParameters(HttpParameter[] params1, HttpParameter[] params2) {
        if (params1 != null && params2 != null) {
            HttpParameter[] params = new HttpParameter[params1.length + params2.length];
            System.arraycopy(params1, 0, params, 0, params1.length);
            System.arraycopy(params2, 0, params, params1.length, params2.length);
            return params;
        }
        if (null == params1 && null == params2) {
            return new HttpParameter[0];
        }
        if (params1 != null) {
            return params1;
        } else {
            return params2;
        }
    }

    // Copied from parent class.
    boolean isOk(HttpResponse response) {
        return response != null && response.getStatusCode() < 300;
    }
}
