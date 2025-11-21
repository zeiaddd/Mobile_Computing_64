package com.example.expensetracker

import Expense.Tracker.R
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.*

class TotalSpendActivity : AppCompatActivity() {

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var dateTextView: TextView
    private lateinit var totalAmountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_spend)

        setupViewModel()
        initializeViews()
        calculateTotalSpend()
    }

    private fun setupViewModel() {
        val database = ExpenseDatabase.getDatabase(applicationContext)
        val dao = database.expenseDao()
        val repo = ExpenseRepo(dao)
        val factory = ExpenseViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[ExpenseViewModel::class.java]
    }

    private fun initializeViews() {
        dateTextView = findViewById(R.id.dateTextView)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)
    }

    private fun calculateTotalSpend() {
        val selectedDate = intent.getStringExtra("SELECTED_DATE") ?: return

        // Format the display
        dateTextView.text = "Total Spend on $selectedDate:"

        viewModel.getExpensesByDate(selectedDate).observe(this) { expenses ->
            val total = expenses.sumOf { it.amount }
            totalAmountTextView.text = String.format("%.1f", total)
        }
    }
}