package twitter4j;

import twitter4j.conf.Configuration;

/**
 * A bean for holding data from a twitter card json string that only contains the bare minimum information to
 * locate a card by its uri. See CardJSONImpl for full card bean including poll answers.
 *
 * Format of json to point to a card right after card creation:
 * {"card_uri":"card://851038416242266113","status":"OK"}
 *
 * @author @JoeSondow
 */
public class CardLightJSONImpl extends TwitterResponseImpl implements CardLight, java.io.Serializable {

    private static final long serialVersionUID = 1750425313422276581L;

    /**
     * Unique resource indicator for card. Called "card_uri" in light json, "url" in full json.
     *
     * Format is "card://<a twitter id>" such as "card://851038416242266113"
     */
    private String cardUri;// format is "card://<a twitter id>" such as "card://851038416242266113"
    private String status; // expected value is "OK"

    /* package */ CardLightJSONImpl(HttpResponse res, Configuration conf) throws TwitterException {
        super(res);
        JSONObject json = res.asJSONObject();
        init(json);
        if (conf.isJSONStoreEnabled()) {
            TwitterObjectFactory.clearThreadLocalMap();
            TwitterObjectFactory.registerJSONObject(this, json);
        }
    }

    /* package */ CardLightJSONImpl(JSONObject json, Configuration conf) throws TwitterException {
        super();
        init(json);
        if (conf.isJSONStoreEnabled()) {
            TwitterObjectFactory.registerJSONObject(this, json);
        }
    }

    /* package */ CardLightJSONImpl(JSONObject json) throws TwitterException {
        super();
        init(json);
    }

    /* Only for serialization purposes. */
    /* package */ CardLightJSONImpl() {

    }

    private void init(JSONObject json) throws TwitterException {
        // Light card json calls this "card_uri". Full card json calls it "url".
        cardUri = ParseUtil.getUnescapedString("card_uri", json);
        status = ParseUtil.getUnescapedString("status", json);
    }

    /**
     * @return the unique address of the card
     */
    @Override
    public String getCardUri() {
        return cardUri;
    }

    /**
     * @param setCardUri the unique address of the card
     */
    @Override
    public void setCardUri(String setCardUri) {
        this.cardUri = setCardUri;
    }

    /**
     * @return the status
     */
    @Override
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    @Override
    public void setStatus(String status) {
        this.status = status;
    }

}
