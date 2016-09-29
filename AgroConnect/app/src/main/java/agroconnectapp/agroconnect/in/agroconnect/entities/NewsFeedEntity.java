package agroconnectapp.agroconnect.in.agroconnect.entities;

/**
 * Created by sumanta on 23/6/16.
 */
public class NewsFeedEntity {
    private int id;
    private int feedType;
    private String sourceUrl;
    private String name;
    private String title;
    private String imageUrl;
    private String addedAt;
    private int likeCount;
    private int commentCount;
    private int agentType;
    private String city;



    public NewsFeedEntity() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFeedType() {
        return feedType;
    }

    public void setFeedType(int feedType) {
        this.feedType = feedType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getAgentType() {
        return agentType;
    }

    public void setAgentType(int agentType) {
        this.agentType = agentType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
