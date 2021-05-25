package com.example.cowinalert

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://cdn-api.co-vin.in/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface CowinAPIService {
    @GET("api/v2/appointment/sessions/public/calendarByPin?pincode=121001&date=28-05-2021")
    fun getCowinData():
            Call<Centers>
}

object CowinAPI {
    val retrofitService : CowinAPIService by lazy {
        retrofit.create(CowinAPIService::class.java)
    }
}