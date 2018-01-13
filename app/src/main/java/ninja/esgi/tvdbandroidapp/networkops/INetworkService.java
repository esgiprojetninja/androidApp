package ninja.esgi.tvdbandroidapp.networkops;

import ninja.esgi.tvdbandroidapp.model.Login;
import ninja.esgi.tvdbandroidapp.model.response.LoginResponse;
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

}
