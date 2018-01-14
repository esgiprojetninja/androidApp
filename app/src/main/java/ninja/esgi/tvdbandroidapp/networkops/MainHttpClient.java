package ninja.esgi.tvdbandroidapp.networkops;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by dylanfoster on 13/01/18.
 */

public class MainHttpClient {
    private static final long DEFAULT_TIMEOUT_SECONDS = 90;
    private static MainHttpClient sInstance;
    private OkHttpClient okHttpClient;


    public synchronized static MainHttpClient getInstance() {
        if (sInstance == null) {
            sInstance = new MainHttpClient();
        }
        return sInstance;
    }

    private MainHttpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(logging)
                .build();
    }


    /**
     * Intercept & handle timeout request
     * Intercept or add cookies
     *
     * @return okHttpClient
     */
    public OkHttpClient getClient() {
        return okHttpClient;
    }

}
