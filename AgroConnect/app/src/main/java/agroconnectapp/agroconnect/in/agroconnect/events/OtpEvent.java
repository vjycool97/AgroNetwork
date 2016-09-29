package agroconnectapp.agroconnect.in.agroconnect.events;

/**
 * Created by sumanta on 22/3/16.
 */
public class OtpEvent {
    private String timerAction;
    public OtpEvent(String timerAction) {
        this.timerAction = timerAction;
    }
    public String getStartTimer() {
        return timerAction;
    }
}
