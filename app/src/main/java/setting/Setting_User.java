package setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.Statistics;
import com.example.project.User_Guide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import overspeed.Overspeed;

public class Setting_User extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private EditText editName;
    private Button saveButton;
    private ImageView profileImageView;
    private ProgressBar progress;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseUser currentUser;
    private String currentUserId;

    // สำหรับจัดการการเลือกรูปภาพ
    private Uri imageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_setting_user);

        // --- Initialize Firebase ---
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        currentUserId = currentUser.getUid();

        // --- Initialize Views ---
        editName = findViewById(R.id.editName);
        saveButton = findViewById(R.id.saveButton);
        profileImageView = findViewById(R.id.imageView3);
        progress = findViewById(R.id.progressBar7);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        progress.setVisibility(View.GONE);

        // --- โหลดข้อมูลโปรไฟล์ผู้ใช้มาแสดง ---
        loadUserProfile();

        // --- ตั้งค่า ActivityResultLauncher สำหรับการเลือกรูปจากแกลเลอรี ---
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        // แสดงรูปที่เลือกใน ImageView
                        Glide.with(this).load(imageUri).circleCrop().into(profileImageView);
                    }
                }
        );

        // --- ทำให้ ImageView กดเพื่อเลือกรูปได้ ---
        profileImageView.setOnClickListener(v -> openGallery());

        // --- ทำให้ปุ่ม Save ทำงาน ---
        saveButton.setOnClickListener(v -> saveUserProfile());

        // --- จัดการ Window Insets ---
        setupWindowInsets();

        // --- BottomNavigationView ---
        setupBottomNavigation();
    }

    private void loadUserProfile() {
        progress.setVisibility(View.VISIBLE);
        DocumentReference userRef = db.collection("users").document(currentUserId);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String profileImageUrl = documentSnapshot.getString("profileImageUrl");

                editName.setText(name);

                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(profileImageUrl)
                            .circleCrop()
                            .placeholder(R.drawable.baseline_account_circle_24)
                            .into(profileImageView);
                }
            }
            progress.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            progress.setVisibility(View.GONE);
            Toast.makeText(Setting_User.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private void saveUserProfile() {
        String newName = editName.getText().toString().trim();

        if (newName.isEmpty()) {
            editName.setError("Name cannot be empty");
            editName.requestFocus();
            return;
        }

        progress.setVisibility(View.VISIBLE);

        if (imageUri != null) {
            StorageReference fileRef = storage.getReference().child("profile_images").child(currentUserId + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        updateFirestore(newName, downloadUrl);
                    }))
                    .addOnFailureListener(e -> {
                        progress.setVisibility(View.GONE);
                        Toast.makeText(Setting_User.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            updateFirestore(newName, null);
        }
    }

    private void updateFirestore(String name, String imageUrl) {
        DocumentReference userRef = db.collection("users").document(currentUserId);
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);

        if (imageUrl != null) {
            userData.put("profileImageUrl", imageUrl);
        }

        userRef.set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(Setting_User.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    imageUri = null;
                })
                .addOnFailureListener(e -> {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(Setting_User.this, "Failed to update profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
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
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_main) {
                startActivityWithProgress(MainActivity.class);
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

    private void startActivityWithProgress(Class<?> activityClass) {
        progress.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                progress.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), activityClass));
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            });
        }).start();
    }
}