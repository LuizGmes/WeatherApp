package br.com.weatherapp.feature.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.weatherapp.Const
import br.com.weatherapp.R
import br.com.weatherapp.api.RetrofitManager
import br.com.weatherapp.database.RoomManager
import br.com.weatherapp.entity.FavoriteCity
import br.com.weatherapp.entity.FindResult
import br.com.weatherapp.feature.setting.SettingActivity
import kotlinx.android.synthetic.main.activity_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListActivity : AppCompatActivity() {

    private val roomManager by lazy { RoomManager.instance(this) }

    private val adapter = ListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

//        roomManager.getFavoriteDao()
//            .addCity(FavoriteCity(10, "Recife"))
//
//        roomManager.getFavoriteDao()
//            .addCity(FavoriteCity(11, "Olinda"))
//
//        val items = roomManager.getFavoriteDao()
//            .getFavoriteCities()
//
//        items.forEach {
//            Log.d("WELL", it.name)
//        }

        getFavoriteCitiesAsync()
        initUI()
    }

    @SuppressLint("StaticFieldLeak")
    private fun getFavoriteCitiesAsync() {
        val task = object : AsyncTask<Void, Void, List<FavoriteCity>>() {

            override fun onPreExecute() {
                progressBar.visibility = View.VISIBLE
            }

            override fun doInBackground(vararg p0: Void?): List<FavoriteCity> {
                return RoomManager.instance(this@ListActivity)
                    .getFavoriteDao()
                    .getFavoriteCities()
            }

            override fun onPostExecute(result: List<FavoriteCity>?) {
                super.onPostExecute(result)
                result?.forEach {
                    Log.d("WELL", it.name)
                }
                progressBar.visibility = View.GONE
            }
        }
        task.execute()
    }

    private fun initUI() {
        rvCities.apply {
            layoutManager = LinearLayoutManager(this@ListActivity)
            adapter = this@ListActivity.adapter
        }

        btnSearch.setOnClickListener {
            findCity()
        }
    }

    fun findCity() {
        if (isDeviceConnected()) {
            progressBar.visibility = View.VISIBLE

            val call = RetrofitManager
                .getWeatherService()
                .find(edtCityName.text.toString(), Const.APP_KEY)

            call.enqueue(object : Callback<FindResult> {

                override fun onFailure(call: Call<FindResult>, t: Throwable) {
                    Log.e("WELL", "Error", t)
                    progressBar.visibility = View.GONE
                }

                override fun onResponse(call: Call<FindResult>, response: Response<FindResult>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            adapter.data(it.items)
                        }
                    }
                    progressBar.visibility = View.GONE
                }
            })
        } else {
            Toast.makeText(this,
                "Device is not connected. Try again later",
                Toast.LENGTH_LONG).show()
        }
    }

    fun isDeviceConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected();
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_setting) {
            startActivity(Intent(this,
                SettingActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
