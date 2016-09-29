package agroconnectapp.agroconnect.in.agroconnect.model;

import org.json.JSONArray;

/**
 * Created by niteshtarani on 18/04/16.
 */
public class AdvisoryData {

    private int id;
    private int agentId;
    private String description;
    private String agentName;
    private int agentType;
    private String agentCity;
    private String agentPhone;
    private String LastUpdated;
    private String ParentId;
    private String repliesCount;
    private String agentCommodity;
    private String agentOrganisation;
    private String agentDepartment;
    private String problemAsDiagnosedByAdvisor;
    private String productToBeApplied;
    private String dosage;
    private String applicationTime;
    private String additionalAdvice;
    private JSONArray resources;
    private int type;

    public AdvisoryData() {
    }

    public int getAgentType() {
        return agentType;
    }

    public void setAgentType(int agentType) {
        this.agentType = agentType;
    }

    public String getAgentCity() {
        return agentCity;
    }

    public void setAgentCity(String agentCity) {
        this.agentCity = agentCity;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastUpdated() {
        return LastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        LastUpdated = lastUpdated;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setParentId(String parentId) {
        ParentId = parentId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone;
    }

    public String getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(String repliesCount) {
        this.repliesCount = repliesCount;
    }

    public String getAgentCommodity() {
        return agentCommodity;
    }

    public void setAgentCommodity(String agentCommodity) {
        this.agentCommodity = agentCommodity;
    }

    public String getAdditionalAdvice() {
        return additionalAdvice;
    }

    public void setAdditionalAdvice(String additionalAdvice) {
        this.additionalAdvice = additionalAdvice;
    }

    public String getAgentDepartment() {
        return agentDepartment;
    }

    public void setAgentDepartment(String agentDepartment) {
        this.agentDepartment = agentDepartment;
    }

    public String getAgentOrganisation() {
        return agentOrganisation;
    }

    public void setAgentOrganisation(String agentOrganisation) {
        this.agentOrganisation = agentOrganisation;
    }

    public String getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(String applicationTime) {
        this.applicationTime = applicationTime;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getProblemAsDiagnosedByAdvisor() {
        return problemAsDiagnosedByAdvisor;
    }

    public void setProblemAsDiagnosedByAdvisor(String problemAsDiagnosedByAdvisor) {
        this.problemAsDiagnosedByAdvisor = problemAsDiagnosedByAdvisor;
    }

    public String getProductToBeApplied() {
        return productToBeApplied;
    }

    public void setProductToBeApplied(String productToBeApplied) {
        this.productToBeApplied = productToBeApplied;
    }

    public JSONArray getResources() {
        return resources;
    }

    public void setResources(JSONArray resources) {
        this.resources = resources;
    }
}
