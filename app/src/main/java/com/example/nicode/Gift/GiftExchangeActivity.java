package com.example.nicode.Gift;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nicode.Activity.Constants;
import com.example.nicode.Activity.MainActivity;
import com.example.nicode.Activity.PreferenceManager;
import com.example.nicode.InsideCommunity.RecentChatActivity;
import com.example.nicode.InsideCommunity.UsersActivity;
import com.example.nicode.R;
import com.example.nicode.databinding.GiftExchangeBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class GiftExchangeActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;

    private GiftExchangeBinding binding;

    private FirebaseFirestore Fdatabase;

    int Nicocoin,Nicocoin_new;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = GiftExchangeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        Fdatabase = FirebaseFirestore.getInstance();
        DocumentReference nicoReference = Fdatabase.collection("users").document(Constants.KEY_USER_ID);

        binding.imageBack.setOnClickListener(v -> onBackPressed());

        binding.conditionbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textConditiongift.setVisibility(View.VISIBLE);
                binding.conditionbutton.setVisibility(View.GONE);
                binding.detailbutton.setVisibility(View.VISIBLE);
                binding.textDetailgift.setVisibility(View.VISIBLE);
                binding.textDetailgift.setVisibility(View.GONE);
            }
        });

        binding.detailbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textConditiongift.setVisibility(View.GONE);
                binding.conditionbutton.setVisibility(View.VISIBLE);
                binding.detailbutton.setVisibility(View.GONE);
                binding.textDetailgift.setVisibility(View.GONE);
                binding.textDetailgift.setVisibility(View.VISIBLE);
            }
        });

        Nicocoin = preferenceManager.getInt(Constants.KEY_NICOCOIN);

        binding.exchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Nicocoin >= 250) {
                    nicoReference.update(Constants.KEY_NICOCOIN, FieldValue.increment(-250));
                    Nicocoin_new = Nicocoin - 250;
                    preferenceManager.putInt(Constants.KEY_NICOCOIN, Nicocoin_new);
                    ExchangeReward();
                }{
                    Toast.makeText(GiftExchangeActivity.this,"เหรียญของคุณไม่เพียงพอ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void ExchangeReward(){
        HashMap<String, Object> usergiftlist = new HashMap<>();
        usergiftlist.put(Constants.KEY_USERNAME, preferenceManager.getString(Constants.KEY_USERNAME));
        usergiftlist.put(Constants.KEY_FIRSTNAME, preferenceManager.getString(Constants.KEY_FIRSTNAME));
        usergiftlist.put(Constants.KEY_LASTNAME, preferenceManager.getString(Constants.KEY_LASTNAME));
        usergiftlist.put(Constants.KEY_PHONENUMBER, preferenceManager.getString(Constants.KEY_PHONENUMBER));
        usergiftlist.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
        usergiftlist.put(Constants.KEY_ADDRESS, preferenceManager.getString(Constants.KEY_ADDRESS));
        usergiftlist.put(Constants.KEY_GIFT, "giftvoucher1");
        Fdatabase.collection(Constants.KEY_COLLECTION_EXCHANGEDGIFT)
                .add(usergiftlist)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this,"เเลกเปลี่ยนของรางวัลสำเร็จ",Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(this,"เเลกเปลี่ยนของรางวัลไม่สำเร็จ โปรดลองอีกครั้ง",Toast.LENGTH_SHORT).show();
                });
    }
}








