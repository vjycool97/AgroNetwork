package agroconnectapp.agroconnect.in.agroconnect.entities;

import java.util.ArrayList;

/**
 * Created by sumanta on 31/7/16.
 */
public class CommentEntity {
    private String comment;
    private String agentName;
    private String lastUpdate;
    private int agentType;
    private String specialization;
    private String city;
    private String discussion;
    private ArrayList<String> ImageUrls;

    public ArrayList<String> getImageUrls() {
        return ImageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        ImageUrls = imageUrls;
    }

    public CommentEntity() {}

    public int getAgentType() {
        return agentType;
    }

    public void setAgentType(int agentType) {
        this.agentType = agentType;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDiscussion() {
        return discussion;
    }

    public void setDiscussion(String discussion) {
        this.discussion = discussion;
    }
}
