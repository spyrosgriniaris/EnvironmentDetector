package com.myhost.spyros.environmentdetector.Models;


public class FirebaseDetectionEntity {

    private String detectedObjectName;
    private double detectedObjectLatitude, detectedObjectLongitude;
    private long timestamp;

    public FirebaseDetectionEntity(String detectedObjectName, double detectedObjectLatitude, double detectedObjectLongitude, long timestamp) {
        this.detectedObjectName = detectedObjectName;
        this.detectedObjectLatitude = detectedObjectLatitude;
        this.detectedObjectLongitude = detectedObjectLongitude;
        this.timestamp = timestamp;
    }

    public String getDetectedObjectName() {
        return detectedObjectName;
    }

    public void setDetectedObjectName(String detectedObjectName) {
        this.detectedObjectName = detectedObjectName;
    }

    public double getDetectedObjectLatitude() {
        return detectedObjectLatitude;
    }

    public void setDetectedObjectLatitude(double detectedObjectLatitude) {
        this.detectedObjectLatitude = detectedObjectLatitude;
    }

    public double getDetectedObjectLongitude() {
        return detectedObjectLongitude;
    }

    public void setDetectedObjectLongitude(double detectedObjectLongitude) {
        this.detectedObjectLongitude = detectedObjectLongitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
