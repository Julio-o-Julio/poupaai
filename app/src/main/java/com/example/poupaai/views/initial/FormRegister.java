package com.example.poupaai.views.initial;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
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

import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;

public class FormRegister extends Fragment {

    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private FragmentFormRegisterBinding binding;
    private LocalDatabase db;
    private Uri imageUri;
    private Uri photoUri;

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
        binding.btnChooseGalery.setOnClickListener(this::chooseImageFromGallery);
        binding.btnTakePicture.setOnClickListener(this::takePictureFromCamera);
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

        if (imageUri != null) {
            newUser.setProfileImagePath(imageUri.getPath());
        }
        if (photoUri != null) {
            newUser.setProfileImagePath(photoUri.toString());
        }

        db.userModel().insert(newUser);
        Toast.makeText(requireContext(), "Usuário " + newUser.getUsername() + " cadastrado com sucesso",
                Toast.LENGTH_SHORT).show();

        NavHostFragment.findNavController(FormRegister.this)
                .navigate(R.id.action_FormRegister_to_FormLogin);
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void chooseImageFromGallery(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
    }

    public void takePictureFromCamera(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                if (imageUri != null) {
                    binding.imgProfile.setImageURI(imageUri);
                }
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    photoUri = getImageUri(requireContext(), photo);
                    binding.imgProfile.setImageBitmap(photo);
                }
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
