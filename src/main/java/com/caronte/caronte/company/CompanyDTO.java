package com.caronte.caronte.company;

import com.caronte.caronte.plan.Plan;

public class CompanyDTO {

    private String name;
    private String email;
    private String telephone;
    private String address;
    private String city;
    private String zipCode;
    private String imageUrl;
    private String description;
    private String nif;

    public CompanyDTO(String name, String email, String telephone, String address, String city, String zipCode, String imageUrl, String description, String nif) {
        this.name = name;
        this.email = email;
        this.telephone = telephone;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.imageUrl = imageUrl;
        this.description = description;
        this.nif = nif;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

}
