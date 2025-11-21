package com.example.expensetracker

import Expense.Tracker.R
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var currentDateDisplay: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var expenseNameInput: EditText
    private lateinit var expenseAmountInput: EditText
    private lateinit var addExpenseButton: Button
    private lateinit var filterButton: Button
    private lateinit var showAllButton: Button

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    private var selectedDate: String = dateFormat.format(Date())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupViewModel()
        setupRecyclerView()
        setupClickListeners()

        // Set initial date display
        updateDateDisplay()
        loadAllExpenses()
    }

    private fun initializeViews() {
        currentDateDisplay = findViewById(R.id.currentDateDisplay)
        calendarView = findViewById(R.id.calendarView)
        expenseNameInput = findViewById(R.id.expenseNameInput)
        expenseAmountInput = findViewById(R.id.expenseAmountInput)
        addExpenseButton = findViewById(R.id.addExpenseButton)
        filterButton = findViewById(R.id.filterButton)
        showAllButton = findViewById(R.id.showAllButton)
    }

    private fun setupViewModel() {
        val database = ExpenseDatabase.getDatabase(applicationContext)
        val dao = database.expenseDao()
        val repo = ExpenseRepo(dao)
        val factory = ExpenseViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[ExpenseViewModel::class.java]
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
        // Calendar date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = dateFormat.format(calendar.time)
            updateDateDisplay()
        }

        addExpenseButton.setOnClickListener {
            addExpense()
        }

        filterButton.setOnClickListener {
            filterExpensesByDate()
        }

        showAllButton.setOnClickListener {
            showAllExpenses()
        }
    }

    private fun updateDateDisplay() {
        try {
            val date = dateFormat.parse(selectedDate)
            currentDateDisplay.text = displayDateFormat.format(date!!)
        } catch (e: Exception) {
            currentDateDisplay.text = selectedDate
        }
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
        Toast.makeText(this, "Expense added successfully", Toast.LENGTH_SHORT).show()

        // Refresh the list to show the new expense
        filterExpensesByDate()
    }

    private fun filterExpensesByDate() {
        viewModel.getExpensesByDate(selectedDate).observe(this) { expenses ->
            expenseAdapter.updateExpenses(expenses)

            if (expenses.isEmpty()) {
                Toast.makeText(this, "No expenses found for selected date", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAllExpenses() {
        viewModel.allExpenses.observe(this) { expenses ->
            expenseAdapter.updateExpenses(expenses)
        }
        Toast.makeText(this, "Showing all expenses", Toast.LENGTH_SHORT).show()
    }

    private fun loadAllExpenses() {
        viewModel.allExpenses.observe(this) { expenses ->
            expenseAdapter.updateExpenses(expenses)
        }
    }
}