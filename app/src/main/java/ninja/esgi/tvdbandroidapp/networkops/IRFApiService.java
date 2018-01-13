package ninja.esgi.tvdbandroidapp.networkops;

import ninja.esgi.tvdbandroidapp.model.Login;
import ninja.esgi.tvdbandroidapp.model.response.LoginResponse;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by dylanfoster on 13/01/18.
 */

public interface IRFApiService {

    // ### TOKEN Routes #################
    @POST("/login")
    Observable<Response<LoginResponse>> login(@Body Login credentials);

    @GET("/refresh_token")
    Observable<Response<LoginResponse>> refreshToken(@Header("Authorization") String token);
    // ##################################


}
