package com.example.poupaai.views;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentNotificationBinding;
import com.example.poupaai.entities.FriendRequest;
import com.example.poupaai.entities.Notification;
import com.example.poupaai.entities.User;

import java.util.List;
import java.util.Objects;

public class FragmentNotification extends Fragment {
    private FragmentNotificationBinding binding;
    private LocalDatabase db;
    private Notification notification;
    private FriendRequest friendRequest;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationBinding.inflate(inflater, container, false);

        db = LocalDatabase.getDatabase(requireContext());

        Bundle arguments = getArguments();
        if (arguments != null) {
            notification = arguments.getParcelable("notification");
        }

        friendRequest = db.friendRequest().getFindById(notification.getFriendRequestId());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Objects.equals(friendRequest.getStatus(), "pending")) {
            binding.btnAcceptFriendRequest.setOnClickListener(v -> acceptFriendRequest(view));
            binding.btnRefusedFriendRequest.setOnClickListener(v -> refusedFriendRequest(view));
        } else {
            binding.btnAcceptFriendRequest.setAlpha(0.5F);
            binding.btnRefusedFriendRequest.setAlpha(0.5F);
        }
    }

    private void acceptFriendRequest(View view) {

        friendRequest.setStatus("accepted");
        notification.setRead(true);

        db.friendRequest().update(friendRequest);
        db.notification().update(notification);
        showToast("Solicitação de amizade aceita com sucesso");

        Navigation.findNavController(view).navigateUp();
    }

    private void refusedFriendRequest(View view) {

        friendRequest.setStatus("rejected");
        notification.setRead(true);

        db.friendRequest().update(friendRequest);
        db.notification().update(notification);
        showToast("Solicitação de amizade recusada com sucesso");

        Navigation.findNavController(view).navigateUp();
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
