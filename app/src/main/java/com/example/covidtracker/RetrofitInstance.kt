package com.example.covidtracker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    var retrofit: Retrofit? = null
    var BASE_URL: String = "https://disease.sh/v3/covid-19/"

    @JvmName("getRetrofit1")
    fun getRetrofit(): Retrofit? {

        if(retrofit == null){

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }
        return retrofit
    }

}