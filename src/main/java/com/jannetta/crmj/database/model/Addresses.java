package com.jannetta.crmj.database.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Addresses {
    @SerializedName("addresses")
    private ArrayList<Address> addresses = new ArrayList<>();

    public void add(Address address) {
        addresses.add(address);
    }
    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }
}
