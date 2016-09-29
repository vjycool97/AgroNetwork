package agroconnectapp.agroconnect.in.agroconnect.utility;

/**
 * Created by nitin.gupta on 12/12/2015.
 */
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

public class HttpRequest {

    OkHttpClient client;
    String headerKey;
    String headerValue;
    Boolean headerRequired;
    public final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public HttpRequest(Boolean headerRequired,String key, String value) {
        client = new OkHttpClient();
        client.setConnectTimeout(15, TimeUnit.SECONDS);
        client.setReadTimeout(15, TimeUnit.SECONDS);
        this.headerRequired =  headerRequired;
        this.headerKey = key;
        this.headerValue = value;
    }

    // code request code here
    public Call doGetRequest(String url, Callback cb) throws IOException {

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if(headerRequired)
            requestBuilder.addHeader(headerKey, headerValue);
        Request request = requestBuilder.build();

        Call call = client.newCall(request);
        call.enqueue(cb);
        return call;
    }

    public Call doPostRequest(String url, RequestBody body, Callback cb) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        requestBuilder.post(body);
        if(headerRequired)
            requestBuilder.addHeader(headerKey, headerValue);
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(cb);
        return call;
    }

    public Call doPutRequest(String url, RequestBody body, Callback cb) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        requestBuilder.put(body);
        if(headerRequired)
            requestBuilder.addHeader(headerKey, headerValue);
        Request request = requestBuilder.build();
        Call call = client.newCall(request);
        call.enqueue(cb);
        return call;
    }
}
