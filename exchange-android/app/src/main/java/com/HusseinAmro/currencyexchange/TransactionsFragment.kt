package com.HusseinAmro.currencyexchange

import android.util.Log
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.HusseinAmro.currencyexchange.api.Authentication
import com.HusseinAmro.currencyexchange.api.ExchangeService
import com.HusseinAmro.currencyexchange.api.model.Transaction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionsFragment : Fragment() {
    private var listview: ListView? = null
    private var transactions: ArrayList<Transaction>? = ArrayList()
    private var adapter: TransactionAdapter? = null

    class TransactionAdapter(
        private val inflater: LayoutInflater,
        private val dataSource: List<Transaction>
    ) : BaseAdapter() {
        override fun getView(
            position: Int, convertView: View?, parent:
            ViewGroup?
        ): View {
            val view: View = inflater.inflate(
                R.layout.item_transaction,
                parent, false
            )
            val transactionLine = view.findViewById<TextView>(R.id.transaction_line)
            val dateAdded = view.findViewById<TextView>(R.id.date_added)
            val transactionType = view.findViewById<TextView>(R.id.transaction_type)

            val thisTransaction = dataSource[position]

            transactionLine.text =
                if (thisTransaction.usdToLbp == true)
                    "Transaction: ${String.format("%.2f",thisTransaction.usdAmount)} USD to ${String.format("%.2f",thisTransaction.lbpAmount)} LBP"
                else
                    "Transaction: ${String.format("%.2f",thisTransaction.lbpAmount)} LBP to ${String.format("%.2f",thisTransaction.usdAmount)} USD"

            transactionType.text =
                if (thisTransaction.usdToLbp == true)
                    "Type: USD to LBP"
                else
                    "Type: LBP to USD"

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(thisTransaction.dateAdded)
            val displayDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            dateAdded.text = "Date: ${displayDateFormat.format(date)}"
            return view
        }

        override fun getItem(position: Int): Any {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return dataSource[position].id?.toLong() ?: 0
        }

        override fun getCount(): Int {
            return dataSource.size
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    fetchTransactions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_transactions,
            container, false)
        listview = view.findViewById(R.id.listview)
        adapter =
            TransactionAdapter(layoutInflater, transactions!!)
        listview?.adapter = adapter
        return view
    }

    private fun fetchTransactions() {
        if (Authentication.getToken() != null) {
            ExchangeService.exchangeApi()
                .getTransactions("Bearer ${Authentication.getToken()}")
                .enqueue(object : Callback<List<Transaction>> {
                    override fun onFailure(call: Call<List<Transaction>>,
                                           t: Throwable) {
                        return
                    }
                    override fun onResponse(
                        call: Call<List<Transaction>>,
                        response: Response<List<Transaction>>
                    ) {
                        transactions?.addAll(response.body()!!)
                        adapter?.notifyDataSetChanged()
                    }
                })
        }
    }


}
