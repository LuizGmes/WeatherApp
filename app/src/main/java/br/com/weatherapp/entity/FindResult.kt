package br.com.weatherapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class FindResult(
    @SerializedName("list")
    val items: List<City>)

data class City(
    val id : Int = 0,
    val name : String = "",
    @SerializedName("weather")
    val weatherList : List<Weather>,
    val main : Main
)

data class Weather(
    val description : String = "",
    val icon : String = ""
)

data class Main(
    val temp : Float
)

@Entity(tableName = "tb_city")
data class FavoriteCity(
    @PrimaryKey
    val id : Int = 0,
    val name : String = ""
)