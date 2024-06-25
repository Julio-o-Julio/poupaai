package com.example.poupaai.views;

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

import com.example.poupaai.adapters.FriendAdapter;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentFriendsBinding;
import com.example.poupaai.entities.User;

import java.util.ArrayList;
import java.util.List;

public class FragmentFriends extends Fragment {
    private FragmentFriendsBinding binding;
    private LocalDatabase db;
    private User loggedUser;
    private List<User> friendList;
    private FriendAdapter friendAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFriendsBinding.inflate(inflater, container, false);
        db = LocalDatabase.getDatabase(requireContext());

        if (loggedUser == null && getArguments() != null) {
            loggedUser = getArguments().getParcelable("user");
        }

        if (loggedUser != null) {
            friendList = new ArrayList<>(db.userModel().getAllFriends(loggedUser.getUid()));
        } else {
            friendList = new ArrayList<>();
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        friendAdapter = new FriendAdapter(friendList, loggedUser, NavHostFragment.findNavController(this), requireContext());

        RecyclerView recyclerView = binding.recyclerViewFriends;
        recyclerView.setAdapter(friendAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
