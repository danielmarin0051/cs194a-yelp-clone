package com.danielmq.myyelpclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
private const val API_KEY = "fxJkzRBbb_uhCQIi3LNHgvQ7lGwNdQ2Quyi5d_rj4ccVwcrd6b0ahUQKVLhRuOzHdhzMien3JfzAjWAZgvMeIS3iYTI9ZcXKVDOAAQQK8AIESSp1sGUK-PdzQ-6NYXYx"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit =
            Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val yelpService =retrofit.create(YelpService::class.java)
        yelpService.searchRestaurants("BEARER $API_KEY","Avocado Toast", "New York").enqueue(object: Callback<YelpSearchResult> {
            override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                Log.i(TAG, "onResponse $response");
            }

            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }

        })

    }
}