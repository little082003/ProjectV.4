package overspeed;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager; // ต้องมี
import androidx.recyclerview.widget.RecyclerView; // ต้องมี

import com.example.project.MainActivity;
import com.example.project.R;
import com.example.project.Statistics;
import com.example.project.User_Guide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener; // ต้องมี
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
// ไม่จำเป็นต้องใช้ ValueEventListener แล้ว

import java.util.ArrayList; // ต้องมี
import java.util.List; // ต้องมี

import setting.setting;

public class Overspeed extends AppCompatActivity {

    ProgressBar Progress;
    BottomNavigationView bottomNavigationView;

    // ตัวแปรสำหรับ Notification
    private static final String CHANNEL_ID = "Overspeed_Alert_Channel";
    private NotificationManagerCompat notificationManager;

    // ตัวแปรสำหรับ Firebase และ Log
    private DatabaseReference databaseReference;
    // เปลี่ยนมาใช้ ChildEventListener แทน ValueEventListener
    private ChildEventListener speedChildListener;

    // ตัวแปรสำหรับแสดงรายการ Log (UI)
    private RecyclerView overspeedRecyclerView; // ต้องมี
    private OverspeedAdapter adapter; // ต้องมี (มาจาก OverspeedAdapter.java ที่คุณสร้าง)
    private List<OverspeedLog> logList; // ต้องมี

    // ลบ Button button_Home; และ TextView statusTextView; ที่ทำให้เกิด Error ออกไป

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_overspeed);

        // **1. ตั้งค่า Notification Channel**
        notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();

        // **2. ตั้งค่า RecyclerView**
        // ลบบรรทัดที่เรียกใช้ statusTextView ออกไป
        overspeedRecyclerView = findViewById(R.id.overspeedRecyclerView); // ต้องมี ID นี้ใน XML
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); // ให้รายการใหม่แสดงอยู่ด้านบนสุด
        layoutManager.setStackFromEnd(true);
        overspeedRecyclerView.setLayoutManager(layoutManager);

        logList = new ArrayList<>();
        adapter = new OverspeedAdapter(logList);
        overspeedRecyclerView.setAdapter(adapter);

        // **3. ตั้งค่า Firebase ให้ชี้ไปที่ Log_Overspeed**
        databaseReference = FirebaseDatabase.getInstance().getReference("Log_Overspeed");
        startSpeedMonitoring();

        // โค้ดจัดการ Insets และ BottomNavigationView (เหมือนเดิม)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottomNavigationView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            layoutParams.bottomMargin = systemBars.bottom;
            v.setLayoutParams(layoutParams);
            return insets;
        });

        Progress = findViewById(R.id.progressBar2);
        Progress.setVisibility(View.GONE);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.bottom_speed);
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

                    return true;

                // --- ส่วนที่แก้ไข ---
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "แจ้งเตือนความเร็วเกินกำหนด";
            String description = "ช่องสำหรับแจ้งเตือนเมื่อรถขับเกินความเร็วที่ตั้งไว้";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void startSpeedMonitoring() {
        speedChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // เหตุการณ์นี้จะทำงานเมื่อมี Log ใหม่ถูกเพิ่มเข้ามา (จาก SpeedSimulator)
                OverspeedLog log = snapshot.getValue(OverspeedLog.class);

                if (log != null) {
                    // 1. เพิ่มรายการใหม่ลงใน List ที่ตำแหน่งแรกสุด (ล่าสุดอยู่บนสุด)
                    logList.add(0, log);

                    // 2. อัปเดต RecyclerView
                    adapter.notifyItemInserted(0);
                    overspeedRecyclerView.scrollToPosition(0);

                    // 3. สร้าง System Notification สำหรับเหตุการณ์ใหม่นี้
                    sendOverspeedNotification(log.getCarID(), log.getSpeed());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "ChildEventListener cancelled: " + error.getMessage());
            }
        };

        databaseReference.addChildEventListener(speedChildListener);
    }

    private void sendOverspeedNotification(String carID, double speed) {

        Intent intent = new Intent(this, Overspeed.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_overspeed_notification)
                .setContentTitle("🚨 รถคันที่ " + carID + " ขับเร็วเกินกำหนด!")
                .setContentText("ความเร็ว: " + String.format("%.2f", speed) + " km/h")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // ใช้ ID ที่ไม่ซ้ำกัน (Timestamp) เพื่อให้ทุกการแจ้งเตือนเด้งขึ้นมาใหม่
        int dynamicID = (int) System.currentTimeMillis() % 10000;
        notificationManager.notify(dynamicID, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // เปลี่ยนมา removeEventListener ของ ChildEventListener
        if (databaseReference != null && speedChildListener != null) {
            databaseReference.removeEventListener(speedChildListener);
        }
    }
}