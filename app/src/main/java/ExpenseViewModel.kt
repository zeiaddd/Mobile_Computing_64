package com.example.expensetracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExpenseViewModel(private val repo: ExpenseRepo) : ViewModel() {
    val allExpenses = repo.allExpenses

    fun getExpensesByDate(date: String) = repo.expensesByDate(date)

    fun insert(expense: ExpenseEntity) = viewModelScope.launch {
        repo.insert(expense)
    }
}