package agroconnectapp.agroconnect.in.agroconnect.entities;

/**
 * Created by sumanta on 22/7/16.
 */
public class NotificationEntity {
    private String message;
    private String id;
    private String feedId;
    private String agentId;
    private String cityId;
    private String commodityId;
    private String advisoryPostId;
    private String feedType;
    private long timestamp;

    public NotificationEntity() {}

    public NotificationEntity(String message, String id, String agentId, String cityId, String commodityId, String advisoryPostId, long timestamp) {
        this.message = message;
        this.id = id;
        this.agentId = agentId;
        this.cityId = cityId;
        this.commodityId = commodityId;
        this.advisoryPostId = advisoryPostId;
        this.timestamp = timestamp;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(String commodityId) {
        this.commodityId = commodityId;
    }

    public String getAdvisoryPostId() {
        return advisoryPostId;
    }

    public void setAdvisoryPostId(String advisoryPostId) {
        this.advisoryPostId = advisoryPostId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
