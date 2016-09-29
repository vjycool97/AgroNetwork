package agroconnectapp.agroconnect.in.agroconnect.entities;

import com.google.gson.annotations.SerializedName;

public class CityEntity {
    @SerializedName("CityId")
    private int id;
    @SerializedName("Name")
    private String name;
    @SerializedName("LocalName")
    private String localName;

    public CityEntity() {}
    public CityEntity(int id, String name, String localName) {
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

}
