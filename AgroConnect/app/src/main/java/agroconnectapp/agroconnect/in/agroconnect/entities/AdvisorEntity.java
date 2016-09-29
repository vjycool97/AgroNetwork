package agroconnectapp.agroconnect.in.agroconnect.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sumanta on 9/7/16.
 */
public class AdvisorEntity {
    @SerializedName("Id")
    private int id;
    @SerializedName("AgentId")
    private int agentId;
    @SerializedName("Title")
    private String title;
    @SerializedName("AgentName")
    private String agentName;
    @SerializedName("AgentCity")
    private String agentCity;
    @SerializedName("Description")
    private String description;
    @SerializedName("AgentCommodity")
     private String agentCommodity;
    @SerializedName("LastUpdated")
    private String lastUpdate;
    @SerializedName("AgentPhoneNumber")
    private String agentPhoneNumber;
    @SerializedName("Replies")
    private int replies;

    public AdvisorEntity() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentCity() {
        return agentCity;
    }

    public void setAgentCity(String agentCity) {
        this.agentCity = agentCity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAgentCommodity() {
        return agentCommodity;
    }

    public void setAgentCommodity(String agentCommodity) {
        this.agentCommodity = agentCommodity;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getAgentPhoneNumber() {
        return agentPhoneNumber;
    }

    public void setAgentPhoneNumber(String agentPhoneNumber) {
        this.agentPhoneNumber = agentPhoneNumber;
    }

    public int getReplies() {
        return replies;
    }

    public void setReplies(int replies) {
        this.replies = replies;
    }
}
