package com.example.project;


public class HelperClass {
    // ประกาศตัวแปร (Attributes/Fields) ไว้เก็บข้อมูล
    String name, email, username, password;

    // เมธอด getter สำหรับดึงค่า name
    public String getName() {
        return name;
    }

    // เมธอด setter สำหรับกำหนดค่า name
    public void setName(String name) {
        this.name = name;
    }

    // getter สำหรับดึงค่า email
    public String getEmail() {
        return email;
    }

    // setter สำหรับกำหนดค่า email
    public void setEmail(String email) {
        this.email = email;
    }

    // getter สำหรับดึงค่า username
    public String getUsername() {
        return username;
    }

    // setter สำหรับกำหนดค่า username
    public void setUsername(String username) {
        this.username = username;
    }

    // getter สำหรับดึงค่า password
    public String getPassword() {
        return password;
    }

    // setter สำหรับกำหนดค่า password
    public void setPassword(String password) {
        this.password = password;
    }

    // Constructor แบบกำหนดค่าเริ่มต้นให้ครบทุกฟิลด์
    public HelperClass(String name, String email, String username, String password) {
        this.name = name;         // กำหนดค่าตัวแปร name
        this.email = email;       // กำหนดค่าตัวแปร email
        this.username = username; // กำหนดค่าตัวแปร username
        this.password = password; // กำหนดค่าตัวแปร password
    }

    // Constructor ว่าง (Default) - เผื่อเวลาที่ไม่ต้องการส่งค่าเริ่มต้น
    public HelperClass() {
    }
}

