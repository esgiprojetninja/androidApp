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
import retrofit2.Response;
import rx.Subscriber;

public interface INetworkService {

    // ########## TOKEN ROUTES ###################################################
    void login(Login login, Subscriber<Response<LoginResponse>> subscriber);

    void refreshToken(String current_token, Subscriber<Response<LoginResponse>> subscriber);
    // ###########################################################################


    // ########## USER ROUTES ####################################################
    void getUser(String token, Subscriber<Response<UserResponse>> subscriber);
    void getUserFavorites(String token, Subscriber<Response<UserFavoritesResponse>> subscriber);
    void getUserRatings(String token, Subscriber<Response<UserRatingsResponse>> subscriber);
    void putUserRating(String token, String itemType, String itemId, String itemRating, Subscriber<Response<UserRatingsResponse>> subscriber);
    void deleteUserRating(String token, String itemType, String itemId, Subscriber<Response<UserRatingsResponse>> subscriber);
    void putUserFavorite(String token, String favoriteId, Subscriber<Response<UserFavoritesResponse>> subscriber);
    void deleteUserFavorite(String token, String favoriteId, Subscriber<Response<UserFavoritesResponse>> subscriber);


    // ########## LANGUAGES ROUTES ###############################################
    void getLanguages(String token, Subscriber<Response<LanguagesResponse>> subscriber);


    // ########## SEARCH SERIES ROUTES ###########################################
    void getSearchSeries(String token, String defaultLanguage, Search searchParams, Subscriber<Response<SearchSeriesResponse>> subscriber);


    // ########## SERIES ROUTES ##################################################
    void getSerie(String token, String language, Long id, Subscriber<Response<GetSerieResponse>> subscriber);
    void getSeriesEpisodes(String token, String language, Long id, Subscriber<Response<GetSeriesEpisodesResponse>> subscriber);


    // ########## UPDATED SERIES ROUTES ##########################################
    void getUpdatedSeries(String token, String language, String fromTime, String toTime, Subscriber<Response<UpdatedSeriesResponse>> subscriber);

}
