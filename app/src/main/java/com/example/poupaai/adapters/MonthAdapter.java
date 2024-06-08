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
import com.example.poupaai.entities.Month;

import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {
    private List<Month> monthList;
    private NavController navController;
    private Context context;

    public MonthAdapter(List<Month> monthList, NavController navController) {
        this.monthList = monthList;
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

                navController.navigate(R.id.action_to_fragment_add_expenses, bundle);
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
            year.setText(month.getYear());
            totalExpenses.setText("error total despesas");
        }
    }
}
