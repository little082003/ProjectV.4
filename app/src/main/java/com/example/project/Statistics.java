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
import com.github.mikephil.charting.components.XAxis; // เพิ่ม import นี้
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
    private TextView speedText, motionText, freqercyText;
    private LineChart speedChart;
    private ProgressBar Progress;

    // --- Firebase ---
    private DatabaseReference hb100Ref;
    private ValueEventListener hb100Listener;

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
        speedText = findViewById(R.id.speed);
        motionText = findViewById(R.id.motion);
        freqercyText = findViewById(R.id.frequency);
        speedChart = findViewById(R.id.speedChart);
        Progress = findViewById(R.id.progressBar3);
        Progress.setVisibility(View.GONE);

        // --- ตั้งค่า ---
        setupBottomNavigation();
        setupChart();
        startFetchingData();
    }

    private void setupBottomNavigation() {
        // ... โค้ด BottomNavigationView แบบดั้งเดิม ...
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_statistics);
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
                // ... case อื่นๆ ...
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
                    Progress.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try { Thread.sleep(1000); } catch (Exception e) { e.printStackTrace(); }
                        runOnUiThread(() -> {
                            startActivity(new Intent(getApplicationContext(), setting.class));
                            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            finish();
                        });
                    }).start();
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

        // --- ส่วนที่แก้ไข: ตั้งค่าแกน X ให้แสดงเป็นเวลา ---
        XAxis xAxis = speedChart.getXAxis();
        xAxis.setValueFormatter(new TimeAxisValueFormatter()); // ใช้ Formatter ที่เราสร้าง
        xAxis.setPosition(XAxis.XAxisPosition.TOP);           // ย้ายป้ายกำกับไปไว้ข้างบน
        xAxis.setGranularity(1f);                               // ป้องกันป้ายกำกับซ้อนกัน
        xAxis.setLabelRotationAngle(-45);                       // เอียงป้ายกำกับให้อ่านง่าย

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
                    Double frequency = snapshot.child("Frequency").getValue(Double.class);
                    Boolean motionDetected = snapshot.child("MotionDetected").getValue(Boolean.class);

                    SharedPreferences prefs = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
                    String unit = prefs.getString(Constants.KEY_SPEED_UNIT, "km/h");

                    double displaySpeed = 0.0;
                    String unitLabel = "km/h";

                    if (speedKmh != null) {
                        if (unit.equals("mph")) {
                            displaySpeed = speedKmh * 0.621371;
                            unitLabel = "mph";
                        } else {
                            displaySpeed = speedKmh;
                            unitLabel = "km/h";
                        }
                    }

                    speedText.setText(String.format("%.2f %s", displaySpeed, unitLabel));
                    freqercyText.setText(frequency != null ? String.format("%.2f Hz", frequency) : "N/A");
                    motionText.setText(motionDetected != null && motionDetected ? "Detected" : "No Detected!");

                    if (speedKmh != null) {
                        LineData data = speedChart.getData();
                        if (data != null) {
                            ILineDataSet dataSet = data.getDataSetByIndex(0);
                            if (dataSet != null) {
                                dataSet.setLabel("Speed (" + unitLabel + ")");
                            }
                        }

                        // --- ส่วนที่แก้ไข: สร้าง Entry ด้วยเวลาปัจจุบัน ---
                        long timestamp = System.currentTimeMillis();
                        Entry newEntry = new Entry(timestamp, (float) displaySpeed);
                        updateChart(newEntry); // ส่ง Entry ทั้งก้อนไปอัปเดต
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // ... โค้ด onCancelled ...
            }
        };
        hb100Ref.addValueEventListener(hb100Listener);
    }

    // --- ส่วนที่แก้ไข: รับพารามิเตอร์เป็น Entry ---
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

            // ใช้ newEntry ที่ได้รับมาโดยตรง
            data.addEntry(newEntry, 0);

            data.notifyDataChanged();
            speedChart.notifyDataSetChanged();
            speedChart.moveViewToX(newEntry.getX()); // เลื่อนไปที่เวลาล่าสุด
            speedChart.invalidate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hb100Ref != null && hb100Listener != null) {
            hb100Ref.removeEventListener(hb100Listener);
        }
    }
}