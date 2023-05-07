package com.example.nicode.Account;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.nicode.R;
import com.example.nicode.databinding.ActivityChangePasswordBinding;
import com.example.nicode.databinding.ActivityChangePersonalinfoBinding;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


}