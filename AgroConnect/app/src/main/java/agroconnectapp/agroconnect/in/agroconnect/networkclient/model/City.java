package agroconnectapp.agroconnect.in.agroconnect.networkclient.model;

import com.orm.SugarRecord;

/**
 * Created by shakti on 4/30/2016.
 */
public class City extends SugarRecord {
    public City() {
    }

    public City(String cityId, String name, String localName) {
        CityId = cityId;
        Name = name;
        LocalName = localName;
    }

    public String getCityId() {
        return CityId;
    }

    public String getName() {
        return Name;
    }

    public String getLocalName() {
        return LocalName;
    }

    String CityId;
    String Name;
    String LocalName;
}
