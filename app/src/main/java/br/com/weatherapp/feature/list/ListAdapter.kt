package br.com.weatherapp.feature.list

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.weatherapp.R
import br.com.weatherapp.entity.City
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.row_city_layout.view.*

class ListAdapter() : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private var items : List<City>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_city_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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
            }
        }
    }

    fun data(items: List<City>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {}
}