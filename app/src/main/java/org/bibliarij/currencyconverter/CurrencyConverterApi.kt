package org.bibliarij.currencyconverter

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.jetbrains.anko.runOnUiThread
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.math.BigDecimal
import java.util.*

class CurrencyConverterApi (private val context: Context) {

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

        if(checkInternet()){
            return currencyConverterApi.getCurrencies().execute().body()!!.results
        }

        return Collections.emptyMap()
    }

    fun getRate(pair: String): BigDecimal {
        if(checkInternet()){
            return currencyConverterApi.getRate(pair).execute().body()!!.getValue(pair)
        }

        return BigDecimal.ZERO
    }

    private fun checkInternet(): Boolean {

        if (!hasNetwork()) {
            context.runOnUiThread {
                Toast.makeText(context, "No Internet connection!", Toast.LENGTH_SHORT).show()
            }

            return false
        }

        return true
    }

    private fun hasNetwork(): Boolean {
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }
}