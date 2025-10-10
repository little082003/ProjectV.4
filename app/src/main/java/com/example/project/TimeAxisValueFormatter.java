package com.example.project;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeAxisValueFormatter extends ValueFormatter {

    private final SimpleDateFormat mFormat;
    private final Date mDate;

    public TimeAxisValueFormatter() {
        // กำหนดรูปแบบของเวลาที่จะแสดงบนแกน
        mFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        mDate = new Date();
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        // 'value' ที่ส่งเข้ามาคือ timestamp (เวลาในหน่วยมิลลิวินาที)
        // เราจะแปลงค่านั้นกลับเป็น Date object แล้วจัดรูปแบบ
        mDate.setTime((long) value);
        return mFormat.format(mDate);
    }
}