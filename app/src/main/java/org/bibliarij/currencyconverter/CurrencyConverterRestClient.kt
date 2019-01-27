package org.bibliarij.currencyconverter

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.math.BigDecimal

interface CurrencyConverterRestClient {

    @GET("currencies")
    fun getCurrencies(): Call<CurrenciesResponse>

    @GET("convert?compact=ultra")
    fun getRate(@Query("q") pair: String): Call<Map<String, BigDecimal>>
}