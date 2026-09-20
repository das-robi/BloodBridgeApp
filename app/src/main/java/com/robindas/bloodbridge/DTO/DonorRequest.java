package com.robindas.bloodbridge.DTO;

public class DonorRequest {
    private String bldGroup;
    private String city;
    private String district;
    private String phone;
    private String lastDonateDate;
    private boolean available;

    public DonorRequest() {}

    public DonorRequest(String bldGroup, String city, String district, String phone, String lastDonateDate, boolean available) {
        this.bldGroup = bldGroup;
        this.city = city;
        this.district = district;
        this.phone = phone;
        this.lastDonateDate = lastDonateDate;
        this.available = available;
    }

    // Getters and Setters
    public String getBldGroup() { return bldGroup; }
    public void setBldGroup(String bldGroup) { this.bldGroup = bldGroup; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLastDonateDate() { return lastDonateDate; }
    public void setLastDonateDate(String lastDonateDate) { this.lastDonateDate = lastDonateDate; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
