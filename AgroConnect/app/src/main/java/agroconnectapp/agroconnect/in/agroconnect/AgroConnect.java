package agroconnectapp.agroconnect.in.agroconnect;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.stetho.Stetho;
import com.orm.SugarApp;
import com.squareup.otto.ThreadEnforcer;

import agroconnectapp.agroconnect.in.agroconnect.events.AgroEventBus;
import io.fabric.sdk.android.Fabric;

public class AgroConnect extends SugarApp {

    private static Context context;
    private RequestQueue requestQueue;
    private static final AgroConnect AGRO_CONNECT = new AgroConnect();
    public static AgroEventBus agroEventBus;
    public AgroConnect() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        //initialize event bus
        agroEventBus = new AgroEventBus(ThreadEnforcer.MAIN);
        Fabric.with(this, new Crashlytics());
        Fabric.with(this, new Answers());

        setUpStetho();
    }

    private void setUpStetho() {
        Stetho.InitializerBuilder initializerBuilder = Stetho.newInitializerBuilder(this);
        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));
        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));
        Stetho.Initializer initializer = initializerBuilder.build();
        Stetho.initialize(initializer);
    }

    public static AgroConnect getInstance() {
        return AGRO_CONNECT;
    }

    public Context getContext() {
        return context;
    }
    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public RequestQueue getRequestQueue() {
        synchronized (this) {
            if (requestQueue == null) {
                requestQueue = Volley.newRequestQueue(context);
            }
        }
        return requestQueue;
    }


}
