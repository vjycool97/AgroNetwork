package agroconnectapp.agroconnect.in.agroconnect.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nitin.gupta on 12/6/2015.
 */
public class FeedData implements Parcelable{
    private int listingId;
    private int agentId;
    private String agentName;
    private String agentType;
    private String lastUpdated;
//    private String mandi;
//    private int mandiId;
//    private String city;
//    private int cityId;
    private String quantity;
    private String price;
    private boolean isSell;
    private int commodityId;
    private String commodityName;
    private String variety;
    private String description;
    private String location;
    private int type;

    public FeedData() {

    }

    public FeedData(Parcel in){
        this.listingId = in.readInt();
        this.agentId = in.readInt();
        this.agentName = in.readString();
        this.agentType = in.readString();
        this.lastUpdated = in.readString();
//        this.mandi = in.readString();
//        this.city = in.readString();
//        this.mandiId = in.readInt();
//        this.cityId = in.readInt();
        this.quantity = in.readString();
        this.price = in.readString();
        this.isSell = Boolean.valueOf(in.readString());
        this.commodityId = in.readInt();
        this.commodityName = in.readString();
        this.variety = in.readString();
        this.description = in.readString();
        this.location = in.readString();
        this.type = in.readInt();
    }

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
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

    public String getAgentType() {
        return agentType;
    }

    public void setAgentType(String agentType) {
        this.agentType = agentType;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

//    public String getMandi() {
//        return mandi;
//    }
//
//    public void setMandi(String mandi) {
//        this.mandi = mandi;
//    }
//
//    public int getMandiId() {
//        return mandiId;
//    }
//
//    public void setMandiId(int mandiId) {
//        this.mandiId = mandiId;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public int getParentCropId() {
//        return cityId;
//    }
//
//    public void setCityId(int cityId) {
//        this.cityId = cityId;
//    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isSell() {
        return isSell;
    }

    public void setIsSell(boolean isSell) {
        this.isSell = isSell;
    }

    public int getCommodityId() {
        return commodityId;
    }

    public void setCommodityId(int commodityId) {
        this.commodityId = commodityId;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCommodityName() {
        return commodityName;
    }

    public void setCommodityName(String commodityName) {
        this.commodityName = commodityName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.listingId);
        dest.writeInt(this.agentId);
        dest.writeString(this.agentName);
        dest.writeString(this.agentType);
        dest.writeString(this.lastUpdated);
//        dest.writeString(this.mandi);
//        dest.writeString(this.city);
//        dest.writeInt(this.mandiId);
//        dest.writeInt(this.cityId);
        dest.writeString(this.quantity);
        dest.writeString(this.price);
        dest.writeString(String.valueOf(this.isSell));
        dest.writeInt(this.commodityId);
        dest.writeString(this.commodityName);
        dest.writeString(this.variety);
        dest.writeString(this.description);
        dest.writeString(this.location);
        dest.writeInt(this.type);
    }

    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public FeedData createFromParcel(Parcel source) {
            return new FeedData(source);
        }

        @Override
        public FeedData[] newArray(int size) {
            return new FeedData[size];
        }
    };
}
