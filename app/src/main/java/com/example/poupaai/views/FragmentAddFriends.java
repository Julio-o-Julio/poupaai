package com.example.poupaai.views;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentAddFriendsBinding;
import com.example.poupaai.entities.FriendRequest;
import com.example.poupaai.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FragmentAddFriends extends Fragment {
    private FragmentAddFriendsBinding binding;
    private LocalDatabase db;
    private User loggedUser = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddFriendsBinding.inflate(inflater, container, false);

        db = LocalDatabase.getDatabase(requireContext());

        Bundle arguments = getArguments();
        if (arguments != null) {
            loggedUser = arguments.getParcelable("user");
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSendFriendRequest.setOnClickListener(v -> sendFriendRequest(view));
        binding.btnRemoveFriendRequest.setOnClickListener(v -> removeFriendRequest(view));
    }

    private void sendFriendRequest(View view) {
        String friendEmail = binding.edtFriendEmail.getText().toString();

        if (!validateFriendRequestFields(friendEmail, "sendRequest")) {
            return;
        }

        User user = db.userModel().findByEmail(friendEmail);

        FriendRequest newFriendRequest = new FriendRequest();
        newFriendRequest.setSenderId(loggedUser.getUid());
        newFriendRequest.setReceiverId(user.getUid());
        newFriendRequest.setStatus("pending");

        db.friendRequest().insert(newFriendRequest);
        showToast("Solicitação de amizade enviada com sucesso");
    }

    private void removeFriendRequest(View view) {
        String friendEmail = binding.edtFriendEmail.getText().toString();

        if (!validateFriendRequestFields(friendEmail, "removeRequest")) {
            return;
        }

        User user = db.userModel().findByEmail(friendEmail);

        List<FriendRequest> friendRequests = db.friendRequest().getAllFriendRequestsSender(loggedUser.getUid());
        for (FriendRequest request : friendRequests) {
            if (request.getSenderId() == loggedUser.getUid() && request.getReceiverId() == user.getUid() && !Objects.equals(request.getStatus(), "rejected")) {
                db.friendRequest().delete(request);
                showToast("Solicitação de amizade excluida com sucesso");
                return;
            }
        }

        showToast("Solicitação de amizade não encontrada");
    }

    private boolean validateFriendRequestFields(String friendEmail, String action) {
        if (friendEmail.isEmpty()) {
            showToast("O email é obrigatório");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(friendEmail).matches()) {
            showToast("Digite um email válido");
            return false;
        }
        if (loggedUser != null && Objects.equals(loggedUser.getEmail(), friendEmail)) {
            showToast("Este email é igual ao seu email de usuário");
            return false;
        }
        User user = db.userModel().findByEmail(friendEmail);
        if (user == null) {
            showToast("Não existe um usuário com esse email");
            return false;
        }
        if (Objects.equals(action, "sendRequest")) {
            List<FriendRequest> friendRequests = db.friendRequest().getAllFriendRequestsSender(loggedUser.getUid());
            for (FriendRequest request : friendRequests) {
                if (request.getReceiverId() == user.getUid() && !Objects.equals(request.getStatus(), "rejected")) {
                    showToast("Você já enviou uma solicitação para este usuário");
                    return false;
                }
            }
        } else if (Objects.equals(action, "removeRequest")) {
            List<FriendRequest> friendRequests = db.friendRequest().getAllFriendRequestsSender(loggedUser.getUid());
            for (FriendRequest request : friendRequests) {
                if (request.getReceiverId() == user.getUid() && !Objects.equals(request.getStatus(), "rejected")) {
                    return true;
                }
                showToast("Solicitação de amizade não encontrada");
                return false;
            }
        }
        return true;
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
