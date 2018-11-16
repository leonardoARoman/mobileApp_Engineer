package com.example.roman.locationpath;

import java.io.Serializable;
import java.util.List;

public class LocationNode implements Serializable {
    private List<LocationNode> locations;
    private String address, zip;
    private double latitude, longitude;
    private boolean permissionGranted = false;

    public LocationNode(){ permissionGranted = true; }

    public LocationNode(String address, double latitude, double longitude, String zip){
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zip = zip;
        permissionGranted = true;
    }

    public List<LocationNode> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationNode> locations) {
        this.locations = locations;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setZip(String zip) { this.zip = zip; }

    public String getZip() { return zip; }

    public Boolean permissionGranted(){ return permissionGranted; }

}
