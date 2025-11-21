package com.example.expensetracker

class ExpenseRepo(private val dao: ExpenseDao) {
    val allExpenses = dao.getAllExpenses()

    fun expensesByDate(date: String) = dao.getExpensesByDate(date)

    suspend fun insert(expense: ExpenseEntity) = dao.insert(expense)

    suspend fun getTotal(date: String) = dao.getTotalForDate(date)
}