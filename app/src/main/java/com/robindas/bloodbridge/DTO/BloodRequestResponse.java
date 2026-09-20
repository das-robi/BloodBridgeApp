package com.robindas.bloodbridge.DTO;

/**
 * DTO for blood request details returned from the API.
 */
public class BloodRequestResponse {

    private int bldId;
    private String patientName;
    private String bldGroup;
    private String city;
    private String district;
    private String hospital;
    private String unit;
    private String disease;
    private String requesterName;
    private String status;

    public BloodRequestResponse() {}

    // Getters and Setters
    public int getBldId() {
        return bldId;
    }
    public void setBldId(int bldId) {
        this.bldId = bldId;
    }
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

    public String getRequesterName() {
        return requesterName;
    }
    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
