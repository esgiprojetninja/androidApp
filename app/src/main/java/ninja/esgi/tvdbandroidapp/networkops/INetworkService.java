package ninja.esgi.tvdbandroidapp.networkops;

import ninja.esgi.tvdbandroidapp.model.Login;
import ninja.esgi.tvdbandroidapp.model.response.LoginResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserFavoritesResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserResponse;
import retrofit2.Response;
import rx.Subscriber;

/**
 * Created by dylanfoster on 13/01/18.
 */

public interface INetworkService {

    // ########## TOKEN ROUTES ###################################################
    void login(Login login, Subscriber<Response<LoginResponse>> subscriber);

    void refreshToken(String current_token, Subscriber<Response<LoginResponse>> subscriber);
    // ###########################################################################


    // ########## USER ROUTES ####################################################
    void getUser(String token, Subscriber<Response<UserResponse>> subscriber);
    void getUserFavorites(String token, Subscriber<Response<UserFavoritesResponse>> subscriber);
}
