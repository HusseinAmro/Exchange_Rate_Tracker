package com.HusseinAmro.currencyexchange.api

import com.HusseinAmro.currencyexchange.api.model.ExchangeRates
import com.HusseinAmro.currencyexchange.api.model.Token
import com.HusseinAmro.currencyexchange.api.model.Transaction
import com.HusseinAmro.currencyexchange.api.model.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

object ExchangeService {
    private const val API_URL: String = "http://192.168.43.51:5000"
    fun exchangeApi(): Exchange {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        // Create an instance of our Exchange API interface.
        return retrofit.create(Exchange::class.java);
    }

    interface Exchange {
        @GET("/exchangeRate")
        fun getExchangeRates(): Call<ExchangeRates>

        @POST("/transaction")
        fun addTransaction(@Body transaction: Transaction,
                           @Header("Authorization") authorization: String?): Call<Any>
        @GET("/transactionAndr")
        fun getTransactions(@Header("Authorization") authorization: String):
                Call<List<Transaction>>

        @POST("/user")
        fun addUser(@Body user: User): Call<User>

        @POST("/authentication")
        fun authenticate(@Body user: User): Call<Token>

    }
}