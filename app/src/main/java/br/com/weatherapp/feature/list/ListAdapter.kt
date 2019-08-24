package br.com.weatherapp.feature.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.weatherapp.R
import br.com.weatherapp.database.RoomManager
import br.com.weatherapp.entity.City
import br.com.weatherapp.entity.FavoriteCity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_city_layout.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ListAdapter() : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var items : List<City>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_city_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val roomManager = RoomManager.instance(holder.itemView.context)

        items?.let {
            val city = it[position]

            holder.itemView.apply {
                if (city.weatherList.size > 0) {
                    tvCloudsState.text = city.weatherList[0].description

                    val icon = city.weatherList[0].icon
                    val url = "http://openweathermap.org/img/w/$icon.png"
                    Glide.with(this.context)
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .into(imgIcon)
                }

                tvTemperature.text = city.main.temp.toInt().toString()
                tvCityName.text = city.name

                doAsync {
                    val favoriteCity: FavoriteCity = roomManager.getFavoriteDao().favoriteById(city.id)

                    uiThread {
                        if(favoriteCity != null) {
                            imgFavorite.setImageResource(R.drawable.ic_star_yellow_24dp)
                        } else {
                            imgFavorite.setImageResource(R.drawable.ic_star_border_gray_24dp)
                        }
                    }
                }

                imgFavorite.setOnClickListener {
                    doAsync {
                        val cityResult: FavoriteCity = roomManager.getFavoriteDao().favoriteById(city.id)
                        if (cityResult == null) {
                            roomManager.getFavoriteDao().addCity(FavoriteCity(city.id, city.name))

                            uiThread {
                                imgFavorite.setImageResource(R.drawable.ic_star_yellow_24dp)
                            }
                        } else {
                            roomManager.getFavoriteDao().delete(cityResult)
                            uiThread {
                                imgFavorite.setImageResource(R.drawable.ic_star_border_gray_24dp)
                            }
                        }
                    }
                }

            }
        }
    }

    fun data(items: List<City>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {}
}