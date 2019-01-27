package org.bibliarij.currencyconverter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    private val currencyConverterApi: CurrencyConverterApi = CurrencyConverterApi(this)

    fun convertButtonOnClick(view: View) {

        val sourceCurrency = sourceCurrencySpinner.selectedItem
        if (sourceCurrency == null){
            Toast.makeText(this, "Source currency is not selected!", Toast.LENGTH_SHORT).show()
            return
        }
        val targetCurrency = targetCurrencySpinner.selectedItem
        if (targetCurrency == null){
            Toast.makeText(this, "Target currency is not selected!", Toast.LENGTH_SHORT).show()
            return
        }
        val pair: String = "${sourceCurrency}_$targetCurrency"

        doAsync {

            val rate: BigDecimal = currencyConverterApi.getRate(pair)
            var sourceAmountStr: String = sourceCurrencyAmountEditText.text.toString()
            if(sourceAmountStr.isEmpty()){
                sourceCurrencyAmountEditText.text.append("0")
                sourceAmountStr = sourceCurrencyAmountEditText.text.toString()
            }
            val sourceAmount: BigDecimal = BigDecimal(sourceAmountStr)
            val result: BigDecimal = rate.multiply(sourceAmount)

            runOnUiThread {
                resultTextView.text = "Результат: $sourceAmount $sourceCurrency -> $result $targetCurrency"
                resultTextView.visibility = View.VISIBLE
            }
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
