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

import com.example.poupaai.adapters.NotificationAdapter;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentMyNotificationsBinding;
import com.example.poupaai.entities.Notification;
import com.example.poupaai.entities.User;

import java.util.ArrayList;
import java.util.List;

public class FragmentMyNotifications extends Fragment {
    private FragmentMyNotificationsBinding binding;
    private LocalDatabase db;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private User loggedUser;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMyNotificationsBinding.inflate(inflater, container, false);
        db = LocalDatabase.getDatabase(requireContext());

        if (loggedUser == null && getArguments() != null) {
            loggedUser = getArguments().getParcelable("user");
        }

        if (loggedUser != null) {
            notificationList = new ArrayList<>(db.notification().getUnreadNotifications(loggedUser.getUid()));
        } else {
            notificationList = new ArrayList<>();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        notificationAdapter = new NotificationAdapter(notificationList, db, loggedUser, NavHostFragment.findNavController(this));

        RecyclerView recyclerView = binding.recyclerViewMyNotifications;
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
