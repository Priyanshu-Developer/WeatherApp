package com.priyanshu.weatherapp

import com.priyanshu.weatherapp.weatherdata.weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

internal interface AppInterface {
        @GET("weather")
        fun getWeatherData(
            @Query("q") city: String,
            @Query("appid") appId: String,
            @Query("units") units: String
        ): Call<weather>

}