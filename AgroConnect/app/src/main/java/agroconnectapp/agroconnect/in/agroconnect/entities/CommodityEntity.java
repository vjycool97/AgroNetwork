package agroconnectapp.agroconnect.in.agroconnect.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sumanta on 5/6/16.
 */
public class CommodityEntity {
    @SerializedName("CommodityId")
    private int id;
    @SerializedName("Name")
    private String name;
    @SerializedName("LocalName")
    private String localName;

    public CommodityEntity() {}
    public CommodityEntity(int id, String name, String localName) {
        this.id = id;
        this.name = name;
        this.localName = localName;
    }

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

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @Override
    public String toString() {
        return localName;
    }
}
