package com.example.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import overspeed.Overspeed;
import setting.setting;

public class Statistics extends AppCompatActivity {

    // --- ค่าคงที่ ---
    private static final int MAX_CHART_ENTRIES = 100;

    // --- UI Elements ---
    private BottomNavigationView bottomNavigationView;
    private LineChart speedChart;
    private ProgressBar Progress;
    private TextView overspeedCountTextView; // << แก้ไข: ประกาศตัวแปร TextView

    // --- Firebase ---
    private DatabaseReference hb100Ref;
    private ValueEventListener hb100Listener;
    private DatabaseReference overspeedRef;     // << เพิ่ม: Reference สำหรับดึงจำนวน Overspeed
    private ValueEventListener overspeedListener; // << เพิ่ม: Listener สำหรับดึงจำนวน Overspeed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_statistics);

        // --- จัดการ Window Insets ---
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

        // --- เชื่อมโยง View ---
        speedChart = findViewById(R.id.speedChart);
        Progress = findViewById(R.id.progressBar3);
        overspeedCountTextView = findViewById(R.id.cntoverspeed); // << แก้ไข: เชื่อมโยง TextView ให้ถูกต้อง
        Progress.setVisibility(View.GONE);

        // --- ตั้งค่า ---
        setupBottomNavigation();
        setupChart();
        startFetchingData();
        startFetchingOverspeedCount(); // << เพิ่ม: เรียกใช้เมธอดเพื่อดึงข้อมูล
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_statistics);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_main:
                    startActivityWithProgress(MainActivity.class);
                    return true;
                case R.id.bottom_speed:
                    startActivityWithProgress(Overspeed.class);
                    return true;
                case R.id.bottom_statistics:
                    return true;
                case R.id.bottom_guide:
                    startActivityWithProgress(User_Guide.class);
                    return true;
                case R.id.bottom_setting:
                    startActivityWithProgress(setting.class);
                    return true;
                default:
                    return false;
            }
        });
    }

    private void setupChart() {
        speedChart.getDescription().setEnabled(false);
        speedChart.setTouchEnabled(true);
        speedChart.setDragEnabled(true);
        speedChart.setScaleEnabled(true);
        speedChart.setPinchZoom(true);
        speedChart.getAxisLeft().setAxisMinimum(0f);
        speedChart.getAxisRight().setEnabled(false);

        XAxis xAxis = speedChart.getXAxis();
        xAxis.setValueFormatter(new TimeAxisValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setGranularity(1f);
        xAxis.setLabelRotationAngle(-45);

        LineDataSet dataSet = new LineDataSet(null, "Speed");
        dataSet.setColor(ContextCompat.getColor(this, R.color.purple_500));
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.purple_500));
        dataSet.setDrawCircles(true);
        dataSet.setLineWidth(2f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        speedChart.setData(lineData);
    }

    private void startFetchingData() {
        hb100Ref = FirebaseDatabase.getInstance().getReference("HB100");
        hb100Listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double speedKmh = snapshot.child("Speed_kmh").getValue(Double.class);

                    if (speedKmh != null) {
                        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                        String unit = prefs.getString(Constants.KEY_SPEED_UNIT, "km/h");
                        double displaySpeed;
                        String unitLabel;

                        if (unit.equals("mph")) {
                            displaySpeed = speedKmh * 0.621371;
                            unitLabel = "mph";
                        } else {
                            displaySpeed = speedKmh;
                            unitLabel = "km/h";
                        }

                        LineData data = speedChart.getData();
                        if (data != null) {
                            ILineDataSet dataSet = data.getDataSetByIndex(0);
                            if (dataSet != null) {
                                dataSet.setLabel("Speed (" + unitLabel + ")");
                            }
                        }

                        long timestamp = System.currentTimeMillis();
                        Entry newEntry = new Entry(timestamp, (float) displaySpeed);
                        updateChart(newEntry);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        };
        hb100Ref.addValueEventListener(hb100Listener);
    }

    /**
     * << เพิ่ม: เมธอดใหม่ >>
     * ดึงข้อมูลจำนวนครั้งที่ความเร็วเกินกำหนดจาก Firebase Realtime Database
     * สมมติว่าข้อมูลถูกเก็บไว้ที่ path "Overspeed/count"
     */
    private void startFetchingOverspeedCount() {
        overspeedRef = FirebaseDatabase.getInstance().getReference("Overspeed/count");
        overspeedListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer count = snapshot.getValue(Integer.class);
                    if (count != null) {
                        overspeedCountTextView.setText(String.valueOf(count));
                    } else {
                        overspeedCountTextView.setText("0");
                    }
                } else {
                    overspeedCountTextView.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                overspeedCountTextView.setText("-"); // แสดงสัญลักษณ์เมื่อมีข้อผิดพลาด
            }
        };
        overspeedRef.addValueEventListener(overspeedListener);
    }


    private void updateChart(Entry newEntry) {
        LineData data = speedChart.getData();
        if (data != null) {
            ILineDataSet dataSet = data.getDataSetByIndex(0);
            if (dataSet == null) {
                dataSet = new LineDataSet(null, "Speed");
                data.addDataSet(dataSet);
            }

            if (dataSet.getEntryCount() > MAX_CHART_ENTRIES) {
                dataSet.removeFirst();
            }

            data.addEntry(newEntry, 0);

            data.notifyDataChanged();
            speedChart.notifyDataSetChanged();
            speedChart.moveViewToX(newEntry.getX());
            speedChart.invalidate();
        }
    }

    // Helper method เพื่อลดโค้ดซ้ำซ้อนใน Bottom Navigation
    private void startActivityWithProgress(Class<?> activityClass) {
        Progress.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hb100Ref != null && hb100Listener != null) {
            hb100Ref.removeEventListener(hb100Listener);
        }
        // << เพิ่ม: ลบ listener ของ overspeed count เพื่อป้องกัน memory leak >>
        if (overspeedRef != null && overspeedListener != null) {
            overspeedRef.removeEventListener(overspeedListener);
        }
    }
}