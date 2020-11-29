package com.tt.handsomeman.model;

public class PlaceResponse {
    private String primaryPlaceName;
    private String secondaryPlaceName;
    private Double latitude;
    private Double longitude;

    public PlaceResponse(String primaryPlaceName, String secondaryPlaceName, Double latitude, Double longitude) {
        this.primaryPlaceName = primaryPlaceName;
        this.secondaryPlaceName = secondaryPlaceName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPrimaryPlaceName() {
        return primaryPlaceName;
    }

    public void setPrimaryPlaceName(String primaryPlaceName) {
        this.primaryPlaceName = primaryPlaceName;
    }

    public String getSecondaryPlaceName() {
        return secondaryPlaceName;
    }

    public void setSecondaryPlaceName(String secondaryPlaceName) {
        this.secondaryPlaceName = secondaryPlaceName;
    }
}
