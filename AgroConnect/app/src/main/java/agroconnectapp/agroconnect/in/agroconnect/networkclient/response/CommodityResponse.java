package agroconnectapp.agroconnect.in.agroconnect.networkclient.response;

import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CommodityData;

/**
 * Created by shakti on 4/30/2016.
 */
public class CommodityResponse extends TResponse {
    public CommodityResponse(CommodityData commodityData) {
        this.commodityData =  commodityData;
    }

    public CommodityData getCommodityData() {
        return commodityData;
    }

    CommodityData commodityData;
}
