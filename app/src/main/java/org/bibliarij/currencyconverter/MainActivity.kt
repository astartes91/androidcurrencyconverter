package org.bibliarij.currencyconverter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import java.math.BigDecimal

class MainActivity : AppCompatActivity() {

    private lateinit var currencyConverterClient: CurrencyConverterClient

    private val sourceCurrencyAmountTextWatcher: TextWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            if(!s.isNullOrEmpty()){
                handleSourceAmountChange()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    private val targetCurrencyAmountTextWatcher: TextWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            if(!s.isNullOrEmpty()){
                handleTargetAmountChange()
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currencyConverterClient = CurrencyConverterClient(this)

        sourceCurrencyAmountEditText.addTextChangedListener(sourceCurrencyAmountTextWatcher)
        targetCurrencyAmountEditText.addTextChangedListener(targetCurrencyAmountTextWatcher)

        doAsync {

            val currencies: Map<String, Currency> = currencyConverterClient.getCurrencies()
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

    private fun handleSourceAmountChange() {
        val sourceCurrency = sourceCurrencySpinner.selectedItem
        if (sourceCurrency == null) {
            Toast.makeText(this, "Source currency is not selected!", Toast.LENGTH_SHORT).show()
            return
        }
        val targetCurrency = targetCurrencySpinner.selectedItem
        if (targetCurrency == null) {
            Toast.makeText(this, "Target currency is not selected!", Toast.LENGTH_SHORT).show()
            return
        }
        val pair: String = "${sourceCurrency}_$targetCurrency"

        doAsync {

            val rate: BigDecimal = currencyConverterClient.getRate(pair)
            var sourceAmountStr: String = sourceCurrencyAmountEditText.text.toString()
            if (sourceAmountStr.isEmpty()) {
                sourceAmountStr = "0"
            }
            val sourceAmount: BigDecimal = BigDecimal(sourceAmountStr)
            val result: BigDecimal = rate.multiply(sourceAmount)

            runOnUiThread {
                targetCurrencyAmountEditText.removeTextChangedListener(targetCurrencyAmountTextWatcher)
                targetCurrencyAmountEditText.text.clear()
                targetCurrencyAmountEditText.text.append(result.toString())
                targetCurrencyAmountEditText.addTextChangedListener(targetCurrencyAmountTextWatcher)
            }
        }
    }

    private fun handleTargetAmountChange() {
        val sourceCurrency = targetCurrencySpinner.selectedItem
        if (sourceCurrency == null) {
            Toast.makeText(this, "Source currency is not selected!", Toast.LENGTH_SHORT).show()
            return
        }
        val targetCurrency = sourceCurrencySpinner.selectedItem
        if (targetCurrency == null) {
            Toast.makeText(this, "Target currency is not selected!", Toast.LENGTH_SHORT).show()
            return
        }
        val pair: String = "${sourceCurrency}_$targetCurrency"

        doAsync {

            val rate: BigDecimal = currencyConverterClient.getRate(pair)
            var sourceAmountStr: String = targetCurrencyAmountEditText.text.toString()
            if (sourceAmountStr.isEmpty()) {
                sourceAmountStr = "0"
            }
            val sourceAmount: BigDecimal = BigDecimal(sourceAmountStr)
            val result: BigDecimal = rate.multiply(sourceAmount)

            runOnUiThread {
                sourceCurrencyAmountEditText.removeTextChangedListener(sourceCurrencyAmountTextWatcher)
                sourceCurrencyAmountEditText.text.clear()
                sourceCurrencyAmountEditText.text.append(result.toString())
                sourceCurrencyAmountEditText.addTextChangedListener(sourceCurrencyAmountTextWatcher)
            }
        }
    }
}
