package com.example.poupaai.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poupaai.R;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.entities.Expense;
import com.example.poupaai.entities.Month;
import com.example.poupaai.entities.User;

import java.util.ArrayList;
import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {
    private List<Month> monthList;
    private LocalDatabase db;
    private User loggedUser;
    private NavController navController;

    public MonthAdapter(List<Month> monthList, LocalDatabase db, User loggedUser, NavController navController) {
        this.monthList = monthList;
        this.db = db;
        this.loggedUser = loggedUser;
        this.navController = navController;
    }

    @NonNull
    @Override
    public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_month, parent, false
        );
        return new MonthViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
        Month month = monthList.get(position);
        holder.bind(month);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("month", month);
                bundle.putParcelable("user", loggedUser);

                navController.navigate(R.id.action_to_fragment_my_expenses, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return monthList.size();
    }

    class MonthViewHolder extends RecyclerView.ViewHolder {
        TextView monthName;
        TextView year;
        TextView totalExpenses;

        public MonthViewHolder(@NonNull View itemView) {
            super(itemView);

            monthName = itemView.findViewById(R.id.tvMonthName);
            year = itemView.findViewById(R.id.tvYear);
            totalExpenses = itemView.findViewById(R.id.tvTotalExpenses);
        }

        public void bind(Month month) {
            monthName.setText(month.getMonthName());
            year.setText(String.valueOf(month.getYear()));

            List<Expense> expenseList = db.expenseModel().getExpensesByMonthIdAndUserId(month.getId(), loggedUser.getUid());
            double total = 0;
            for (Expense expense : expenseList) {
                total += expense.getValue();
            }
            totalExpenses.setText(String.format("Total: R$ %.2f", total).replace(".", ","));
        }
    }
}
