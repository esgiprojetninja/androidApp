package ninja.esgi.tvdbandroidapp.networkops;

import java.util.HashMap;

import ninja.esgi.tvdbandroidapp.model.Login;
import ninja.esgi.tvdbandroidapp.model.response.LanguagesResponse;
import ninja.esgi.tvdbandroidapp.model.response.LoginResponse;
import ninja.esgi.tvdbandroidapp.model.response.SearchSeriesResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserFavoritesResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserRatingsResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserResponse;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface IRFApiService {

    // ### TOKEN Routes #################
    @POST("/login")
    Observable<Response<LoginResponse>> login(@Body Login credentials);

    @GET("/refresh_token")
    Observable<Response<LoginResponse>> refreshToken(@Header("Authorization") String token);
    // ##################################


    // ### USER Routes ##################
    @GET("/user")
    Observable<Response<UserResponse>> getUser(@Header("Authorization") String token);
    @GET("/user/favorites")
    Observable<Response<UserFavoritesResponse>> getUserFavorites(@Header("Authorization") String token);
    @GET("/user/ratings")
    Observable<Response<UserRatingsResponse>> getUserRatings(@Header("Authorization") String token);


    // ### LANGUAGES Routes #############
    @GET("/languages")
    Observable<Response<LanguagesResponse>> getLanguages(@Header("Authorization") String token);


    // ### SEARCH SERIES Routes #########
    @GET("search/series")
    Observable<Response<SearchSeriesResponse>> getSearchSeries(@HeaderMap HashMap<String, String> headers,
                                                               @QueryMap HashMap<String, String> searchParams);
}
