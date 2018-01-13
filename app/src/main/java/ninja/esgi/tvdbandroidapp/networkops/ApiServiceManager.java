package ninja.esgi.tvdbandroidapp.networkops;

import ninja.esgi.tvdbandroidapp.model.Login;
import ninja.esgi.tvdbandroidapp.model.response.LoginResponse;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by dylanfoster on 13/01/18.
 */
public class ApiServiceManager implements INetworkService {

    private static String TAG = "ApiServiceManager";
    private static final String URL_ENDPOINT = "https://api.thetvdb.com/";

    private Retrofit mRetrofit;

    public ApiServiceManager() {
        if (mRetrofit == null) {
            mRetrofit = getDefault();
        }
    }

    /**
     * Default config retrofit
     *
     * @return retrofit instance
     */
    private Retrofit getDefault() {
        OkHttpClient okHttpClient = MainHttpClient.getInstance().getClient();
        String urlBackend = URL_ENDPOINT;
        return new Retrofit.Builder()
                .baseUrl(urlBackend)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    private static <IME> void addObservable(Observable<IME> observable, Subscriber<IME> subscriber) {
        NetTools.getInstance().addSubscription(
                observable.subscribeOn(Schedulers.io())
                        // Life saving thread management, thank you very much. BB Javandroid misery
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(subscriber)
        );
    }

    @Override
    public void login(Login login, Subscriber<Response<LoginResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);
        addObservable(service.login(login), subscriber);
    }

    @Override
    public void refreshToken(String current_token, Subscriber<Response<LoginResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);
        addObservable(service.refreshToken(current_token), subscriber);
    }
}
