package com.example.nicode.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.nicode.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.Calendar;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton canclebutton, signupbutton;
    private EditText dregis_firstname, dregis_lastname, dregis_username, dregis_phonenumber, dregis_email, dregis_password, dregis_passwordconfirm, dregis_birthdate
            , dregis_address, dregis_rollperday;
    private DatePickerDialog datepicker;
    private RadioGroup dregisGroup_gender;
    private RadioButton dradioButtonregis_gender;
    private ProgressBar progressBar;
    private RoundedImageView dregis_profileicon;
    private String encodedImage;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        preferenceManager = new PreferenceManager(getApplicationContext());

        progressBar = findViewById(R.id.progressBar);

        dregis_profileicon = findViewById(R.id.profileicon);
        dregis_firstname = (EditText) findViewById(R.id.regis_firstname);
        dregis_lastname = (EditText) findViewById(R.id.regis_lastname);
        dregis_username = (EditText) findViewById(R.id.regis_username);
        dregis_phonenumber = (EditText) findViewById(R.id.regis_phonenumber);
        dregis_email = (EditText) findViewById(R.id.regis_email);
        dregis_password = (EditText) findViewById(R.id.regis_password);
        dregis_passwordconfirm = (EditText) findViewById(R.id.regis_passwordconfirm);
        dregis_address = (EditText) findViewById(R.id.regis_address);
        dregis_rollperday = (EditText) findViewById(R.id.regis_rollperday);
        dregisGroup_gender = findViewById(R.id.regis_gender);
        dregisGroup_gender.clearCheck();
        int SelectedGenderID = dregisGroup_gender.getCheckedRadioButtonId();
        dradioButtonregis_gender = findViewById(SelectedGenderID);

        dregis_birthdate = findViewById(R.id.birthdate);
        dregis_birthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                datepicker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dregis_birthdate.setText(dayOfMonth + "/" + month + "/" + year);
                    }
                }, year, month, day);
                datepicker.show();
            }
        });

        canclebutton = findViewById(R.id.cancle_button);
        canclebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        dregis_profileicon = findViewById(R.id.profileicon);
        dregis_profileicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        signupbutton = findViewById(R.id.signup_button);
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidRegisterDetails()){
                    Register();
                }
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

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            dregis_profileicon.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

    );

    private Boolean isValidRegisterDetails() {
        int SelectedGenderID = dregisGroup_gender.getCheckedRadioButtonId();
        dradioButtonregis_gender = findViewById(SelectedGenderID);

        String firstname = dregis_firstname.getText().toString();
        String lastname = dregis_lastname.getText().toString();
        String username = dregis_username.getText().toString();
        String phonenumber = dregis_phonenumber.getText().toString();
        String email = dregis_email.getText().toString().trim();
        String password = dregis_password.getText().toString();
        String confirmpassword = dregis_passwordconfirm.getText().toString();
        String birthdate = dregis_birthdate.getText().toString();
        String address = dregis_address.getText().toString();
        String rollperday = dregis_rollperday.getText().toString();
        String gender;
        String encodeImage;

        if (firstname.isEmpty()) {
            dregis_firstname.setError("โปรดกรอกชื่อจริงของคุณ");
            dregis_firstname.requestFocus();
            return false;
        } else if (lastname.isEmpty()) {
            dregis_lastname.setError("โปรดกรอกนามสกุลของคุณ");
            dregis_lastname.requestFocus();
            return false;
        } else if (encodedImage == null) {
            Toast.makeText(RegisterActivity.this,"โปรดใส่ภาพโปร์ไฟล์ลของคุณ",Toast.LENGTH_LONG).show();
            return false;
        } else if (username.isEmpty()) {
            dregis_username.setError("โปรดกรอกชื่อผู้ใช้ของบัญชีคุณ");
            dregis_username.requestFocus();
            return false;
        } else if (birthdate.isEmpty()) {
            dregis_birthdate.setError("โปรดเลือกวันเกิดของคุณ");
            dregis_birthdate.requestFocus();
            return false;
        } else if (dregisGroup_gender.getCheckedRadioButtonId() == -1) {
            dradioButtonregis_gender.setError("โปรดเลือกเพศของคุณ");
            dradioButtonregis_gender.requestFocus();
            return false;
        } else if (phonenumber.isEmpty()) {
            dregis_phonenumber.setError("โปรดกรอกเบอร์โทรศัพท์ของคุณ");
            dregis_phonenumber.requestFocus();
            return false;
        } else if (email.isEmpty()) {
            dregis_email.setError("โปรดกรอกอีเมลของคุณ");
            dregis_email.requestFocus();
            return false;
        } else if (address.isEmpty()) {
            dregis_address.setError("โปรดกรอกที่อยู่ของคุณ");
            dregis_address.requestFocus();
            return false;
        } else if (rollperday.isEmpty()) {
            dregis_rollperday.setError("โปรดกรอกจำนวนมวนสูบบุหรี่ของคุณ");
            dregis_rollperday.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            dregis_email.setError("โปรดกรอกอีเมลของคุณให้ถูกต้อง");
            dregis_email.requestFocus();
            return false;
        } else if (password.isEmpty()) {
            dregis_password.setError("โปรดกรอกรหัสผ่านของบัญชีคุณ");
            dregis_password.requestFocus();
            return false;
        } else if (password.length() < 6) {
            dregis_password.setError("รหัสผ่านจำเป็นต้องมีอย่างน้อย 6 ตัว");
            dregis_password.requestFocus();
            return false;
        } else if (confirmpassword.isEmpty()) {
            dregis_passwordconfirm.setError("โปรดกรอกรหัสผ่านยืนยัน");
            dregis_passwordconfirm.requestFocus();
            return false;
        } else if (!password.equals(confirmpassword)) {
            dregis_passwordconfirm.setError("รหัสผ่านไม่ตรงกัน");
            dregis_passwordconfirm.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void Register() {
        int rollperday = Integer.parseInt(dregis_rollperday.getText().toString());
        int rollpermonth = rollperday * 31;
        FirebaseFirestore Fdatabase = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_USERNAME, dregis_username.getText().toString());
        user.put(Constants.KEY_FIRSTNAME, dregis_firstname.getText().toString());
        user.put(Constants.KEY_LASTNAME, dregis_lastname.getText().toString());
        user.put(Constants.KEY_PHONENUMBER, dregis_phonenumber.getText().toString());
        user.put(Constants.KEY_GENDER, dradioButtonregis_gender.getText().toString());
        user.put(Constants.KEY_EMAIL, dregis_email.getText().toString());
        user.put(Constants.KEY_PASSWORD, dregis_password.getText().toString());
        user.put(Constants.KEY_BIRTHDATE, dregis_birthdate.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);
        user.put(Constants.KEY_ADDRESS, dregis_address.getText().toString());
        user.put(Constants.KEY_CIGARETTESTOPROLL, 0);
        user.put(Constants.KEY_CIGARETTEROLLPERMONTH, rollpermonth);
        user.put(Constants.KEY_SMOKINGLYZER, 0);
        user.put(Constants.KEY_NICOCOIN, 0);
        user.put(Constants.KEY_DAYFROMSTART, 0);
        user.put(Constants.KEY_ALLDAYFORMONTH, 31);
        user.put(Constants.KEY_STATISTIC_NICOCOIN,0);
        user.put(Constants.KEY_STATISTIC_SMOKINGLYZER,0);
        Fdatabase.collection(Constants.KEY_COLLECTION_USERS)
                     .add(user)
                     .addOnSuccessListener(documentReference -> {
                         preferenceManager.putBoolean(Constants.KEY_IS_LOG_IN,true);
                         preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                         preferenceManager.putString(Constants.KEY_USERNAME,dregis_username.getText().toString());
                         preferenceManager.putString(Constants.KEY_FIRSTNAME,dregis_firstname.getText().toString());
                         preferenceManager.putString(Constants.KEY_LASTNAME,dregis_lastname.getText().toString());
                         preferenceManager.putString(Constants.KEY_EMAIL,dregis_email.getText().toString());
                         preferenceManager.putString(Constants.KEY_PASSWORD,dregis_password.getText().toString());
                         preferenceManager.putString(Constants.KEY_PHONENUMBER,dregis_phonenumber.getText().toString());
                         preferenceManager.putString(Constants.KEY_GENDER,dradioButtonregis_gender.getText().toString());
                         preferenceManager.putString(Constants.KEY_ADDRESS,dregis_address.getText().toString());
                         preferenceManager.putString(Constants.KEY_BIRTHDATE,dregis_birthdate.getText().toString());
                         preferenceManager.putInt(Constants.KEY_CIGARETTESTOPROLL, 0);
                         preferenceManager.putInt(Constants.KEY_CIGARETTEROLLPERMONTH, rollpermonth);
                         preferenceManager.putInt(Constants.KEY_SMOKINGLYZER, 0);
                         preferenceManager.putInt(Constants.KEY_NICOCOIN, 0);
                         preferenceManager.putInt(Constants.KEY_DAYFROMSTART,0);
                         preferenceManager.putInt(Constants.KEY_ALLDAYFORMONTH, 31);

                         preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);
                         Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                     })
                     .addOnFailureListener(exception -> {
                         Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
                     });
                    }


}