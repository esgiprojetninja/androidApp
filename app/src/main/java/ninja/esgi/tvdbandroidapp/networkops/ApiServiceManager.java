package ninja.esgi.tvdbandroidapp.networkops;

import java.util.HashMap;

import ninja.esgi.tvdbandroidapp.model.Login;
import ninja.esgi.tvdbandroidapp.model.Search;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieResponse;
import ninja.esgi.tvdbandroidapp.model.response.GetSeriesEpisodesResponse;
import ninja.esgi.tvdbandroidapp.model.response.LanguagesResponse;
import ninja.esgi.tvdbandroidapp.model.response.LoginResponse;
import ninja.esgi.tvdbandroidapp.model.response.SearchSeriesResponse;
import ninja.esgi.tvdbandroidapp.model.response.UpdatedSeriesResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserFavoritesResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserRatingsResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserResponse;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ApiServiceManager implements INetworkService {

    private static String LOG_TAG = "ApiServiceManager";
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

    @Override
    public void getUser(String token, Subscriber<Response<UserResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);
        addObservable(service.getUser(token), subscriber);
    }

    @Override
    public void getUserFavorites(String token, Subscriber<Response<UserFavoritesResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);
        addObservable(service.getUserFavorites(token), subscriber);
    }

    @Override
    public void putUserFavorite(String token, String favoriteId, Subscriber<Response<UserFavoritesResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);
        addObservable(service.putUserFavorite(token, favoriteId), subscriber);
    }

    @Override
    public void deleteUserFavorite(String token, String favoriteId, Subscriber<Response<UserFavoritesResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);
        addObservable(service.deleteUserFavorite(token, favoriteId), subscriber);
    }

    @Override
    public void getUserRatings(String token, Subscriber<Response<UserRatingsResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);
        addObservable(service.getUserRatings(token), subscriber);
    }


    @Override
    public void putUserRating(String token, String itemType, String itemId, String itemRating, Subscriber<Response<UserRatingsResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);
        addObservable(service.putUserRating(token, itemType, itemId, itemRating), subscriber);
    }

    @Override
    public void deleteUserRating(String token, String itemType, String itemId, Subscriber<Response<UserRatingsResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);
        addObservable(service.deleteUserRating(token, itemType, itemId), subscriber);
    }


    @Override
    public void getLanguages(String token, Subscriber<Response<LanguagesResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);
        addObservable(service.getLanguages(token), subscriber);
    }

    @Override
    public void getSearchSeries(String token, String defaultLanguage, Search searchParams, Subscriber<Response<SearchSeriesResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);

        HashMap<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Authorization", token);
        String language = searchParams.getLanguage() != null && searchParams.getLanguage().length() > 0 ? searchParams.getLanguage() : defaultLanguage;
        headersMap.put("Accept-Language", language);

        addObservable(service.getSearchSeries(headersMap, searchParams.getQueriesMap()), subscriber);
    }

    @Override
    public void getSerie(String token, String language, Long seriesId, Subscriber<Response<GetSerieResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);

        HashMap<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Authorization", token);
        headersMap.put("Accept-Language", language);

        addObservable(service.getSerie(headersMap, seriesId.toString()), subscriber);
    }

    @Override
    public void getSeriesEpisodes(String token, String language, Long seriesId, Subscriber<Response<GetSeriesEpisodesResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);

        HashMap<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Authorization", token);
        headersMap.put("Accept-Language", language);

        addObservable(service.getSeriesEpisodes(headersMap, seriesId.toString()), subscriber);
    }

    @Override
    public void getUpdatedSeries(String token, String language, String fromTime, String toTime, Subscriber<Response<UpdatedSeriesResponse>> subscriber) {
        IRFApiService service = mRetrofit.create(IRFApiService.class);

        HashMap<String, String> headersMap = new HashMap<String, String>();
        headersMap.put("Authorization", token);
        headersMap.put("Accept-Language", language);

        addObservable(service.getUpdatedSeries(headersMap, fromTime, toTime), subscriber);
    }
}
