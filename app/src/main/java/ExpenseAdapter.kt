package com.example.expensetracker

import Expense.Tracker.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private var expenseList: List<ExpenseEntity>,
    private val onItemClick: (ExpenseEntity) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.expenseName)
        val amountTextView: TextView = itemView.findViewById(R.id.expenseAmount)
        val dateTextView: TextView = itemView.findViewById(R.id.expenseDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenseList[position]
        holder.nameTextView.text = expense.name
        holder.amountTextView.text = "$${expense.amount}"
        holder.dateTextView.text = expense.date

        holder.itemView.setOnClickListener {
            onItemClick(expense)
        }
    }

    override fun getItemCount(): Int = expenseList.size

    fun updateExpenses(expenses: List<ExpenseEntity>) {
        expenseList = expenses
        notifyDataSetChanged()
    }
}