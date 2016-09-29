package agroconnectapp.agroconnect.in.agroconnect.networkclient.model;

import com.orm.SugarRecord;

/**
 * Created by shakti on 4/30/2016.
 */
public class Commodity extends SugarRecord {
    public Commodity() {
    }

    public Commodity(String commodityId, String name, String localName) {
        CommodityId = commodityId;
        Name = name;
        LocalName = localName;
    }

    public String getCommodityId() {
        return CommodityId;
    }

    public String getName() {
        return Name;
    }

    public String getLocalName() {
        return LocalName;
    }

    String CommodityId;
    String Name;
    String LocalName;
}
