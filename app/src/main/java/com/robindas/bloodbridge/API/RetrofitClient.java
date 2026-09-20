package com.robindas.bloodbridge.API;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL =
            "http://10.0.2.2:8080/";

    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(Context context) {

        HttpLoggingInterceptor loggingInterceptor =
                new HttpLoggingInterceptor();

        loggingInterceptor.setLevel(
                HttpLoggingInterceptor.Level.HEADERS
        );

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))
                .addInterceptor(loggingInterceptor)
                .build();

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
