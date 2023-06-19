package com.jannetta.crmj.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contact {


    @SerializedName("uuid")
    @Expose
    private String m_UUID;
    @SerializedName("firstname")
    @Expose
    private String m_firstName;
    @SerializedName("middlename")
    @Expose
    private String m_middleName;
    @SerializedName("lastname")
    @Expose
    private String m_lastName;

    public Contact(String UUID, String firstname, String middleName, String lastname) {
        m_UUID = UUID;
        m_firstName = firstname;
        m_middleName = middleName;
        m_lastName = lastname;
    }

    public Contact() {

    }

    public String getFirstName() {
        return m_firstName;
    }

    public String getUUID() {
        return m_UUID;
    }

    public void setUUID(String UUID) {
        m_UUID = UUID;
    }

    public void setFirstName(String m_firstName) {
        this.m_firstName = m_firstName;
    }

    public String getMiddleName() {
        return m_middleName;
    }

    public void setMiddleName(String m_middleName) {
        this.m_middleName = m_middleName;
    }

    public String getLastName() {
        return m_lastName;
    }

    public void setLastName(String m_lastName) {
        this.m_lastName = m_lastName;
    }

}
