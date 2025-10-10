package com.example.project;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import overspeed.Overspeed;
import setting.setting;

public class User_Guide extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    TextView textViewalert ;
    AlertDialog.Builder alert ;
    ProgressBar Progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_user_guide);
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
        Progress = findViewById(R.id.progressBar4);
        Progress.setVisibility(View.GONE);
        // BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_guide);
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


        alert = new AlertDialog.Builder(this);
        textViewalert = findViewById(R.id.textView2);
        textViewalert.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                alert.setTitle("What Are You Touch???");
                alert.setMessage("Touch >_<");
                alert.setCancelable(true);
                alert.show();
                return false;
            }
        });

    }
}