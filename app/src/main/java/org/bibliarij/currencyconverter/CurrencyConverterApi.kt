package org.bibliarij.currencyconverter

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.math.BigDecimal

class CurrencyConverterApi {

    companion object {

        private val jacksonMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(KotlinModule())

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://free.currencyconverterapi.com/api/v6/")
            .addConverterFactory(JacksonConverterFactory.create(jacksonMapper))
            .build()

        private val currencyConverterApi: CurrencyConverterRestApi = retrofit.create(
            CurrencyConverterRestApi::class.java
        )
    }

    fun getCurrencies(): Map<String, Currency> {
        return currencyConverterApi.getCurrencies().execute().body()!!.results
    }

    fun getRate(pair: String): BigDecimal {
        return currencyConverterApi.getRate(pair).execute().body()!!.getValue(pair)
    }
}