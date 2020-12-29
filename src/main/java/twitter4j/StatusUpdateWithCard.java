package twitter4j;

import java.util.ArrayList;
import java.util.List;

public final class StatusUpdateWithCard implements java.io.Serializable {

    private static final long serialVersionUID = 5665502492269993250L;
    private final String status;
    private String cardUri;
    // private long includeCards = -1L;
    // private String cardsPlatform;
    // private long contributorDetails;
    private long inReplyToStatusId = -1L;
    private GeoLocation location = null;
    private String placeId = null;
    private boolean displayCoordinates = true;

    public StatusUpdateWithCard(String status, String cardUri) {
        this.status = status;
        this.cardUri = cardUri;
    }

    public String getStatus() {
        return status;
    }

    public long getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public void setInReplyToStatusId(long inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public StatusUpdateWithCard inReplyToStatusId(long inReplyToStatusId) {
        setInReplyToStatusId(inReplyToStatusId);
        return this;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public StatusUpdateWithCard location(GeoLocation location) {
        setLocation(location);
        return this;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public StatusUpdateWithCard placeId(String placeId) {
        setPlaceId(placeId);
        return this;
    }

    public boolean isDisplayCoordinates() {
        return displayCoordinates;
    }

    public void setDisplayCoordinates(boolean displayCoordinates) {
        this.displayCoordinates = displayCoordinates;
    }

    public StatusUpdateWithCard displayCoordinates(boolean displayCoordinates) {
        setDisplayCoordinates(displayCoordinates);
        return this;
    }

    /* package */ HttpParameter[] asHttpParameterArray() {
        ArrayList<HttpParameter> params = new ArrayList<HttpParameter>();
        appendParameter("status", status, params);
        if (-1 != inReplyToStatusId) {
            appendParameter("in_reply_to_status_id", inReplyToStatusId, params);
        }
        if (location != null) {
            appendParameter("lat", location.getLatitude(), params);
            appendParameter("long", location.getLongitude(), params);

        }
        appendParameter("place_id", placeId, params);
        if (!displayCoordinates) {
            appendParameter("display_coordinates", "false", params);
        }
        if (cardUri != null) {
            appendParameter("card_uri", cardUri, params);
            appendParameter("includeCards", "1", params);
            appendParameter("cards_platform", "iPhone-13", params);
            appendParameter("contributor_details", "1", params);
        }

        HttpParameter[] paramArray = new HttpParameter[params.size()];
        return params.toArray(paramArray);
    }

    private void appendParameter(String name, String value, List<HttpParameter> params) {
        if (value != null) {
            params.add(new HttpParameter(name, value));
        }
    }

    private void appendParameter(String name, double value, List<HttpParameter> params) {
        params.add(new HttpParameter(name, String.valueOf(value)));
    }

    private void appendParameter(String name, long value, List<HttpParameter> params) {
        params.add(new HttpParameter(name, String.valueOf(value)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        StatusUpdateWithCard that = (StatusUpdateWithCard) o;

        if (displayCoordinates != that.displayCoordinates)
            return false;
        if (inReplyToStatusId != that.inReplyToStatusId)
            return false;
        if (location != null ? !location.equals(that.location) : that.location != null)
            return false;
        if (placeId != null ? !placeId.equals(that.placeId) : that.placeId != null)
            return false;
        if (status != null ? !status.equals(that.status) : that.status != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (int) (inReplyToStatusId ^ (inReplyToStatusId >>> 32));
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (placeId != null ? placeId.hashCode() : 0);
        result = 31 * result + (displayCoordinates ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StatusUpdateWithCard{" + "status='" + status + '\'' + ", inReplyToStatusId=" + inReplyToStatusId
                + ", location=" + location + ", placeId='" + placeId + '\'' + ", displayCoordinates="
                + displayCoordinates + '\'' + '}';
    }
}
