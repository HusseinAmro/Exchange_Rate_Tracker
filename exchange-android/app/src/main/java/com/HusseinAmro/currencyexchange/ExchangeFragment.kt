package com.HusseinAmro.currencyexchange

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.HusseinAmro.currencyexchange.api.ExchangeService
import com.HusseinAmro.currencyexchange.api.model.ExchangeRates
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExchangeFragment : Fragment() {

    private lateinit var Result: TextView
    private lateinit var buyUsd: TextView
    private lateinit var sellUsd: TextView
    private lateinit var Amount: EditText
    private lateinit var Spinner: Spinner
    private lateinit var Button: Button
    private var buyRate: Float? = null
    private var sellRate: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchRates()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view: View = inflater.inflate(R.layout.fragment_exchange,
            container, false)

        Result = view.findViewById(R.id.result)
        buyUsd = view.findViewById(R.id.txtBuyUsdRate)
        sellUsd = view.findViewById(R.id.txtSellUsdRate)
        Amount = view.findViewById(R.id.amount)
        Spinner = view.findViewById(R.id.spinner)
        Button = view.findViewById(R.id.button)

        val Adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.options,
            android.R.layout.simple_spinner_item
        )
        Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        Spinner.adapter = Adapter

        Button.setOnClickListener {
            calculateExchange()
        }

        fetchRates()
        return view
    }

    private fun fetchRates() {
        ExchangeService.exchangeApi().getExchangeRates().enqueue(object : Callback<ExchangeRates> {
            override fun onResponse(call: Call<ExchangeRates>, response: Response<ExchangeRates>) {
                val responseBody: ExchangeRates? = response.body();
                if (responseBody != null) {
                    val buyRate = String.format("%.2f", responseBody.lbpToUsd)
                    val sellRate = String.format("%.2f", responseBody.usdToLbp)
                    val buyRateTextView = view?.findViewById<TextView>(R.id.txtBuyUsdRate)
                    val sellRateTextView = view?.findViewById<TextView>(R.id.txtSellUsdRate)
                    buyRateTextView?.text = "$buyRate LBP"
                    sellRateTextView?.text = "$sellRate LBP"
                }
                //For Calculator
                buyRate = responseBody?.lbpToUsd
                sellRate = responseBody?.usdToLbp
            }

            override fun onFailure(call: Call<ExchangeRates>, t: Throwable) {
                return;
            }
        })
    }

    private fun calculateExchange() {
        val inp = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inp.hideSoftInputFromWindow(view?.windowToken, 0)
        val amountInput = Amount.text.toString().toFloatOrNull()

        if ( sellRate != null && buyRate != null && sellRate?.equals(0) != true
            && buyRate?.equals(0) != true && amountInput != null)
        {
            val selectedExchange = when (Spinner.selectedItem.toString()) {
                "USD to LBP" -> amountInput * sellRate!!
                "LBP to USD" -> amountInput / buyRate!!
                else -> 0f
            }
            if (Spinner.selectedItem.toString() == "USD to LBP") {
                Result.text = String.format("%.2f", selectedExchange) + " LBP"
            }
            else {
                Result.text = String.format("%.2f", selectedExchange) + " USD"
            }
        } else {
            Result.text = "Invalid input"
        }
    }
}