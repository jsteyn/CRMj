package com.jannetta.crmj.database.model;

import com.google.gson.annotations.*;

import javax.persistence.*;

@Entity
@Table(name = "contacts")
public class Contact {
    @SerializedName("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int m_id;
    @SerializedName("firstName")
    @Column(name = "firstName")
    private String m_firstName;
    @SerializedName("middleNames")
    @Column(name = "middleNames")
    private String m_middleNames;
    @SerializedName("lastName")
    @Column(name = "lastName")
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
