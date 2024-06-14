package com.example.poupaai.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poupaai.adapters.ExpenseAdapter;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentMyExpensesBinding;
import com.example.poupaai.entities.Expense;
import com.example.poupaai.entities.Month;
import com.example.poupaai.entities.User;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyExpenses extends Fragment {
    private FragmentMyExpensesBinding binding;
    private ExpenseAdapter expenseAdapter;
    private ArrayList<Expense> expenseList;
    private Month month;
    private User loggedUser;
    private LocalDatabase db;
    private OnMonthSelectedListener callback;

    public interface OnMonthSelectedListener {
        void onMonthSelected(Month month);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (OnMonthSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMonthSelectedListener");
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMyExpensesBinding.inflate(inflater, container, false);

        db = LocalDatabase.getDatabase(requireContext());

        Bundle arguments = getArguments();
        if (arguments != null) {
            month = arguments.getParcelable("month");
            loggedUser = arguments.getParcelable("user");
        }

        expenseList = new ArrayList<>(db.expenseModel().getExpensesByMonthIdAndUserId(month.getId(), loggedUser.getUid()));

        if (callback != null) {
            callback.onMonthSelected(month);
        }

        expenseAdapter = new ExpenseAdapter(loggedUser, expenseList, month, NavHostFragment.findNavController(this));

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
