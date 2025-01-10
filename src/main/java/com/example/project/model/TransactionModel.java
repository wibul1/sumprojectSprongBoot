package com.example.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "transactions")
public class TransactionModel {
    @Id
    private String id;
    private String amount;
    private String date;
    private String time;
    private String rawText; // ข้อความดิบจาก OCR
    private String fileName;
    private String month;  // เพิ่มฟิลด์เดือน
    private String year;

    // Getters
    public String getRawText() { return rawText; }
    public String getAmount() { return amount; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getFileName() { return fileName; }
    public String getMonth() { return month; }
    public String getYear() { return year; }

    // Setters
    public void setRawText(String rawText) { this.rawText = rawText; }
    public void setAmount(String amount) { this.amount = amount; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setMonth(String month) { this.month = month; }
    public void setYear(String year) { this.year = year; }
}
