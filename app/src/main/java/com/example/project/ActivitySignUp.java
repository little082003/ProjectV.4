package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ActivitySignUp extends AppCompatActivity {
    EditText signupName, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ActivitySignUp), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        // signupUsername = findViewById(R.id.signup_username); // เราจะใช้ Email เป็นหลัก ไม่จำเป็นต้องมี username แยก
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        // --- เริ่มต้น Firebase Auth และ Firestore ---
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(view -> registerUser());

        loginRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(ActivitySignUp.this, ActivityLogin.class);
            startActivity(intent);
        });
    }
    private void registerUser() {
        String name = signupName.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();

        // --- ตรวจสอบข้อมูลเบื้องต้น ---
        if (TextUtils.isEmpty(name)) {
            signupName.setError("Name is required.");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            signupEmail.setError("Email is required.");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            signupPassword.setError("Password must be at least 6 characters.");
            return;
        }

        Toast.makeText(ActivitySignUp.this, "Registering...", Toast.LENGTH_SHORT).show();

        // --- 1. สร้างผู้ใช้ใน FirebaseAuth ---
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // --- 2. บันทึกข้อมูลชื่อเพิ่มเติมใน Firestore ---
                            saveAdditionalUserData(user.getUid(), name, email);
                        }
                    } else {
                        // ถ้าการลงทะเบียนล้มเหลว แสดงข้อความ
                        Toast.makeText(ActivitySignUp.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveAdditionalUserData(String userId, String name, String email) {
        // สร้างข้อมูลที่จะเก็บ
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("profileImageUrl", ""); // ใส่ค่าเริ่มต้นเป็นค่าว่าง

        // บันทึกไปยัง Firestore ใน collection "users" โดยใช้ UID เป็น key
        db.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> {
                    // เมื่อบันทึกสำเร็จ ไปยังหน้า MainActivity
                    Toast.makeText(ActivitySignUp.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    // เคลียร์หน้าก่อนหน้าทั้งหมดออกจาก Stack
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // ถ้าบันทึกข้อมูลเพิ่มเติมไม่สำเร็จ
                    Toast.makeText(ActivitySignUp.this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                });
    }
}