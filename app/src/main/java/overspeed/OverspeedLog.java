package overspeed;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OverspeedLog {
    private String carID;
    private double speed;
    private double limit = 80.0; // ค่าขีดจำกัดคงที่
    private String alertTime;

    // ต้องมี Constructor ว่างสำหรับ Firebase
    public OverspeedLog() {
        // Required for Firebase DataSnapshot.getValue(OverspeedLog.class)
    }

    public OverspeedLog(String carID, double speed) {
        this.carID = carID;
        this.speed = speed;
        // กำหนดเวลาปัจจุบัน
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        this.alertTime = sdf.format(new Date());
    }

    // Getters สำหรับ Firebase และการเข้าถึงข้อมูล
    public String getCarID() {
        return carID;
    }

    public double getSpeed() {
        return speed;
    }

    public double getLimit() {
        return limit;
    }

    public String getAlertTime() {
        return alertTime;
    }

    // Setters (อาจไม่จำเป็นต้องใช้ในงานนี้ แต่ควรมี)
    public void setCarID(String carID) {
        this.carID = carID;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public void setAlertTime(String alertTime) {
        this.alertTime = alertTime;
    }
}