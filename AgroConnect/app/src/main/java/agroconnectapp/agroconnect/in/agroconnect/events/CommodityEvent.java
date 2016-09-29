package agroconnectapp.agroconnect.in.agroconnect.events;

import agroconnectapp.agroconnect.in.agroconnect.entities.CommodityEntity;

/**
 * Created by sumanta on 5/6/16.
 */
public class CommodityEvent {
    private CommodityEntity entity;
    public CommodityEvent(CommodityEntity entity) {
        this.entity = entity;
    }
    public CommodityEntity getEntity() {
        return entity;
    }
}
