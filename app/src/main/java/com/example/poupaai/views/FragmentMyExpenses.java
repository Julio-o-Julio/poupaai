package com.example.poupaai.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentMyExpensesBinding;
import com.example.poupaai.entities.Expense;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyExpenses extends Fragment {
    private FragmentMyExpensesBinding binding;
    private LocalDatabase db;
    private ExpenseAdapter expenseAdapter;
    private List<Expense> expenseList;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMyExpensesBinding.inflate(inflater, container, false);

        db = LocalDatabase.getDatabase(requireContext());

        expenseList = new ArrayList<>(db.expenseModel().getAll());

        expenseAdapter = new ExpenseAdapter(expenseList, NavHostFragment.findNavController(this));

        RecyclerView recyclerView = binding.recyclerViewFragmentMyExpenses;
        recyclerView.setAdapter(expenseAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
