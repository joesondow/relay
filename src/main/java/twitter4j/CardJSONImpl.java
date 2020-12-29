package twitter4j;

import java.time.Instant;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import twitter4j.conf.Configuration;

/**
 * Bean to hold data about an entire twitter card that has already been tweeted. Data includes twitter poll data.
 * Compare with CardLightJSONImpl.
 */
public class CardJSONImpl extends TwitterResponseImpl implements Card, java.io.Serializable {
    private static final long serialVersionUID = 2797252984453290489L;

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(StatusJSONImpl.class);

    /**
     * Usually "poll2choice_text_only" or "poll3choice_text_only" or "poll4choice_text_only"
     */
    private String name;
    private String cardUrl; // This is always "https://twitter.com" for a poll.
    private String api; // This is always "capi://passthrough/1" for a poll.
    private int durationMinutes;
    private boolean countsAreFinal;
    private Instant lastUpdatedDateTimeUtc;
    private Instant endDatetimeUtc;
    private Choice[] choices;

    /**
     * Unique resource indicator for card. Called "card_uri" in light json, "url" in full json.
     *
     * Format is "card://<a twitter id>" such as "card://851038416242266113"
     */
    private String url;

    /* package */ CardJSONImpl(HttpResponse res, Configuration conf) throws TwitterException {
        super(res);
        JSONObject json = res.asJSONObject();
        init(json);
        if (conf.isJSONStoreEnabled()) {
            TwitterObjectFactory.clearThreadLocalMap();
            TwitterObjectFactory.registerJSONObject(this, json);
        }
    }

    /* package */ CardJSONImpl(JSONObject json, Configuration conf) throws TwitterException {
        super();
        init(json);
        if (conf.isJSONStoreEnabled()) {
            TwitterObjectFactory.registerJSONObject(this, json);
        }
    }

    /* package */ CardJSONImpl(JSONObject json) throws TwitterException {
        super();
        init(json);
    }

    /* Only for serialization purposes. */
    /* package */ CardJSONImpl() {

    }

    private void init(JSONObject json) throws TwitterException {

        // Light card json calls this "card_uri". Full card json calls it "url".
        url = ParseUtil.getUnescapedString("url", json);
        name = ParseUtil.getUnescapedString("name", json);
        Integer choiceCount = -1;

        Pattern p = Pattern.compile("poll([0-9])choice_text_only"); // poll2choice_text_only
        Matcher matcher = p.matcher(name);
        if (matcher.matches()) {
            choiceCount = Integer.parseInt(matcher.group(1));
        }

        if (!json.isNull("binding_values")) {
            try {
                JSONObject bindingValuesJson = json.getJSONObject("binding_values");

                if (!bindingValuesJson.isNull("duration_minutes")) {
                    JSONObject durationJson = bindingValuesJson.getJSONObject("duration_minutes");
                    durationMinutes = ParseUtil.getInt("string_value", durationJson);
                }
                if (!bindingValuesJson.isNull("counts_are_final")) {
                    JSONObject countsAreFinalJson = bindingValuesJson.getJSONObject("counts_are_final");
                    countsAreFinal = ParseUtil.getBoolean("boolean_value", countsAreFinalJson);
                }
                if (!bindingValuesJson.isNull("card_url")) {
                    JSONObject cardUrlJson = bindingValuesJson.getJSONObject("card_url");
                    cardUrl = ParseUtil.getUnescapedString("string_value", cardUrlJson);
                }
                if (!bindingValuesJson.isNull("api")) {
                    JSONObject apiJson = bindingValuesJson.getJSONObject("api");
                    api = ParseUtil.getUnescapedString("string_value", apiJson);
                }
                if (!bindingValuesJson.isNull("last_updated_datetime_utc")) {
                    JSONObject lastUpdatedJson = bindingValuesJson.getJSONObject("last_updated_datetime_utc");
                    String lastUpdatedDateUtcString = ParseUtil.getUnescapedString("string_value", lastUpdatedJson);
                    lastUpdatedDateTimeUtc = Instant.parse(lastUpdatedDateUtcString);
                }
                if (!bindingValuesJson.isNull("end_datetime_utc")) {
                    JSONObject endJson = bindingValuesJson.getJSONObject("end_datetime_utc");
                    String endDatetimeUtcString = ParseUtil.getUnescapedString("string_value", endJson);
                    endDatetimeUtc = Instant.parse(endDatetimeUtcString);
                }

                if (choiceCount >= 1) {

                    choices = new Choice[choiceCount];
                    for (int i = 0; i < choiceCount; i++) {
                        String labelKey = "choice" + (i + 1) + "_label";
                        String countKey = "choice" + (i + 1) + "_count";
                        if (!bindingValuesJson.isNull(labelKey)) {
                            JSONObject labelJson = bindingValuesJson.getJSONObject(labelKey);
                            String label = ParseUtil.getUnescapedString("string_value", labelJson);
                            Integer count = 0;
                            if (!bindingValuesJson.isNull(countKey)) {
                                JSONObject countJson = bindingValuesJson.getJSONObject(countKey);
                                count = ParseUtil.getInt("string_value", countJson);
                            }
                            Choice choice = new Choice(label, count);
                            choices[i] = choice;
                        }
                    }
                }

            } catch (JSONException jsone) {
                throw new TwitterException(jsone);
            }
        }

    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the cardUrl
     */
    @Override
    public String getCardUrl() {
        return cardUrl;
    }

    /**
     * @return the api
     */
    @Override
    public String getApi() {
        return api;
    }

    /**
     * @return the durationMinutes
     */
    @Override
    public int getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * @return the countsAreFinal
     */
    @Override
    public boolean isCountsAreFinal() {
        return countsAreFinal;
    }

    /**
     * @return the lastUpdatedDateTimeUtc
     */
    @Override
    public Instant getLastUpdatedDateTimeUtc() {
        return lastUpdatedDateTimeUtc;
    }

    /**
     * @return the endDatetimeUtc
     */
    @Override
    public Instant getEndDatetimeUtc() {
        return endDatetimeUtc;
    }

    /**
     * @return the choices
     */
    @Override
    public Choice[] getChoices() {
        return choices;
    }

    /**
     * @return the unique identifier of the card
     */
    @Override
    public String getUrl() {
        return url;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Poll [name=" + name + ", cardUrl=" + cardUrl + ", api=" + api + ", durationMinutes="
                + durationMinutes + ", countsAreFinal=" + countsAreFinal + ", lastUpdatedDateTimeUtc="
                + lastUpdatedDateTimeUtc + ", endDatetimeUtc=" + endDatetimeUtc + ", choices="
                + Arrays.toString(choices) + ", url=" + url + "]";
    }

}
