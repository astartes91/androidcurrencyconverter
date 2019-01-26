package org.bibliarij.currencyconverter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    companion object {

        private val currencyConverterApi: CurrencyConverterApi = CurrencyConverterApi()
    }

    fun convertButtonOnClick(view: View) {

        val pair: String = "${sourceCurrencySpinner.selectedItem}_${targetCurrencySpinner.selectedItem}"

        doAsync {

            val rate = currencyConverterApi.getRate(pair)
            val result: BigDecimal = rate.multiply(BigDecimal(sourceCurrencyAmountTextView.text.toString()))

            result
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        doAsync {

            val currencies: Map<String, Currency> = currencyConverterApi.getCurrencies()
            val arrayAdapter: ArrayAdapter<String> = ArrayAdapter(
                applicationContext, android.R.layout.simple_spinner_item, currencies.keys.toList().sorted()
            )
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            runOnUiThread {
                sourceCurrencySpinner.adapter = arrayAdapter
                targetCurrencySpinner.adapter = arrayAdapter
            }
        }
    }
}
