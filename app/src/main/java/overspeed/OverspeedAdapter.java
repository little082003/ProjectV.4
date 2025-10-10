package overspeed;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;

import java.util.List;

public class OverspeedAdapter extends RecyclerView.Adapter<OverspeedAdapter.OverspeedViewHolder> {

    private List<OverspeedLog> logList;

    // Constructor: รับรายการ Log เข้ามา
    public OverspeedAdapter(List<OverspeedLog> logList) {
        this.logList = logList;
    }

    // ViewHolder: จัดการมุมมองของแต่ละรายการ
    public static class OverspeedViewHolder extends RecyclerView.ViewHolder {
        TextView textCarId;
        TextView textSpeed;
        TextView textTime;

        public OverspeedViewHolder(@NonNull View itemView) {
            super(itemView);
            textCarId = itemView.findViewById(R.id.textCarId);
            textSpeed = itemView.findViewById(R.id.textSpeed);
            textTime = itemView.findViewById(R.id.textTime);
        }
    }

    // 1. สร้าง ViewHolder เมื่อจำเป็นต้องใช้ Layout ใหม่
    @NonNull
    @Override
    public OverspeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // เชื่อม item_overspeed_log.xml เข้ากับ RecyclerView
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_overspeed_log, parent, false);
        return new OverspeedViewHolder(view);
    }

    // 2. ผูกข้อมูลเข้ากับ View
    @Override
    public void onBindViewHolder(@NonNull OverspeedViewHolder holder, int position) {
        OverspeedLog log = logList.get(position);

        // ดึงข้อมูลจาก Log object และนำไปแสดงใน TextView
        holder.textCarId.setText("รถคันที่: " + log.getCarID());
        holder.textSpeed.setText("ความเร็ว: " + String.format("%.2f", log.getSpeed()) + " km/h");
        holder.textTime.setText("เวลา: " + log.getAlertTime());

        // สามารถเพิ่มการเปลี่ยนสี CardView ตามความรุนแรงของความเร็วที่นี่ได้
    }

    // 3. บอกจำนวนรายการทั้งหมด
    @Override
    public int getItemCount() {
        return logList.size();
    }
}