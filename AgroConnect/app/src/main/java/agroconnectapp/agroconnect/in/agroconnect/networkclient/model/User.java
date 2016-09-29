package agroconnectapp.agroconnect.in.agroconnect.networkclient.model;


import java.util.HashMap;
import java.util.Map;

public class User {

    private Integer Id;
    private String Name;
    private Integer AgentType;
    private String Mandi;
    private Object MandiId;
    private String City;
    private Integer CityId;
    private String PhoneNumber;
    private Object AlternateNumber;
    private String Description;
    private Boolean IsVerified;
    private Integer Likes;
    private Integer Dislikes;
    private Integer Rating;
    private Object Password;
    private Object LanguageId;
    private Object CommoditiesCSV;
    private Integer CommodityId;
    private String Commodity;
    private Object Organisation;
    private Object Department;
    private Object Email;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The Id
     */
    public Integer getId() {
        return Id;
    }

    /**
     * @param Id The Id
     */
    public void setId(Integer Id) {
        this.Id = Id;
    }

    /**
     * @return The Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @param Name The Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * @return The AgentType
     */
    public Integer getAgentType() {
        return AgentType;
    }

    /**
     * @param AgentType The AgentType
     */
    public void setAgentType(Integer AgentType) {
        this.AgentType = AgentType;
    }

    /**
     * @return The Mandi
     */
    public String getMandi() {
        return Mandi;
    }

    /**
     * @param Mandi The Mandi
     */
    public void setMandi(String Mandi) {
        this.Mandi = Mandi;
    }

    /**
     * @return The MandiId
     */
    public Object getMandiId() {
        return MandiId;
    }

    /**
     * @param MandiId The MandiId
     */
    public void setMandiId(Object MandiId) {
        this.MandiId = MandiId;
    }

    /**
     * @return The City
     */
    public String getCity() {
        return City;
    }

    /**
     * @param City The City
     */
    public void setCity(String City) {
        this.City = City;
    }

    /**
     * @return The parentCropId
     */
    public Integer getCityId() {
        return CityId;
    }

    /**
     * @param CityId The parentCropId
     */
    public void setCityId(Integer CityId) {
        this.CityId = CityId;
    }

    /**
     * @return The PhoneNumber
     */
    public String getPhoneNumber() {
        return PhoneNumber;
    }

    /**
     * @param PhoneNumber The PhoneNumber
     */
    public void setPhoneNumber(String PhoneNumber) {
        this.PhoneNumber = PhoneNumber;
    }

    /**
     * @return The AlternateNumber
     */
    public Object getAlternateNumber() {
        return AlternateNumber;
    }

    /**
     * @param AlternateNumber The AlternateNumber
     */
    public void setAlternateNumber(Object AlternateNumber) {
        this.AlternateNumber = AlternateNumber;
    }

    /**
     * @return The Description
     */
    public String getDescription() {
        return Description;
    }

    /**
     * @param Description The Description
     */
    public void setDescription(String Description) {
        this.Description = Description;
    }

    /**
     * @return The IsVerified
     */
    public Boolean getIsVerified() {
        return IsVerified;
    }

    /**
     * @param IsVerified The IsVerified
     */
    public void setIsVerified(Boolean IsVerified) {
        this.IsVerified = IsVerified;
    }

    /**
     * @return The Likes
     */
    public Integer getLikes() {
        return Likes;
    }

    /**
     * @param Likes The Likes
     */
    public void setLikes(Integer Likes) {
        this.Likes = Likes;
    }

    /**
     * @return The Dislikes
     */
    public Integer getDislikes() {
        return Dislikes;
    }

    /**
     * @param Dislikes The Dislikes
     */
    public void setDislikes(Integer Dislikes) {
        this.Dislikes = Dislikes;
    }

    /**
     * @return The Rating
     */
    public Integer getRating() {
        return Rating;
    }

    /**
     * @param Rating The Rating
     */
    public void setRating(Integer Rating) {
        this.Rating = Rating;
    }

    /**
     * @return The Password
     */
    public Object getPassword() {
        return Password;
    }

    /**
     * @param Password The Password
     */
    public void setPassword(Object Password) {
        this.Password = Password;
    }

    /**
     * @return The LanguageId
     */
    public Object getLanguageId() {
        return LanguageId;
    }

    /**
     * @param LanguageId The LanguageId
     */
    public void setLanguageId(Object LanguageId) {
        this.LanguageId = LanguageId;
    }

    /**
     * @return The CommoditiesCSV
     */
    public Object getCommoditiesCSV() {
        return CommoditiesCSV;
    }

    /**
     * @param CommoditiesCSV The CommoditiesCSV
     */
    public void setCommoditiesCSV(Object CommoditiesCSV) {
        this.CommoditiesCSV = CommoditiesCSV;
    }

    /**
     * @return The CommodityId
     */
    public Integer getCommodityId() {
        return CommodityId;
    }

    /**
     * @param CommodityId The CommodityId
     */
    public void setCommodityId(Integer CommodityId) {
        this.CommodityId = CommodityId;
    }

    /**
     * @return The Commodity
     */
    public String getCommodity() {
        return Commodity;
    }

    /**
     * @param Commodity The Commodity
     */
    public void setCommodity(String Commodity) {
        this.Commodity = Commodity;
    }

    /**
     * @return The Organisation
     */
    public Object getOrganisation() {
        return Organisation;
    }

    /**
     * @param Organisation The Organisation
     */
    public void setOrganisation(Object Organisation) {
        this.Organisation = Organisation;
    }

    /**
     * @return The Department
     */
    public Object getDepartment() {
        return Department;
    }

    /**
     * @param Department The Department
     */
    public void setDepartment(Object Department) {
        this.Department = Department;
    }

    /**
     * @return The Email
     */
    public Object getEmail() {
        return Email;
    }

    /**
     * @param Email The Email
     */
    public void setEmail(Object Email) {
        this.Email = Email;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}