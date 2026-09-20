package com.robindas.bloodbridge.DTO;

/**
 * DTO for creating a new blood request.
 */
public class BloodRequestRequest {

    private String patientName;
    private String bldGroup;
    private String city;
    private String district;
    private String hospital;
    private String unit;
    private String disease;
    private String status;

    public BloodRequestRequest() {}

    public BloodRequestRequest(String patientName, String bldGroup, String city, String district, String hospital, String unit, String disease, String status) {
        this.patientName = patientName;
        this.bldGroup = bldGroup;
        this.city = city;
        this.district = district;
        this.hospital = hospital;
        this.unit = unit;
        this.disease = disease;
        this.status = status;
    }

    // Getters and Setters
    public String getPatientName() {
        return patientName;
    }
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getBldGroup() {
        return bldGroup;
    }
    public void setBldGroup(String bldGroup) {
        this.bldGroup = bldGroup;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }
    public void setDistrict(String district) {
        this.district = district;
    }

    public String getHospital() {
        return hospital;
    }
    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDisease() {
        return disease;
    }
    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
