package com.example.nicetry;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

interface RetrofitAPI {
    @POST("service/register")
    Call<ResponseBody> register(@Body User user);

    @POST("service/authenticate")
    Call<ResponseBody> authenticate(@Body User user);

    @POST("service/upload/${username}")
    Call<ResponseBody> upload(
            @Path("username") String username,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file);
}
