package overspeed;

import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class SpeedSimulator {

    private static final String TAG = "SpeedSimulator";
    private DatabaseReference databaseReference;
    private Handler handler;
    private Runnable runnable;
    private boolean isRunning = false;
    private static final double SPEED_THRESHOLD = 80.0;

    // ตั้งค่ารถจำลอง 3 คัน
    private final String[] CAR_IDS = {"CAR_001", "CAR_002", "CAR_003"};
    private final Random random = new Random();

    public SpeedSimulator() {
        // อ้างอิง Path ใหม่: Log_Overspeed (นี่คือที่ข้อมูลจำลองจะถูกส่งไป)
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Log_Overspeed");
        this.handler = new Handler();
        this.runnable = new Runnable() {
            @Override
            public void run() {
                // 1. สุ่มเลือกรถ
                String carID = CAR_IDS[random.nextInt(CAR_IDS.length)];

                // 2. สุ่มความเร็วเกิน 80.0 km/h (ช่วง 80.1 ถึง 120.0)
                double simulatedSpeed = SPEED_THRESHOLD + 0.1 + (40 * random.nextDouble());

                // 3. สร้าง Log Event โดยใช้ Data Model ที่สร้างไว้
                OverspeedLog log = new OverspeedLog(carID, simulatedSpeed);

                // 4. Push ข้อมูลเข้า Firebase โดยใช้ .push() เพื่อสร้าง unique key
                databaseReference.push().setValue(log)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Simulated data pushed for " + carID + ": " + String.format("%.2f", simulatedSpeed) + " km/h"))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to push simulated data", e));

                // 5. ตั้งเวลาให้ทำงานอีกครั้งใน 3 วินาที
                handler.postDelayed(this, 3000);
            }
        };
    }

    public void startSimulation() {
        if (!isRunning) {
            isRunning = true;
            Log.i(TAG, "Speed simulation started, pushing data to Log_Overspeed every 3 seconds.");
            handler.post(runnable);
        }
    }

    public void stopSimulation() {
        if (isRunning) {
            isRunning = false;
            Log.i(TAG, "Speed simulation stopped.");
            handler.removeCallbacks(runnable);
        }
    }
}