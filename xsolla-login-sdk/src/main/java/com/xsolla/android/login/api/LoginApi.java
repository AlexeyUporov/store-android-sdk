package com.xsolla.android.login.api;

import com.xsolla.android.login.entity.request.User;
import com.xsolla.android.login.entity.request.NewUser;
import com.xsolla.android.login.entity.request.ResetPasswordBody;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.SocialAuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoginApi {

    @POST("/api/user")
    Call<Void> registerUser(@Query("projectId") String projectId, @Body NewUser newUser);

    @POST("/api/login")
    Call<AuthResponse> login(@Query("projectId") String projectId, @Body User user);

    @POST("/api/password/reset/request")
    Call<Void> resetPassword(@Query("projectId") String projectId, @Body ResetPasswordBody resetPasswordBody);

    @GET("/api/social/{providerName}/login_url")
    Call<SocialAuthResponse> getLinkForSocialAuth(@Path("providerName") String providerName, @Query("projectId") String projectId);
}
