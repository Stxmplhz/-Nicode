package com.example.nicode.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nicode.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

   ImageButton createbtn, loginbtn;
   TextView forgetpassbtn;
   EditText password,email;
   AlertDialog.Builder resetpassword_alert;
   LayoutInflater inflater;
   FirebaseAuth FAuth;
   FirebaseUser user;
   private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        FAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_LOG_IN)) {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }

        forgetpassbtn = findViewById(R.id.forgetpassword);
        email = findViewById(R.id.loginemail);
        password = findViewById(R.id.loginpassword);
        loginbtn = findViewById(R.id.loginbutton);
        createbtn = findViewById(R.id.createbutton);

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (isValidLoginDetails()) {
                    Login();
                }
            }
        });

        createbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
            }
        });

        resetpassword_alert = new AlertDialog.Builder(this);
        inflater = this.getLayoutInflater();

        forgetpassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = inflater.inflate(R.layout.resetpassword_popup,null);
                resetpassword_alert.setTitle("Forget password?")
                        .setMessage("Enter your email to reset password")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                   EditText email = view.findViewById(R.id.email_to_reset);
                                   if(email.getText().toString().isEmpty()){
                                       email.setError("Required Field");
                                       return;
                                   }
                                   FAuth.sendPasswordResetEmail(email.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                       @Override
                                       public void onSuccess(Void aVoid) {
                                           Toast.makeText(LoginActivity.this,"ส่งอีเมลเพื่อเปลี่ยนรหัสเเล้ว โปรดตรวจสอบอีเมลของคุณ",Toast.LENGTH_SHORT).show();
                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                       }
                                   });
                            }
                        }).setNegativeButton("Cancle",null)
                        .setView(view)
                        .create().show();
            }
        });
    }

    private Boolean isValidLoginDetails(){
        if (email.getText().toString().isEmpty()){
            email.setError("โปรดใส่ Email ของบัญชีผู้ใช้คุณ");
            return false;
        } else if (password.getText().toString().isEmpty()){
            password.setError("โปรดใส่รหัสผ่านของบัญชีผู้ใช้คุณ");
            return false;
        } else {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void Login(){
        FirebaseFirestore Fdatabase = FirebaseFirestore.getInstance();
        Fdatabase.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, password.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_LOG_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_EMAIL,documentSnapshot.getString(Constants.KEY_EMAIL));
                        preferenceManager.putString(Constants.KEY_BIRTHDATE,documentSnapshot.getString(Constants.KEY_BIRTHDATE));
                        preferenceManager.putString(Constants.KEY_USERNAME,documentSnapshot.getString(Constants.KEY_USERNAME));
                        preferenceManager.putString(Constants.KEY_IMAGE,documentSnapshot.getString(Constants.KEY_IMAGE));
                        preferenceManager.putString(Constants.KEY_FIRSTNAME,documentSnapshot.getString(Constants.KEY_FIRSTNAME));
                        preferenceManager.putString(Constants.KEY_LASTNAME,documentSnapshot.getString(Constants.KEY_LASTNAME));
                        preferenceManager.putString(Constants.KEY_PHONENUMBER,documentSnapshot.getString(Constants.KEY_PHONENUMBER));
                        preferenceManager.putString(Constants.KEY_GENDER,documentSnapshot.getString(Constants.KEY_GENDER));
                        preferenceManager.putString(Constants.KEY_ADDRESS,documentSnapshot.getString(Constants.KEY_ADDRESS));
                        preferenceManager.putInt(Constants.KEY_CIGARETTESTOPROLL, Math.toIntExact(documentSnapshot.getLong(Constants.KEY_CIGARETTESTOPROLL)));
                        preferenceManager.putInt(Constants.KEY_CIGARETTEROLLPERMONTH,Math.toIntExact(documentSnapshot.getLong(Constants.KEY_CIGARETTEROLLPERMONTH)));
                        preferenceManager.putInt(Constants.KEY_NICOCOIN,Math.toIntExact(documentSnapshot.getLong(Constants.KEY_NICOCOIN)));
                        preferenceManager.putInt(Constants.KEY_DAYFROMSTART,Math.toIntExact(documentSnapshot.getLong(Constants.KEY_DAYFROMSTART)));
                        preferenceManager.putInt(Constants.KEY_ALLDAYFORMONTH,Math.toIntExact(documentSnapshot.getLong(Constants.KEY_ALLDAYFORMONTH)));
                        preferenceManager.putInt(Constants.KEY_SMOKINGLYZER,Math.toIntExact(documentSnapshot.getLong(Constants.KEY_SMOKINGLYZER)));
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this,"คุณยังไม่ได้สมัครบัญชีผู้ใช้",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void  onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }
}
