package setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.project.MainActivity;
import overspeed.Overspeed;
import com.example.project.R;
import com.example.project.Statistics;
import com.example.project.User_Guide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Help_support extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    ProgressBar Progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_help_support);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        // จัดการ marginBottom ของ BottomNavigationView
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNavigationView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.bottomMargin = systemBars.bottom;
            v.setLayoutParams(params);
            return insets;
        });


        Progress = findViewById(R.id.progressBar6);
        Progress.setVisibility(View.GONE);

        // BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_main);
        bottomNavigationView.setSelectedItemId(R.id.bottom_speed);
        bottomNavigationView.setSelectedItemId(R.id.bottom_statistics);
        bottomNavigationView.setSelectedItemId(R.id.bottom_main);
        bottomNavigationView.setSelectedItemId(R.id.bottom_speed);
        bottomNavigationView.setSelectedItemId(R.id.bottom_statistics);
        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.bottom_main:
                    Progress.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(() -> {
                            Progress.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        }); // <-- แก้ไข: ปิดวงเล็บของ runOnUiThread
                    }).start(); // <-- แก้ไข: เพิ่ม .start() เพื่อให้ Thread ทำงาน
                    return true; // <-- แก้ไข: เพิ่ม return true สำหรับ case นี้
                case R.id.bottom_speed:
                    Progress.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(() -> {
                            Progress.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), Overspeed.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        });
                    }).start();
                    return true;
                case R.id.bottom_statistics:
                    Progress.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(() -> {
                            Progress.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), Statistics.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        }); // <-- แก้ไข: ปิดวงเล็บของ runOnUiThread
                    }).start(); // <-- แก้ไข: เพิ่ม .start() เพื่อให้ Thread ทำงาน
                    return true; // <-- แก้ไข: เพิ่ม return true สำหรับ case นี้

                case R.id.bottom_guide:
                    Progress.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(() -> {
                            Progress.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), User_Guide.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        }); // <-- แก้ไข: ปิดวงเล็บของ runOnUiThread
                    }).start(); // <-- แก้ไข: เพิ่ม .start() เพื่อให้ Thread ทำงาน
                    return true; // <-- แก้ไข: เพิ่ม return true สำหรับ case นี้

                case R.id.bottom_setting:
                    Progress.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(() -> {
                            Progress.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), setting.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        }); // <-- แก้ไข: ปิดวงเล็บของ runOnUiThread
                    }).start(); // <-- แก้ไข: เพิ่ม .start() เพื่อให้ Thread ทำงาน
                    return true; // <-- แก้ไข: เพิ่ม return true สำหรับ case นี้

                default:
                    return false;
            }
        });
    }
}