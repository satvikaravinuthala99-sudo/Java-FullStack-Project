package com.echovault.model;
import java.sql.Timestamp;
public class Capsule {
 public int id; public String title,description,recipientEmail,visibility,status; public Timestamp unlockAt,createdAt; public String fileName,filePath;
 public boolean isUnlocked(){return unlockAt!=null && !unlockAt.after(new Timestamp(System.currentTimeMillis()));}
}