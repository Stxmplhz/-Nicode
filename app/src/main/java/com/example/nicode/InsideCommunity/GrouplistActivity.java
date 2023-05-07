package com.example.nicode.InsideCommunity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.nicode.R;

public class GrouplistActivity extends AppCompatActivity {

    ImageView chatbtn,imageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grouplist);

        imageBack = findViewById(R.id.image_Back);
        imageBack.setOnClickListener(v -> onBackPressed());

        chatbtn = findViewById(R.id.gotochat);
        chatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RecentChatActivity.class);
                startActivity(intent);
            }
        });
    }
}