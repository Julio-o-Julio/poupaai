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

import com.example.poupaai.adapters.MonthAdapter;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentMyExpensesMonthlyBinding;
import com.example.poupaai.entities.Month;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyExpensesMonthly extends Fragment {

    private FragmentMyExpensesMonthlyBinding binding;
    private LocalDatabase db;
    private MonthAdapter monthAdapter;
    private List<Month> monthcList;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentMyExpensesMonthlyBinding.inflate(inflater, container, false);

        db = LocalDatabase.getDatabase(requireContext());

        monthcList = new ArrayList<>(db.monthModel().getAll());

        monthAdapter = new MonthAdapter(monthcList, NavHostFragment.findNavController(this));

        RecyclerView recyclerView = binding.recyclerViewMyExpensesMonthly;
        recyclerView.setAdapter(monthAdapter);
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