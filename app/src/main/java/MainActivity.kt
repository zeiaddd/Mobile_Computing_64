package com.example.expensetracker

import Expense.Tracker.R
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var currentDateDisplay: TextView
    private lateinit var expenseNameInput: EditText
    private lateinit var expenseAmountInput: EditText
    private lateinit var dateSelectButton: Button
    private lateinit var addExpenseButton: Button
    private lateinit var showAllButton: Button

    private var selectedDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupViewModel()
        setupRecyclerView()
        setupClickListeners()
        updateDateDisplay()
    }

    private fun initializeViews() {
        currentDateDisplay = findViewById(R.id.currentDateDisplay)
        expenseNameInput = findViewById(R.id.expenseNameInput)
        expenseAmountInput = findViewById(R.id.expenseAmountInput)
        dateSelectButton = findViewById(R.id.dateSelectButton)
        addExpenseButton = findViewById(R.id.addExpenseButton)
        showAllButton = findViewById(R.id.showAllButton)
    }

    private fun setupViewModel() {
        val database = ExpenseDatabase.getDatabase(applicationContext)
        val dao = database.expenseDao()
        val repo = ExpenseRepo(dao)
        val factory = ExpenseViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[ExpenseViewModel::class.java]

        // Observe all expenses initially
        viewModel.allExpenses.observe(this) { expenses ->
            expenseAdapter.updateExpenses(expenses)
        }
    }

    private fun setupRecyclerView() {
        val recyclerView: RecyclerView = findViewById(R.id.expensesRecyclerView)
        expenseAdapter = ExpenseAdapter(emptyList()) { expense ->
            val intent = Intent(this, TotalSpendActivity::class.java).apply {
                putExtra("SELECTED_DATE", expense.date)
            }
            startActivity(intent)
        }
        recyclerView.adapter = expenseAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        dateSelectButton.setOnClickListener { showDatePicker() }
        addExpenseButton.setOnClickListener { addExpense() }
        showAllButton.setOnClickListener { showAllExpenses() }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            updateDateDisplay()
            filterByDate()
        }, year, month, day)
        datePicker.show()
    }

    private fun addExpense() {
        val name = expenseNameInput.text.toString().trim()
        val amountText = expenseAmountInput.text.toString().trim()

        if (name.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this, "Please enter both name and amount", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val expense = ExpenseEntity(
            name = name,
            amount = amount,
            date = selectedDate
        )

        viewModel.insert(expense)
        expenseNameInput.text.clear()
        expenseAmountInput.text.clear()
        Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show()
    }

    private fun filterByDate() {
        viewModel.getExpensesByDate(selectedDate).observe(this) { expenses ->
            expenseAdapter.updateExpenses(expenses)
        }
    }

    private fun showAllExpenses() {
        viewModel.allExpenses.observe(this) { expenses ->
            expenseAdapter.updateExpenses(expenses)
        }
        currentDateDisplay.text = "Showing All Expenses"
    }

    private fun updateDateDisplay() {
        currentDateDisplay.text = "Selected Date: $selectedDate"
    }
}