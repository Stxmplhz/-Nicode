package com.example.nicode.Donate;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nicode.Activity.Constants;
import com.example.nicode.Activity.PreferenceManager;
import com.example.nicode.databinding.DonateExchangeBinding;
import com.example.nicode.databinding.GiftExchangeBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class DonationExchangeActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;

    private DonateExchangeBinding binding;

    private FirebaseFirestore Fdatabase;

    int Nicocoin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DonateExchangeBinding.inflate(getLayoutInflater());
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
                if (Nicocoin >= 200) {
                    nicoReference.update(Constants.KEY_NICOCOIN, FieldValue.increment(-200));
                    ExchangeReward();
                }{
                    Toast.makeText(DonationExchangeActivity.this,"เหรียญของคุณไม่เพียงพอ",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void ExchangeReward(){
        HashMap<String, Object> usergiftlist = new HashMap<>();
        usergiftlist.put(Constants.KEY_USERNAME, Constants.KEY_USERNAME);
        usergiftlist.put(Constants.KEY_FIRSTNAME, Constants.KEY_FIRSTNAME);
        usergiftlist.put(Constants.KEY_LASTNAME, Constants.KEY_LASTNAME);
        usergiftlist.put(Constants.KEY_PHONENUMBER, Constants.KEY_PHONENUMBER);
        usergiftlist.put(Constants.KEY_EMAIL, Constants.KEY_EMAIL);
        usergiftlist.put(Constants.KEY_ADDRESS, Constants.KEY_ADDRESS);
        usergiftlist.put(Constants.KEY_GIFT, "donationvoucher1");
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








