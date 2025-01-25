package org.courier.models;

public class Address {
    private String fullName;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String addressType;
    public final String addressState = "update";


    public Address(String fullName, String streetAddress, String city, String state, String zipCode, String addressType) {
        this.fullName = fullName;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.addressType = addressType;
    }

    public Address() {}

    public String getFullName() { return fullName; }
    public String getStreetAddress() { return streetAddress; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZipCode() { return zipCode; }
    public String getAddressType() { return addressType; }
    public String getAddressState() { return addressState; }

}
