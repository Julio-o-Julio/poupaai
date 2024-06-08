package com.example.poupaai.views.initial;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.poupaai.R;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentFormLoginBinding;
import com.example.poupaai.entities.User;
import com.example.poupaai.views.MainActivity;

import org.mindrot.jbcrypt.BCrypt;

public class FormLogin extends Fragment {

    private FragmentFormLoginBinding binding;
    private LocalDatabase db;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFormLoginBinding.inflate(inflater, container, false);

        db = LocalDatabase.getDatabase(requireContext());

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textToRegister.setOnClickListener(v ->
                NavHostFragment.findNavController(FormLogin.this)
                        .navigate(R.id.action_FormLogin_to_FormRegister)
        );

        binding.btnToEnter.setOnClickListener(this::loginUser);
    }

    private void loginUser(View view) {
        String email = binding.edtEmail.getText().toString();
        String password = binding.edtPassword.getText().toString();

        if(email.equals("")) {
            Toast.makeText(requireContext(), "O email é obrigatório", Toast.LENGTH_SHORT).show();
            return;
        }
        if(password.equals("")) {
            Toast.makeText(requireContext(), "A senha é obrigatória", Toast.LENGTH_SHORT).show();
            return;
        }

        User dbUser = db.userModel().findByEmail(email);
        if (dbUser != null) {
            String passHash = dbUser.getPasswordHash();
            if (BCrypt.checkpw(password, passHash)) {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.putExtra("user", dbUser);
                startActivity(intent);
                getActivity().finish();
                return;
            }
        }

        Toast.makeText(requireContext(), "Email ou senha inválidos", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}