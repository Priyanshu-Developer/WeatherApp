package com.priyanshu.weatherapp.helper

import android.content.Context
import android.util.Log
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class IpGeolocationHelper (private var context: Context){
    lateinit var retrofit: IpGeo
     init{
        retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://ipinfo.io/")
            .build().create(IpGeo::class.java)
    }
    public fun getCity(callback: (String?) -> Unit){
        retrofit.getLocation().enqueue(object : Callback<IpGeoData> {
            override fun onResponse(p0: Call<IpGeoData?>,p1: Response<IpGeoData?>) {
                if (p1.isSuccessful){
                    Log.d("IpGeo", "IP Location:  city=${p1.body()?.city}")
                    callback(p1.body()?.city)
                }
            }
            override fun onFailure(
                p0: Call<IpGeoData?>,
                p1: Throwable
            ) {
                Toast.makeText(context, "No Internet Available", Toast.LENGTH_SHORT).show()

                callback(null)
            }


        })
    }
}