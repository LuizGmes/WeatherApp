package br.com.weatherapp.feature.setting

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import br.com.weatherapp.Const
import br.com.weatherapp.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    private val sp by lazy {
        getSharedPreferences(Const.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initUI()
    }

    private fun initUI() {
        val isCelsius = sp.getBoolean(Const.PREF_IS_CELSIUS, true)
        rbC.isChecked = isCelsius
        rbF.isChecked = !isCelsius

        val isPt = sp.getBoolean(Const.PREF_IS_PT, true)
        rbPt.isChecked = isPt
        rbEn.isChecked = !isPt

        btnSave.setOnClickListener {
            sp.edit().apply {
                putBoolean(Const.PREF_IS_CELSIUS, rbC.isChecked)
                putBoolean(Const.PREF_IS_PT, rbPt.isChecked)
                apply()
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
    }
}
