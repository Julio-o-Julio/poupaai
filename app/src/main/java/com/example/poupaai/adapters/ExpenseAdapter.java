package com.example.poupaai.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poupaai.R;
import com.example.poupaai.entities.Expense;
import com.example.poupaai.entities.Month;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenseList;
    private NavController navController;
    private Context context;

    public ExpenseAdapter(List<Expense> expenseList, NavController navController) {
        this.expenseList = expenseList;
        this.navController = navController;
    }

    @NonNull
    @Override
    public ExpenseAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_expense, parent, false
        );
        return new ExpenseAdapter.ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("expense", expense);

                navController.navigate(R.id.action_to_fragment_add_expenses, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView monthName;
        TextView year;
        TextView totalExpenses;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            monthName = itemView.findViewById(R.id.tvMonthName);
            year = itemView.findViewById(R.id.tvYear);
            totalExpenses = itemView.findViewById(R.id.tvTotalExpenses);
        }

        public void bind(Expense expense) {
            monthName.setText(expense.getMonthName());
            year.setText(expense.getYear());
            totalExpenses.setText("error total despesas");
        }
    }
}
