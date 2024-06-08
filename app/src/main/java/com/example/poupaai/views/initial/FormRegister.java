package com.example.poupaai.views.initial;

import android.content.Intent;
import android.os.Bundle;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.poupaai.R;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentFormRegisterBinding;
import com.example.poupaai.entities.User;
import com.example.poupaai.views.MainActivity;

import org.mindrot.jbcrypt.BCrypt;

public class FormRegister extends Fragment {

    private FragmentFormRegisterBinding binding;
    private LocalDatabase db;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFormRegisterBinding.inflate(inflater, container, false);

        db = LocalDatabase.getDatabase(requireContext());

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textToLogin.setOnClickListener(v ->
                NavHostFragment.findNavController(FormRegister.this)
                        .navigate(R.id.action_FormRegister_to_FormLogin)
        );

        binding.btnRegister.setOnClickListener(this::registerUser);
    }

    private void registerUser(View view) {
        String name = binding.edtName.getText().toString();
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();

        if(name.isEmpty()){
            Toast.makeText(requireContext(), "O nome é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }
        if(email.isEmpty()) {
            Toast.makeText(requireContext(), "O email é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.isEmpty()) {
            Toast.makeText(requireContext(), "A senha é obrigatória", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Digite um email válido", Toast.LENGTH_SHORT).show();
            return;
        }

        User dbUser = db.userModel().findByEmail(email);
        if (dbUser != null) {
            Toast.makeText(requireContext(), "Este e-mail já está em uso", Toast.LENGTH_SHORT).show();
            return;
        }

        String passwordHash = hashPassword(password);

        User newUser = new User();
        newUser.setUsername(name);
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordHash);

        db.userModel().insert(newUser);
        Toast.makeText(requireContext(), "Usuário " + newUser.getUsername() + " cadastrado com sucesso",
                Toast.LENGTH_SHORT).show();

        NavHostFragment.findNavController(FormRegister.this)
                .navigate(R.id.action_FormRegister_to_FormLogin);
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