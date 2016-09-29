package agroconnectapp.agroconnect.in.agroconnect.events;

import agroconnectapp.agroconnect.in.agroconnect.entities.CityEntity;

/**
 * Created by sumanta on 5/6/16.
 */
public class CityEvent {
    private CityEntity entity;
    public CityEvent(CityEntity entity) {
        this.entity = entity;
    }
    public CityEntity getEntity() {
        return entity;
    }
}
