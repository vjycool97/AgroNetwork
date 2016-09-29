package agroconnectapp.agroconnect.in.agroconnect.networkclient.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shakti on 4/30/2016.
 */
public class CommodityData {
    public CommodityData(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    int version;
    public List<Commodity> commodities = new ArrayList<>();
}
