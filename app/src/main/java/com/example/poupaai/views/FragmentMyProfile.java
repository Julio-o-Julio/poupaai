package com.example.poupaai.views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.poupaai.R;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentMyProfileBinding;
import com.example.poupaai.entities.User;
import com.example.poupaai.views.initial.InitialActivity;

import org.mindrot.jbcrypt.BCrypt;

public class FragmentMyProfile extends Fragment {
    private FragmentMyProfileBinding binding;
    private LocalDatabase db;
    private User loggedUser;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMyProfileBinding.inflate(inflater, container, false);

        db = LocalDatabase.getDatabase(requireContext());

        Bundle arguments = getArguments();
        if (arguments != null) {
            loggedUser = arguments.getParcelable("user");
        }

        if (loggedUser != null) {
            binding.edtMyName.setText(loggedUser.getUsername());
            binding.edtMyEmail.setText(loggedUser.getEmail());

            if (loggedUser.getProfileImagePath() != null) {
                Glide.with(requireContext())
                        .load(Uri.parse(loggedUser.getProfileImagePath()))
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .into(binding.ivImgProfile);
            }
        }

        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSave.setOnClickListener(this::saveUser);

        binding.btnRemoveAccount.setOnClickListener(this::removeAccount);
    }

    private void saveUser(View view) {
        String name = binding.edtMyName.getText().toString();
        String email = binding.edtMyEmail.getText().toString();
        String password = binding.edtMyPassword.getText().toString();

        if (name.equalsIgnoreCase(loggedUser.getUsername()) &&
                email.equalsIgnoreCase(loggedUser.getEmail()) &&
                password.isEmpty()) {

            Toast.makeText(requireContext(), "Nenhum dado foi alterado", Toast.LENGTH_SHORT).show();
            return;
        }

        if(name.isEmpty()){
            Toast.makeText(requireContext(), "O nome é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }
        if(email.isEmpty()) {
            Toast.makeText(requireContext(), "O email é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordHash = hashPassword(password);

        loggedUser.setUsername(name);
        loggedUser.setEmail(email);
        if(!password.isEmpty()) {
            loggedUser.setPasswordHash(passwordHash);
        }

        db.userModel().update(loggedUser);
        Toast.makeText(requireContext(), "Seus dados foram atualizados com sucesso",
                Toast.LENGTH_SHORT).show();
    }

    private void removeAccount(View view) {
        String email = loggedUser.getEmail();

        db.userModel().delete(loggedUser);

        User user = db.userModel().findByEmail(email);

        if(user != null) {
            Toast.makeText(requireContext(), "Erro ao tentar excluir sua conta",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(), "Sua conta foi excluida com sucesso",
                Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(requireContext(), InitialActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
