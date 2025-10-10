package setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.Constants;
import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.Statistics;
import com.example.project.User_Guide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.project.ActivityLogin;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import overspeed.Overspeed;

// แนะนำให้เปลี่ยนชื่อไฟล์และชื่อคลาสเป็น Setting (ตัว S พิมพ์ใหญ่)
public class setting extends AppCompatActivity {

    private Button help_and_support, user,logoutButton;
    private BottomNavigationView bottomNavigationView;
    private ProgressBar Progress;

    // ประกาศตัวแปรสำหรับ RadioGroup และ RadioButton
    private RadioGroup radioGroupSpeedUnit;
    private RadioButton radioKmh, radioMph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_setting);

        // --- จัดการ Window Insets ส่วนบน ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        // --- จัดการ marginBottom ของ BottomNavigationView ---
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNavigationView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.bottomMargin = systemBars.bottom;
            v.setLayoutParams(params);
            return insets;
        });

        // --- เชื่อมโยง View อื่นๆ ---
        Progress = findViewById(R.id.progressBar5);
        Progress.setVisibility(View.GONE);

        help_and_support = findViewById(R.id.button_help_support);
        help_and_support.setOnClickListener(v -> {
            Intent help = new Intent(setting.this, Help_support.class);
            startActivity(help);
        });
        user = findViewById(R.id.button_user);
        user.setOnClickListener(v -> {
            Intent user_set = new Intent(setting.this, Setting_User.class);
            startActivity(user_set);
        });

        logoutButton = findViewById(R.id.button_logout);
        logoutButton.setOnClickListener(v -> {
            // สั่งให้ Firebase Sign Out
            FirebaseAuth.getInstance().signOut();

            // สร้าง Intent เพื่อกลับไปหน้า Login
            Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);

            // ตั้งค่า Flags เพื่อล้างหน้าเก่าๆ ออกทั้งหมด (ป้องกันการกด back กลับมา)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish(); // ปิดหน้าปัจจุบัน
            Toast.makeText(setting.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        });

        // --- ตั้งค่า Bottom Navigation ---
        setupBottomNavigation();

        // --- เชื่อมโยงและตั้งค่าการเลือกหน่วยวัดความเร็ว ---
        radioGroupSpeedUnit = findViewById(R.id.radioGroup_speed_unit);
        radioKmh = findViewById(R.id.radio_kmh);
        radioMph = findViewById(R.id.radio_mph);

        loadAndUpdateSpeedUnitSetting(); // เรียกเมธอดเพื่อโหลดค่าเก่า
        setupSpeedUnitListener();      // เรียกเมธอดเพื่อรอรับการกดเลือกใหม่
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_setting);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_main:
                    Progress.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
                        runOnUiThread(() -> {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        });
                    }).start();
                    return true;
                case R.id.bottom_speed:
                    Progress.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
                        runOnUiThread(() -> {
                            startActivity(new Intent(getApplicationContext(), Overspeed.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        });
                    }).start();
                    return true;
                case R.id.bottom_statistics:
                    Progress.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
                        runOnUiThread(() -> {
                            startActivity(new Intent(getApplicationContext(), Statistics.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        });
                    }).start();
                    return true;
                case R.id.bottom_guide:
                    Progress.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
                        runOnUiThread(() -> {
                            startActivity(new Intent(getApplicationContext(), User_Guide.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        });
                    }).start();
                    return true;
                case R.id.bottom_setting:
                    return true; // อยู่หน้าเดิม ไม่ต้องทำอะไร
                default:
                    return false;
            }
        });
    }

    /**
     * โหลดค่าหน่วยความเร็วที่เคยบันทึกไว้และอัปเดตหน้าจอ
     */
    private void loadAndUpdateSpeedUnitSetting() {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
        String savedUnit = prefs.getString(Constants.KEY_SPEED_UNIT, "km/h");

        if (savedUnit.equals("mph")) {
            radioMph.setChecked(true);
        } else {
            radioKmh.setChecked(true);
        }
    }

    /**
     * ตั้งค่า Listener เพื่อรอรับการเปลี่ยนแปลงค่าใน RadioGroup
     */
    private void setupSpeedUnitListener() {
        radioGroupSpeedUnit.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE).edit();

            if (checkedId == R.id.radio_mph) {
                editor.putString(Constants.KEY_SPEED_UNIT, "mph");
            } else {
                editor.putString(Constants.KEY_SPEED_UNIT, "km/h");
            }

            editor.apply(); // บันทึกค่า
        });
    }
}