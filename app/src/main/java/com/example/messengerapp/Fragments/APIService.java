package com.example.messengerapp.Fragments;

import com.example.messengerapp.Notifications.MyResponse;
import com.example.messengerapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAIwdKjMM:APA91bFKdnUzW58IMn_fWqzh12-dLP6vvD-ssO_tb9jyXKusvm3T8uLUF4jHL9waSHwGo4IhZJb1I6uDBQMLmNj_o1SFW6I_4TyCsM1FLf6i4EG9-SOCaU1wKF6Vc-yfT_i8geldbkE"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
