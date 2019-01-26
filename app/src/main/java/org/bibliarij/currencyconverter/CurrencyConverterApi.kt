package org.bibliarij.currencyconverter

import retrofit2.Call
import retrofit2.http.GET

interface CurrencyConverterApi {

    @GET("currencies")
    fun getCurrencies(): Call<CurrenciesResponse>
}