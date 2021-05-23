package com.rozikmaliki.smartpdam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class RegistrasiActivity extends AppCompatActivity {

    // variabel
    private EditText nama;
    private EditText email;
    private EditText password;
    private Button login;
    private Button register;
    private ImageButton pwVisibilitiy;
    private FirebaseAuth auth;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference root =  database.getReference();

    private String userID, bulan;

    boolean passwordVisibility = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);

        // ambil komponen berdasarkan id
        nama = findViewById(R.id.nama);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
        pwVisibilitiy = findViewById(R.id.pwVisibility);

        // inisialisasi firebase auth
        auth = FirebaseAuth.getInstance();

        // set on click listener untuk melihat password;
        pwVisibilitiy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordVisibility == false){
                    pwVisibilitiy.setImageResource(R.drawable.ic_visibility);
                    passwordVisibility = true;
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    pwVisibilitiy.setImageResource(R.drawable.ic_visibility_off);
                    passwordVisibility = false;
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        // set on click listener pada button login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrasiActivity.this, LoginActivity.class));
                finish();
            }
        });

        // set on click listener pada button register
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail = email.getText().toString();
                String txtPass = password.getText().toString();

                // menampilkan peringatan jika kolom email dan password kosong
                if(TextUtils.isEmpty(txtEmail) || TextUtils.isEmpty(txtPass)){
                    Toast.makeText(RegistrasiActivity.this, "Email dan password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                }
                // menampilkan peringatan jika password terlalu pendek
                else if (txtPass.length() < 6){
                    Toast.makeText(RegistrasiActivity.this, "Password tidak boleh kurang dari 6 karakter!", Toast.LENGTH_SHORT).show();
                }
                // melakukan proses registrasi user ke firebase
                else{
                    registerUser(txtEmail, txtPass);
                }
            }
        });
    }

    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrasiActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String txtNama = nama.getText().toString();

                    userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    for (int i = 1; i<=12;i++){
                        switch (i){
                            case 1:
                                bulan = "Januari";
                                break;
                            case 2:
                                bulan = "Februari";
                                break;
                            case 3:
                                bulan = "Maret";
                                break;
                            case 4:
                                bulan = "April";
                                break;
                            case 5:
                                bulan = "Mei";
                                break;
                            case 6:
                                bulan = "Juni";
                                break;
                            case 7:
                                bulan = "Juli";
                                break;
                            case 8:
                                bulan = "Agustus";
                                break;
                            case 9:
                                bulan = "September";
                                break;
                            case 10:
                                bulan = "Oktober";
                                break;
                            case 11:
                                bulan = "November";
                                break;
                            case 12:
                                bulan = "Desember";
                                break;
                        }
                        root.child("users").child(userID).child("data").child(String.valueOf(i)).child("bulan").setValue(bulan);
                        root.child("users").child(userID).child("data").child(String.valueOf(i)).child("air").setValue("0");
                        root.child("users").child(userID).child("data").child(String.valueOf(i)).child("biaya").setValue("0");

                        root.child("users").child(userID).child("chart").child(String.valueOf(i)).child("x").setValue(String.valueOf(i));
                        root.child("users").child(userID).child("chart").child(String.valueOf(i)).child("y").setValue("0");
                    }
                    root.child("users").child(userID).child("nama").setValue(txtNama);
                    Toast.makeText(RegistrasiActivity.this, "Registrasi berhasil!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegistrasiActivity.this, MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(RegistrasiActivity.this, "Registrasi gagal! periksa format email dan password atau user sudah ada, silahkan login.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}