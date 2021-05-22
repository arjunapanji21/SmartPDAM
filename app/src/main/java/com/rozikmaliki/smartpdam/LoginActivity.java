package com.rozikmaliki.smartpdam;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    // variabel
    private EditText email;
    private EditText password;
    private Button login;
    private Button register;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ambil komponen berdasarkan id
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        // inisialisasi firebase auth
        auth = FirebaseAuth.getInstance();

        // set on click listener pada button login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtEmail = email.getText().toString();
                String txtPass = password.getText().toString();
                loginUser(txtEmail, txtPass);
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
                    Toast.makeText(LoginActivity.this, "Email dan password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
                }
                // menampilkan peringatan jika password terlalu pendek
                else if (txtPass.length() < 6){
                    Toast.makeText(LoginActivity.this, "Password tidak boleh kurang dari 6 karakter!", Toast.LENGTH_SHORT).show();
                }
                // melakukan proses registrasi user ke firebase
                else{
                    registerUser(txtEmail, txtPass);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    setProgressBarVisibility(true);
                    Toast.makeText(LoginActivity.this, "Login berhasil!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Login gagal! periksa format email dan password atau user belum ada, silahkan registrasi.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Registrasi berhasil!",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Registrasi gagal! periksa format email dan password atau user sudah ada, silahkan login.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}