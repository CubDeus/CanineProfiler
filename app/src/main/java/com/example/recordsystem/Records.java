package com.example.recordsystem;

public class Records {
  String pva, petName, dob, sex, breed, color, ownerName, contact,address, imageProfile;

    public Records() {
    }

    public Records(String pva, String petName,
                   String dob, String sex,
                   String breed, String color,
                   String ownerName, String contact,
                   String address,
                   String imageProfile) {

        this.pva = pva;
        this.petName = petName;
        this.dob = dob;
        this.sex = sex;
        this.breed = breed;
        this.color = color;
        this.ownerName = ownerName;
        this.contact = contact;
        this.address = address;
        this.imageProfile = imageProfile;
    }

    public String getPva() {
        return pva;
    }

    public void setPva(String pva) {
        this.pva = pva;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }
}
