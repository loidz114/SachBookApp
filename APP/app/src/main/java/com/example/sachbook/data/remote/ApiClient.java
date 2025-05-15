package com.example.sachbook.data.remote;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.sachbook.BuildConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static volatile Retrofit retrofit = null;
    private static volatile BookApiService bookApiService = null;
    private static final String API_URL = "http://192.168.10.46:8080/";

    private static Retrofit getRetrofit(Context context) {
        if (retrofit == null) {
            synchronized (ApiClient.class) {
                if (retrofit == null) {
                    if (context == null) {
                        throw new IllegalArgumentException("Context cannot be null");
                    }

                    // Tạo HttpLoggingInterceptor để log request/response
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    // Tạo OkHttpClient với timeout và interceptor
                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .addInterceptor(loggingInterceptor)
                            .addInterceptor(chain -> {
                                SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                                String token = prefs.getString("jwt_token", null);

                                Request originalRequest = chain.request();
                                Request.Builder requestBuilder = originalRequest.newBuilder();

                                // Chỉ thêm header Authorization cho các endpoint yêu cầu xác thực
                                String url = originalRequest.url().toString();
                                if (token != null && !url.contains("/api/auth/")) {
                                    requestBuilder.addHeader("Authorization", "Bearer " + token);
                                }

                                Request newRequest = requestBuilder.build();
                                return chain.proceed(newRequest);
                            })
                            .build();

                    // Tạo Retrofit instance
                    retrofit = new Retrofit.Builder()
                            .baseUrl(API_URL) // Sử dụng BuildConfig
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static BookApiService getBookApiService(Context context) {
        if (bookApiService == null) {
            synchronized (ApiClient.class) {
                if (bookApiService == null) {
                    bookApiService = getRetrofit(context).create(BookApiService.class);
                }
            }
        }
        return bookApiService;
    }
}