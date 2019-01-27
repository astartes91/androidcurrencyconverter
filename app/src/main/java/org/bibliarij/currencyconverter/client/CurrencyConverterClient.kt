package org.bibliarij.currencyconverter.client

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bibliarij.currencyconverter.model.CurrenciesResponse
import org.bibliarij.currencyconverter.model.Currency
import org.jetbrains.anko.runOnUiThread
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.File
import java.math.BigDecimal
import java.util.*

class CurrencyConverterClient (private val context: Context) {

    private val currencyConverterClient: CurrencyConverterRestClient = buildRestClient()

    fun getCurrencies(): Map<String, Currency> {

        val body: CurrenciesResponse? = currencyConverterClient.getCurrencies().execute().body()

        if (body != null){
            return body.results
        }

        return Collections.emptyMap()
    }

    fun getRate(pair: String): BigDecimal {

        val body: Map<String, BigDecimal>? = currencyConverterClient.getRate(pair).execute().body()

        if (body != null){
            return body.getValue(pair)
        }

        return BigDecimal.ZERO
    }

    private fun buildRestClient(): CurrencyConverterRestClient {

        val cacheSize: Long = (5 * 1024 * 1024).toLong()
        val cacheDir: File = context.cacheDir
        val myCache: Cache = Cache(cacheDir, cacheSize)

        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .cache(myCache)
            .addInterceptor { chain ->

                var request: Request = chain.request()
                request = if (hasNetwork()){
                    /*
                     *  If there is Internet, get the cache that was stored 5 seconds ago.
                     *  If the cache is older than 5 seconds, then discard it,
                     *  and indicate an error in fetching the response.
                     *  The 'max-age' attribute is responsible for this behavior.
                     */
                    request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                } else {
                    /*
                     *  If there is no Internet, get the cache that was stored 7 days ago.
                     *  If the cache is older than 7 days, then discard it,
                     *  and indicate an error in fetching the response.
                     *  The 'max-stale' attribute is responsible for this behavior.
                     *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
                     */
                    request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                        .build()
                }

                chain.proceed(request)
            }
            .build()

        val jacksonMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(KotlinModule())

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://free.currencyconverterapi.com/api/v6/")
            .addConverterFactory(JacksonConverterFactory.create(jacksonMapper))
            .client(okHttpClient)
            .build()

        return retrofit.create(CurrencyConverterRestClient::class.java)
    }

    private fun hasNetwork(): Boolean {
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val result: Boolean = activeNetwork != null && activeNetwork.isConnected

        if(!result){
            context.runOnUiThread {
                Toast.makeText(
                    context, "No Internet connection. Trying to use cache data if exists...", Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        return result
    }
}