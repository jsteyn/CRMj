package com.jannetta.crmj.database.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EmailAddresses {
    @SerializedName("emailaddresses")
    private ArrayList<Email> emailaddresses = new ArrayList<>();

    public void add(Email email) {
        emailaddresses.add(email);
    }
    public ArrayList<Email> getAddresses() {
        return emailaddresses;
    }

    public void setEmailaddresses(ArrayList<Email> emailaddresses) {
        this.emailaddresses = emailaddresses;
    }
}
