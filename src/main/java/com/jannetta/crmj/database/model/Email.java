package com.jannetta.crmj.database.model;

import com.google.gson.annotations.SerializedName;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "emailaddresses")
public class Email {
    @SerializedName("emailId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "email_id")
    private int m_id;

    @SerializedName("email")
    @Column(name = "email", length = 50)
    private String m_email;

    public int getId() {
        return m_id;
    }

    public void setId(int m_id) {
        this.m_id = m_id;
    }

    public String getEmail() {
        return m_email;
    }

    public void setEmail(String m_email) {
        this.m_email = m_email;
    }
}
