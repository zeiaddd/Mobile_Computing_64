package com.example.expensetracker

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY id DESC")
    fun getAllExpenses(): LiveData<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE date = :selectedDate")
    fun getExpensesByDate(selectedDate: String): LiveData<List<ExpenseEntity>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date = :selectedDate")
    suspend fun getTotalForDate(selectedDate: String): Double?
}