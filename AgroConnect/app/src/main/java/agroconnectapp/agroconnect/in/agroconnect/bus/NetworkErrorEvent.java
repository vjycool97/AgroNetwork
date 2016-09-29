package agroconnectapp.agroconnect.in.agroconnect.bus;

/**
 * Created by shakti on 6/14/2015.
 */
public class NetworkErrorEvent {
    String error_message;

    public NetworkErrorEvent(String error_message) {
        this.error_message = error_message;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }


}
