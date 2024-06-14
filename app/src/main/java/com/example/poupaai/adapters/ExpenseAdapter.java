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
import com.example.poupaai.entities.User;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {
    private List<Expense> expenseList;
    private Month month;
    private NavController navController;
    private User loggedUser;

    public ExpenseAdapter(User loggedUser, List<Expense> expenseList, Month month, NavController navController) {
        this.loggedUser = loggedUser;
        this.expenseList = expenseList;
        this.month = month;
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
                bundle.putParcelable("month", month);
                bundle.putParcelable("expense", expense);
                bundle.putParcelable("user", loggedUser);

                navController.navigate(R.id.action_to_fragment_add_expenses, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView expenseDay;
        TextView expenseMonthName;
        TextView expenseYear;
        TextView expenseValue;
        TextView expenseCategory;
        TextView expenseDescription;
        TextView expenseStatus;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            expenseDay = itemView.findViewById(R.id.tvExpenseDay);
            expenseMonthName = itemView.findViewById(R.id.tvExpenseMonthName);
            expenseYear = itemView.findViewById(R.id.tvExpenseYear);
            expenseValue = itemView.findViewById(R.id.tvExpenseValue);
            expenseCategory = itemView.findViewById(R.id.tvExpenseCategory);
            expenseDescription = itemView.findViewById(R.id.tvExpenseDescription);
            expenseStatus = itemView.findViewById(R.id.tvExpenseStatus);
        }

        public void bind(Expense expense) {
            expenseDay.setText(String.valueOf(expense.getDay()));
            expenseMonthName.setText(" de " + month.getMonthName());
            expenseYear.setText(String.valueOf(month.getYear()));
            expenseValue.setText(String.format("R$ %.2f", expense.getValue()).replace(".", ","));
            expenseCategory.setText(expense.getCategory());
            expenseDescription.setText(expense.getDescription());
            expenseStatus.setText(expense.getStatus());

            Context context = itemView.getContext();
            int textColor;
            switch (expense.getStatus()) {
                case "Pago":
                    textColor = R.color.status_paid;
                    break;
                case "Cancelado":
                    textColor = R.color.status_cancelled;
                    break;
                case "Atrasado":
                    textColor = R.color.status_delayed;
                    break;
                default:
                    textColor = R.color.status_pending;
                    break;
            }
            expenseStatus.setTextColor(context.getResources().getColor(textColor));
        }
    }
}
