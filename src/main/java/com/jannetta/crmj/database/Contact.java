package com.jannetta.crmj.database;

import com.google.gson.annotations.SerializedName;

public class Contact {
    private int m_id;
    @SerializedName("firstName")
    private String m_firstName;
    @SerializedName("middleNames")
    private String m_middleNames;
    @SerializedName("lastName")
    private String m_lastName;

    public String getFirstName() {
        return m_firstName;
    }

    public void setFirstName(String firstName) {
        m_firstName = firstName;
    }

    public String getMiddleNames() {
        return m_middleNames;
    }

    public void setMiddleNames(String middleNames) {
        m_middleNames = middleNames;
    }

    public String getLastName() {
        return m_lastName;
    }

    public void setLastName(String lastName) {
        m_lastName = lastName;
    }
}
