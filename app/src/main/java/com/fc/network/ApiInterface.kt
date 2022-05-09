package com.vision2020.network


import com.fc.homescreen.manager.home.model.ValidateSubscriptionModel
import com.fc.homescreen.manager.detail.model.SelectWallaperModel
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*


interface ApiInterface {

    /*  @POST("getWallpapersAtHome") //old
    fun homeApi(@Body file: JsonObject): Call<JsonObject>*/

    /* @POST("selectedWallpaper") // Old api
     fun select(@Body file: JsonObject): Call<SelectWallaperModel>*/

    /*@POST("addUpdSubscription")   // Old api
    fun addUpdSubscription(@Body file: JsonObject): Call<JsonObject>*/

    @Headers("Accept: application/json")
    @POST("https://accounts.google.com/o/oauth2/token")
    fun getToken(@Body file: JsonObject): Call<JsonObject>

/*    @POST("getSubscriptionInfo")   // old api
    fun getSubscriptionInfo(@Body file: JsonObject): Call<ValidateSubscriptionModel>*/

    @POST("addUpdSubscriptionForAndroidUsers")
    fun addUpdSubscriptionForAndroidUsers(@Body file: JsonObject): Call<JsonObject>

    @POST("getSubscriptionInfoForAndroidUsers")
    fun getSubscriptionInfoForAndroidUsers(@Body file: JsonObject): Call<ValidateSubscriptionModel>

    @POST("selectedWallpaperForAndroidUsers")
    fun selectedWallpaperForAndroidUsers(@Body file: JsonObject): Call<SelectWallaperModel>

    @POST("getWallpapersAtHomeForAndroidUsers")
    fun homeApi(@Body file: JsonObject): Call<JsonObject>


    @POST("androidBackendServicesForSubscriptionInfo")
    fun backgroundApi(@Body file: JsonObject): Call<JsonObject>


    @POST("https://accounts.google.com/o/oauth2/token")
    fun tokenGet(@Body file: JsonObject): Call<JsonObject>
}



