package twitter4j;

public interface CardLight {

    /**
     * @return the url
     */
    public String getCardUri();

    /**
     * @param url the url to set
     */
    public void setCardUri(String url);

    /**
     * @return the status
     */
    public String getStatus();

    /**
     * @param status the status to set
     */
    public void setStatus(String status);

}
