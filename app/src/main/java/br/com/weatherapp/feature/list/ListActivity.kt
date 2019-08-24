package br.com.weatherapp.feature.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.text.TextUtils
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
import kotlinx.android.synthetic.main.row_city_layout.*
import kotlinx.android.synthetic.main.row_city_layout.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListActivity : AppCompatActivity() {

    private val adapter = ListAdapter()
    private val roomManager by lazy {
        RoomManager.instance(this)
    }
    private val sp by lazy {
        getSharedPreferences(Const.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

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
                    Log.d("lula", it.name)
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

        loadFavorites()

        btnSearch.setOnClickListener {
            findCity()
        }
    }

    fun findCity() = if (isDeviceConnected()) {
        progressBar.visibility = View.VISIBLE


        val isCelsius = sp.getBoolean(Const.PREF_IS_CELSIUS, true)

        var unit = "imperial"
        when {
            isCelsius -> unit = "metric"
        }

        val isPt = sp.getBoolean(Const.PREF_IS_PT, true)

        var lang = "en"
        when {
            isPt -> lang = "pt"
        }

        val call = RetrofitManager
            .getWeatherService()
            .find(edtCityName.text.toString(),unit, Const.APP_KEY,lang)

        call.enqueue(object : Callback<FindResult> {

            override fun onFailure(call: Call<FindResult>, t: Throwable) {
                Log.e("lula", "Error", t)
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

    fun loadFavorites() {
        if (isDeviceConnected()) {
            progressBar.visibility = View.VISIBLE


            doAsync {
                val list: List<FavoriteCity>? = roomManager.getFavoriteDao().getFavoriteCities()
                uiThread {
                    val cityIdList: ArrayList<String> = ArrayList<String>()
                    list?.forEach {
                        cityIdList.add(it.id.toString())
                    }

                    val isCelsius = sp.getBoolean(Const.PREF_IS_CELSIUS, true)

                    var unit = "imperial"
                    when {
                        isCelsius -> unit = "metric"
                    }

                    val isPt = sp.getBoolean(Const.PREF_IS_PT, true)

                    var lang = "en"
                    when {
                        isPt -> lang = "pt"
                    }

                    val filter: String = TextUtils.join(",", cityIdList)
                    val call = RetrofitManager
                        .getWeatherService()
                        .group(filter, unit, Const.APP_KEY, lang)

                    call.enqueue(object : Callback<FindResult> {

                        override fun onFailure(call: Call<FindResult>, t: Throwable) {
                            Log.e("lula", "Error", t)
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
                }
            }
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
