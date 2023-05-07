package com.example.nicode.Fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nicode.Account.ChangePasswordActivity;
import com.example.nicode.Account.ChangePersonalinfoActivity;
import com.example.nicode.Activity.Constants;
import com.example.nicode.Activity.LoginActivity;
import com.example.nicode.Activity.PreferenceManager;
import com.example.nicode.InsideCommunity.RecentChatActivity;
import com.example.nicode.R;
import com.example.nicode.databinding.FragmentAccountBinding;
import com.example.nicode.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment  {

    private FragmentAccountBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore Fdatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Fdatabase = FirebaseFirestore.getInstance();

        Context thiscontext = container.getContext();

        preferenceManager = new PreferenceManager(thiscontext);

        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.profileImage.setImageBitmap(bitmap);

        String Name = preferenceManager.getString(Constants.KEY_FIRSTNAME)+" " + preferenceManager.getString(Constants.KEY_LASTNAME);
        binding.textName.setText(Name);
        binding.textUsername.setText(preferenceManager.getString(Constants.KEY_USERNAME));
        binding.textAddress.setText(preferenceManager.getString(Constants.KEY_ADDRESS));
        binding.textGender.setText(preferenceManager.getString(Constants.KEY_GENDER));
        binding.textEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        binding.textBirthdate.setText(preferenceManager.getString(Constants.KEY_BIRTHDATE));
        binding.textPhonenumber.setText(preferenceManager.getString(Constants.KEY_PHONENUMBER));

        binding.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePersonalinfoActivity.class);
                startActivity(intent);
            }
        });

        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  binding = null;
            }
        });

        binding.LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Toast.makeText(thiscontext, "กำลังออกจากระบบ...", Toast.LENGTH_LONG).show();
                 FirebaseFirestore Fdatabase = FirebaseFirestore.getInstance();
                 DocumentReference documentReference =
                     Fdatabase.collection(Constants.KEY_COLLECTION_USERS).document(
                          preferenceManager.getString(Constants.KEY_USER_ID)
                     );
                     HashMap<String, Object> updates = new HashMap<>();
                     updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
                     documentReference.update(updates).addOnSuccessListener(unused -> {
                          preferenceManager.clear();
                           Intent intent = new Intent(getActivity(), LoginActivity.class);
                           startActivity(intent);
                     }).addOnFailureListener(e -> {
                          Toast.makeText(thiscontext, "ไม่สามารถออกจากระบบได้", Toast.LENGTH_LONG).show();
                     });
                    }
                });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}