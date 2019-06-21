package com.myhost.spyros.environmentdetector.Models;

public class ViewAllObjectsModel {
    String detectedObjectName;
    double detectedObjectLatitude, detectedObjectLongitude;
    long timestamp;

    public ViewAllObjectsModel() {
    }

    public ViewAllObjectsModel(double objectLatitude, double objectLongitude ,String objectName, long timestamp) {
        this.detectedObjectName = objectName;
        this.detectedObjectLatitude = objectLatitude;
        this.detectedObjectLongitude = objectLongitude;
        this.timestamp = timestamp;
    }

    public String getDetectedObjectName() {
        return detectedObjectName;
    }

    public double getDetectedObjectLatitude() {
        return detectedObjectLatitude;
    }

    public double getDetectedObjectLongitude() {
        return detectedObjectLongitude;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
