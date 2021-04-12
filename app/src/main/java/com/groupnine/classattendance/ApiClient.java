package com.groupnine.classattendance;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2/class_monitor/";
//    private static final String BASE_URL = "http://192.168.43.231/class_monitor/";
//    private static final String BASE_URL = "https://mnyumba.cmtai.co.ke/class_monitor/";
    private static Retrofit retrofit;

    public static Retrofit getApiClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

}
