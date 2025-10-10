package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import overspeed.Overspeed;
import setting.setting;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ProgressBar Progress;
    ImageView logo;
    TextView usernameTextView;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        // --- จัดการ Window Insets ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.MainActivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNavigationView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.bottomMargin = systemBars.bottom;
            v.setLayoutParams(params);
            return insets;
        });

        // --- Initialize Views ---
        usernameTextView = findViewById(R.id.usernameTextView);
        Progress = findViewById(R.id.progressBar);
        logo = findViewById(R.id.imageView4);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Progress.setVisibility(View.GONE);
        logo.setImageResource(R.drawable.logowhite);

        // --- Initialize Firebase ---
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // ถ้ามีผู้ใช้ login อยู่ ให้โหลดชื่อจาก Firestore
            loadUsername(currentUser.getUid());
        } else {
            // ถ้าไม่มี ให้แสดงเป็น Guest หรือกลับไปหน้า Login
            usernameTextView.setText("Guest");
        }

        // --- BottomNavigationView ---
        setupBottomNavigation();
    }

    private void loadUsername(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                if (name != null && !name.isEmpty()) {
                    usernameTextView.setText(name);
                } else {
                    usernameTextView.setText("User");
                }
            } else {
                usernameTextView.setText("Guest");
            }
        }).addOnFailureListener(e -> {
            usernameTextView.setText("Guest");
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.bottom_main);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_main) {
                return true;
            } else if (itemId == R.id.bottom_speed) {
                startActivityWithProgress(Overspeed.class);
                return true;
            } else if (itemId == R.id.bottom_statistics) {
                startActivityWithProgress(Statistics.class);
                return true;
            } else if (itemId == R.id.bottom_guide) {
                startActivityWithProgress(User_Guide.class);
                return true;
            } else if (itemId == R.id.bottom_setting) {
                startActivityWithProgress(setting.class);
                return true;
            }
            return false;
        });
    }

    // Helper method เพื่อลดโค้ดซ้ำซ้อน
    private void startActivityWithProgress(Class<?> activityClass) {
        Progress.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                Progress.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), activityClass));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            });
        }).start();
    }
}