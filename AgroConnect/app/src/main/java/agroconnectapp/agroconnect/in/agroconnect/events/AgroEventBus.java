package agroconnectapp.agroconnect.in.agroconnect.events;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class AgroEventBus extends Bus {
    public AgroEventBus(ThreadEnforcer enforcer) {
        super(enforcer);
    }
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    AgroEventBus.super.post(event);
                }
            });
        }
    }
}
