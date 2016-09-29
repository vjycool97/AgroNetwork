package agroconnectapp.agroconnect.in.agroconnect.model;

/**
 * Created by nitin.gupta on 12/12/2015.
 */
public class ProfileData {
    int id;
    String name;
    int agentType;
    String mandi;
    int mandiId;
    String ciy;
    int cityId;
    String phoneNumber;
    String alternateNumber;
    String description;
    boolean isVerified;
    int likes;
    int dislikes;
    int rating;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAgentType() {
        return agentType;
    }

    public void setAgentType(int agentType) {
        this.agentType = agentType;
    }

    public String getMandi() {
        return mandi;
    }

    public void setMandi(String mandi) {
        this.mandi = mandi;
    }

    public int getMandiId() {
        return mandiId;
    }

    public void setMandiId(int mandiId) {
        this.mandiId = mandiId;
    }

    public String getCiy() {
        return ciy;
    }

    public void setCiy(String ciy) {
        this.ciy = ciy;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAlternateNumber() {
        return alternateNumber;
    }

    public void setAlternateNumber(String alternateNumber) {
        this.alternateNumber = alternateNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setIsVerified(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
