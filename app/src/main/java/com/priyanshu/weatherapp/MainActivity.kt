package com.priyanshu.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.priyanshu.weatherapp.databinding.ActivityMainBinding
import com.priyanshu.weatherapp.helper.IpGeolocationHelper
import com.priyanshu.weatherapp.weatherdata.weather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private val bindings :ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(bindings.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ipGeolocationHelper = IpGeolocationHelper(this)
        ipGeolocationHelper.getCity({city -> fetchWeatherData(city.toString())})
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }


        SearchCity()
    }




    private fun SearchCity() {
        bindings.searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(city:String){
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(AppInterface::class.java)
        val response = retrofit.getWeatherData(city,"529082df2f0c69f80bc943b69113f11c","metric")
        response.enqueue(object :Callback<weather>{
            override fun onResponse(p0: Call<weather>, p1: Response<weather>) {
                val res  = p1.body();
                val condition = res?.weather?.firstOrNull()?.main?: "Unknown"
                if (p1.isSuccessful && res != null){
                    bindings.temprature.text = "${res.main.temp.toString()}°C";
                    bindings.humidity.text ="${res.main.humidity.toString()}"
                    bindings.weather.text = "${condition}"
                    bindings.maxtemp.text = "Max: ${res.main.temp_max.toString()}°C"
                    bindings.mintemp.text = "Min: ${res.main.temp_min.toString()}°C"
                    bindings.wind.text = "${res.wind.speed}"
                    bindings.sunrise.text = "${time(res.sys.sunrise.toLong())}"
                    bindings.sunset.text = "${time(res.sys.sunset.toLong())}"
                    bindings.sealevel.text = "${res.main.sea_level} hPa"
                    bindings.day.text = day()
                    bindings.date.text = date()
                    bindings.city.text = city
                    changeBGOnConditions(condition)
                }
            }
            override fun onFailure(p0: Call<weather>, p1: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun changeBGOnConditions(conditions: String) {
        when (conditions) {
            "Clear Sky", "Sunny", "Clear" -> {
                bindings.root.setBackgroundResource(R.drawable.sunny_background)
                bindings.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                bindings.root.setBackgroundResource(R.drawable.cloud_background)
                bindings.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                bindings.root.setBackgroundResource(R.drawable.rain_background)
                bindings.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                bindings.root.setBackgroundResource(R.drawable.snow_background)
                bindings.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                bindings.root.setBackgroundResource(R.drawable.sunny_background)
                bindings.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        bindings.lottieAnimationView.playAnimation()
    }


    private fun  day(): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))

    }
    private fun  time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))

    }
    private fun  date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))

    }

}