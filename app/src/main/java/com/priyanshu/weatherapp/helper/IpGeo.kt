package com.priyanshu.weatherapp.helper

import retrofit2.http.GET
import retrofit2.Call
interface IpGeo {
    @GET("json")
    fun getLocation(): Call<IpGeoData>
}