package com.example.nicode.Account;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nicode.Activity.Constants;
import com.example.nicode.Activity.MainActivity;
import com.example.nicode.Activity.PreferenceManager;
import com.example.nicode.InsideCommunity.ChatMessage;
import com.example.nicode.R;
import com.example.nicode.databinding.ActivityChangePersonalinfoBinding;
import com.example.nicode.databinding.FragmentAccountBinding;
import com.example.nicode.databinding.FragmentRecentchatBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.core.Path;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firestore.v1.Value;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChangePersonalinfoActivity extends AppCompatActivity {

    private ActivityChangePersonalinfoBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore Fdatabase;
    private String encodedImage,address ,username ,phonenumber ,encodeImage,userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePersonalinfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());
        Fdatabase = FirebaseFirestore.getInstance();

        userID = preferenceManager.getString(Constants.KEY_USER_ID);

        DocumentReference UserUpdateRef = Fdatabase.collection(Constants.KEY_COLLECTION_USERS).document(userID);

        Updateprofileimage();

        binding.imageBack.setOnClickListener(v -> onBackPressed());

        binding.ComfirmChangePersonalbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateuser();
            }
        });

        binding.UpdateProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void Updateprofileimage() {
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.UpdateProfileImage.setImageBitmap(bitmap);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.UpdateProfileImage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

    );

    private void updateuser() {
        Fdatabase = FirebaseFirestore.getInstance();
        userID = preferenceManager.getString(Constants.KEY_USER_ID);
        DocumentReference UserUpdateRef = Fdatabase.collection(Constants.KEY_COLLECTION_USERS).document(userID);

        if(binding.UpdateTextUsername.getText().toString().isEmpty()){
            username = preferenceManager.getString(Constants.KEY_USERNAME);
        } else {
            username = binding.UpdateTextUsername.getText().toString();
        }

        if(binding.UpdateTextAddress.getText().toString().isEmpty()){
            address = preferenceManager.getString(Constants.KEY_ADDRESS);
        } else {
            address = binding.UpdateTextAddress.getText().toString();
        }

        if(binding.UpdateTextPhonenumber.getText().toString().isEmpty()){
            phonenumber = preferenceManager.getString(Constants.KEY_PHONENUMBER);
        } else {
            phonenumber = binding.UpdateTextPhonenumber.getText().toString();
        }

        if(encodedImage == null){
            encodedImage = preferenceManager.getString(Constants.KEY_IMAGE);
        } else {
        }

        UserUpdateRef.collection(Constants.KEY_COLLECTION_USERS).document(userID)
                .update(
                        Constants.KEY_USERNAME, username,
                        Constants.KEY_PHONENUMBER, phonenumber,
                        Constants.KEY_ADDRESS, address,
                        Constants.KEY_IMAGE, encodedImage
                );

        preferenceManager.putString(Constants.KEY_USERNAME,username);
        preferenceManager.putString(Constants.KEY_PHONENUMBER,phonenumber);
        preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
        preferenceManager.putString(Constants.KEY_ADDRESS,address);
        Toast.makeText(ChangePersonalinfoActivity.this,"เเก้ไขข้อมูลส่วนตัวสำเร็จ",Toast.LENGTH_SHORT).show();
    }
}