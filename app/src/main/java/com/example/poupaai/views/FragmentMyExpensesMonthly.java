package com.example.poupaai.views;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poupaai.adapters.MonthAdapter;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentMyExpensesMonthlyBinding;
import com.example.poupaai.entities.Month;
import com.example.poupaai.entities.User;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyExpensesMonthly extends Fragment {

    private FragmentMyExpensesMonthlyBinding binding;
    private LocalDatabase db;
    private MonthAdapter monthAdapter;
    private List<Month> monthList;
    private User loggedUser;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getArguments() != null) {
            loggedUser = getArguments().getParcelable("user");
            Log.d("FragmentMyExpensesMonthly", "onAttach: loggedUser = " + loggedUser);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMyExpensesMonthlyBinding.inflate(inflater, container, false);
        db = LocalDatabase.getDatabase(requireContext());

        if (loggedUser == null && getArguments() != null) {
            loggedUser = getArguments().getParcelable("user");
        }

        Log.d("FragmentMyExpensesMonthly", "onCreateView: loggedUser = " + loggedUser);

        if (loggedUser != null) {
            monthList = new ArrayList<>(db.monthModel().getMonthsWithExpenses(loggedUser.getUid()));
        } else {
            Log.d("FragmentMyExpensesMonthly", "LoggedUser is null");
            monthList = new ArrayList<>();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        monthAdapter = new MonthAdapter(monthList, db, loggedUser, NavHostFragment.findNavController(this));

        RecyclerView recyclerView = binding.recyclerViewMyExpensesMonthly;
        recyclerView.setAdapter(monthAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
