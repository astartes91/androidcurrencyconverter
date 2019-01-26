package org.bibliarij.currencyconverter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.jetbrains.anko.doAsync
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {

        private val jacksonMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(KotlinModule())

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://free.currencyconverterapi.com/api/v6/")
            .addConverterFactory(JacksonConverterFactory.create(jacksonMapper))
            .build()

        private val currencyConverterApi: CurrencyConverterApi = retrofit.create(CurrencyConverterApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doAsync {
            val body: CurrenciesResponse = currencyConverterApi.getCurrencies().execute().body()!!
        }
    }
}
