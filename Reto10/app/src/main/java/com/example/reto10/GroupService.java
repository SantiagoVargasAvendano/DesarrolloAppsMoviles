package com.example.reto10;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GroupService {
    @GET("/resource/hrhc-c4wu.json")
    Call<List<Group>> getGroups();

}