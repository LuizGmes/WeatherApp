package br.com.weatherapp.api

import br.com.weatherapp.entity.FindResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("find?units=metric")
    fun find(
        @Query("q") cityName: String,
        @Query("appid") appId: String) : Call<FindResult>
}