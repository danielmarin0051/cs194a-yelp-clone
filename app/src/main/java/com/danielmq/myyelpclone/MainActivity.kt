package com.danielmq.myyelpclone

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.net.NetworkInfo

import android.net.ConnectivityManager
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import java.io.IOException


private const val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
private const val API_KEY = "fxJkzRBbb_uhCQIi3LNHgvQ7lGwNdQ2Quyi5d_rj4ccVwcrd6b0ahUQKVLhRuOzHdhzMien3JfzAjWAZgvMeIS3iYTI9ZcXKVDOAAQQK8AIESSp1sGUK-PdzQ-6NYXYx"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvRestaurants = findViewById<RecyclerView>(R.id.rvRestaurants)

        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants)
        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        val retrofit =
            Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val yelpService =retrofit.create(YelpService::class.java)

        yelpService.searchRestaurants("BEARER $API_KEY","Avocado Toast", "New York").enqueue(object: Callback<YelpSearchResult> {
            override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                Log.i(TAG, "onResponse $response");
                val body = response.body()
                if (body == null) {
                    Log.w(TAG, "Did not receive valid response body from Yelp API... exiting")
                    return
                }
                restaurants.addAll(body.restaurants)
                adapter.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
                val isOnline = isOnline()
                Log.i(TAG, "isOnline $isOnline")
                if (!isOnline) {
                    this@MainActivity.showAlertDialog()
                }
            }

            private fun isOnline(): Boolean {
                val runtime = Runtime.getRuntime()
                try {
                    val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
                    val exitValue = ipProcess.waitFor()
                    return exitValue == 0
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                return false
            }

        })
    }
    fun showAlertDialog() {
        val placeFormView = LayoutInflater.from(this).inflate(R.layout.activity_main, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("You are not connected to the internet!")
            .setView(placeFormView)
            .setPositiveButton("OK", null)
            .show()

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener {
            dialog.dismiss()
        }
    }
}