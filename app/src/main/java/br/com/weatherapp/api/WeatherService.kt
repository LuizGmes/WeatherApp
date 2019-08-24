package br.com.weatherapp.api

import br.com.weatherapp.entity.FindResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("find")
    fun find(
        @Query("q") cityName: String,
        @Query("units") unit: String,
        @Query("appid") appId: String,
        @Query("lang") lang: String) : Call<FindResult>

    @GET("group")
    fun group(@Query("id") ids: String,
              @Query("units") unit: String,
              @Query("appid") appid: String,
              @Query("lang") lang: String): Call<FindResult>


}