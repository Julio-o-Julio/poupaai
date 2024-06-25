package com.example.poupaai.views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.poupaai.adapters.MonthAdapter;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentExpensesFriendsBinding;
import com.example.poupaai.entities.Month;
import com.example.poupaai.entities.User;

import java.util.ArrayList;
import java.util.List;

public class FragmentExpensesFriends extends Fragment {
    private FragmentExpensesFriendsBinding binding;
    private LocalDatabase db;
    private MonthAdapter monthAdapter;
    private List<Month> monthList;
    private User friend;
    private User loggedUser;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentExpensesFriendsBinding.inflate(inflater, container, false);
        db = LocalDatabase.getDatabase(requireContext());

        if (friend == null && getArguments() != null) {
            friend = getArguments().getParcelable("friend");
            loggedUser = getArguments().getParcelable("user");
        }

        if (friend != null) {
            if (getActivity() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Despesas de " + friend.getUsername());
            }

            monthList = new ArrayList<>(db.monthModel().getMonthsWithExpenses(friend.getUid()));
        } else {
            monthList = new ArrayList<>();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        monthAdapter = new MonthAdapter(monthList, db, loggedUser, friend, NavHostFragment.findNavController(this));

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
