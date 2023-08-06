package com.jannetta.crmj.database.model;

import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "people")
public class Person {
    @SerializedName("personId")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private int m_id;

    @SerializedName("firstName")
    @Column(name = "first_name", length = 50)
    private String m_firstName;
    @SerializedName("middleNames")
    @Column(name = "middle_names", length = 50)
    private String m_middleNames;
    @SerializedName("lastName")
    @Column(name = "last_name", length = 50)
    private String m_lastName;
    @SerializedName("title")
    @Column(name = "title", length = 50)
    private String m_title;
    @SerializedName("dateOfBirth")
    @Column(name = "date_of_birth")
    private Date m_dateOfBirth;
    @SerializedName("nickName")
    @Column(name = "nick_name", length = 50)
    private String m_nickName;
    @SerializedName("maidenName")
    @Column(name = "maiden_name", length = 50)
    private String m_maidenName;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "m_people")
    private Set<Address> m_addresses;
    @OneToMany(
        mappedBy = "m_person",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<PersonSocialMedia> m_socialMedia;

    public Person() {

    }

    public int getId() {
        return m_id;
    }

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

    public String getTitle() {
        return m_title;
    }

    public void setTitle(String title) {
        m_title = title;
    }

    public Date getDateOfBirth() {
        return m_dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        m_dateOfBirth = dateOfBirth;
    }

    public String getNickName() {
        return m_nickName;
    }

    public void setNickName(String nickName) {
        m_nickName = nickName;
    }

    public String getMaidenName() {
        return m_maidenName;
    }

    public void setMaidenName(String maidenName) {
        m_maidenName = maidenName;
    }


    public Set<Address> getAddresses() {
        return m_addresses;
    }

    public Set<PersonSocialMedia> getSocialMedia() {
        return m_socialMedia;
    }
}
