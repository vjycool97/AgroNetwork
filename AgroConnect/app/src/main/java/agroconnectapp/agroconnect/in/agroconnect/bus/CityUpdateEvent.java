package agroconnectapp.agroconnect.in.agroconnect.bus;

/**
 * Created by shakti on 7/21/2015.
 */
public class CityUpdateEvent {
    public CityUpdateEvent(boolean success) {
        this.success = success;
    }

    public boolean success;

}
