package com.example.poupaai.views;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.poupaai.R;
import com.example.poupaai.database.LocalDatabase;
import com.example.poupaai.databinding.FragmentMyProfileBinding;
import com.example.poupaai.entities.User;
import com.example.poupaai.views.initial.InitialActivity;

import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;

public class FragmentMyProfile extends Fragment {
    private FragmentMyProfileBinding binding;
    private LocalDatabase db;
    private User loggedUser;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 300;
    private Uri imageUri;
    private Uri photoUri;

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

        binding.btnChooseGalery.setOnClickListener(this::chooseImageFromGallery);
        binding.btnTakePicture.setOnClickListener(this::takePictureFromCamera);
        binding.btnSave.setOnClickListener(this::saveUser);
        binding.btnRemoveAccount.setOnClickListener(this::removeAccount);
    }

    private void saveUser(View view) {
        String name = binding.edtMyName.getText().toString();
        String email = binding.edtMyEmail.getText().toString();
        String password = binding.edtMyPassword.getText().toString();

        if (name.equalsIgnoreCase(loggedUser.getUsername()) &&
                email.equalsIgnoreCase(loggedUser.getEmail()) &&
                password.isEmpty() &&
                imageUri == null &&
                photoUri == null) {

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
        if (imageUri != null) {
            loggedUser.setProfileImagePath(imageUri.getPath());
        } else if (photoUri != null) {
            loggedUser.setProfileImagePath(photoUri.toString());
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

    public void chooseImageFromGallery(View view) {
        if (checkAndRequestPermissions()) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
        }
    }

    public void takePictureFromCamera(View view) {
        if (checkAndRequestPermissions()) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    private boolean checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Permissões concedidas.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Permissões são necessárias para esta funcionalidade.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                if (imageUri != null) {
                    binding.ivImgProfile.setImageURI(imageUri);
                }
            }
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                if (photo != null) {
                    photoUri = getImageUri(requireContext(), photo);
                    binding.ivImgProfile.setImageBitmap(photo);
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
