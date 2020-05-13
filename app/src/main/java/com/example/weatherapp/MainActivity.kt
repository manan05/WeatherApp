package com.example.weatherapp

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    val CITY: String = "Delhi,IN"
    val API: String = "e4ab3dd1178ab6dada4f9704a848c4f1"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weatherTask().execute()
    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }


        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                //Extracting JSON returs from the API
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/mm/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )
                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                //Populating extracted data toour views
                findViewById<TextView>(R.id.tvLocation).text = address
                findViewById<TextView>(R.id.tvUpdatedAt).text = updatedAtText
                findViewById<TextView>(R.id.tvStatus).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.tvTemperature).text = temp
                findViewById<TextView>(R.id.tvMaxTemp).text = tempMax
                findViewById<TextView>(R.id.tvMinTemp).text = tempMin
                findViewById<TextView>(R.id.tvSunrise).text = SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.tvSunset).text = SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.tvWind).text = windSpeed + " km/h"
                findViewById<TextView>(R.id.tvHumidity).text = humidity + " %"
                findViewById<TextView>(R.id.tvPressure).text = pressure + " mb"

                //When Views are populated, so hiding the loader and showing the weather

                findViewById<ProgressBar>(R.id.loader).visibility =View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
            }
            catch (e:Exception){
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }

    }
}
