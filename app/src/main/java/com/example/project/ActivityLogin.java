package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends AppCompatActivity {

    // เราจะใช้ Email ในการ Login เพื่อให้ตรงกับระบบ FirebaseAuth
    EditText loginEmail, loginPassword;
    Button loginButton;
    TextView signupRedirectText;
    ProgressBar loading;

    // --- ส่วนที่เพิ่มเข้ามา ---
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        // --- ตรวจสอบว่าผู้ใช้เคยล็อกอินค้างไว้หรือไม่ ---
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // ถ้าเคยล็อกอินแล้ว ให้ไปหน้า MainActivity เลย
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- ใช้ ID ของ Email แทน Username ใน Layout ของคุณ ---
        // หรือถ้ายังใช้ ID เดิม ให้เปลี่ยนเป็น loginEmail = findViewById(R.id.login_username);
        loginEmail = findViewById(R.id.login_username); // สมมติว่ายังใช้ ID เดิม
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        loading = findViewById(R.id.Loading);

        // --- เริ่มต้น Firebase Auth ---
        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(view -> loginUser());

        signupRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(ActivityLogin.this, ActivitySignUp.class);
            startActivity(intent);
        });
    }

    private void loginUser(){
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("Email cannot be empty");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            loginPassword.setError("Password cannot be empty");
            return;
        }

        loading.setVisibility(View.VISIBLE);

        // --- ใช้ FirebaseAuth เพื่อ Sign In ---
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loading.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(getApplicationContext(), "Login Successful.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        // เคลียร์หน้าก่อนหน้าทั้งหมดออกจาก Stack
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(ActivityLogin.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}