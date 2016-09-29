package agroconnectapp.agroconnect.in.agroconnect.networkclient.response;

import agroconnectapp.agroconnect.in.agroconnect.networkclient.model.CityData;

/**
 * Created by shakti on 4/30/2016.
 */
public class CitiesResponse extends TResponse {
    public CitiesResponse(CityData cityData) {
        this.cityData = cityData;
    }

    public CityData getCityData() {
        return cityData;
    }

    CityData cityData;
}
