package com.jannetta.crmj.database.model;

import com.google.gson.annotations.SerializedName;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "addresses")
public class Address {
    @SerializedName("address_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private int m_id;

    @SerializedName("address_line_1")
    @Column(name = "address_line_1", length = 50)
    private String m_addressLine1;
    @SerializedName("address_line_2")
    @Column(name = "address_line_2", length = 50)
    private String m_addressLine2;
    @SerializedName("address_line_3")
    @Column(name = "address_line_3", length = 50)
    private String m_addressLine3;

    @SerializedName("city")
    @Column(name = "city", length = 50)
    private String m_city;
    @SerializedName("county")
    @Column(name = "county", length = 50)
    private String m_county;
    @SerializedName("postcode")
    @Column(name = "postcode", length = 50)
    private String m_postcode;
    @SerializedName("country")
    @Column(name = "country", length = 50)
    private String m_country;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "person_address",
        joinColumns = {@JoinColumn(name = "address_id")},
        inverseJoinColumns = {@JoinColumn(name = "person_id")}
    )
    private Set<Person> m_people;
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "organisation_address",
        joinColumns = {@JoinColumn(name = "address_id")},
        inverseJoinColumns = {@JoinColumn(name = "organisation_id")}
    )
    private Set<Organisation> m_organisations;

    public Address() {

    }

    public int getId() {
        return m_id;
    }

    public String getAddressLine1() {
        return m_addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        m_addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return m_addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        m_addressLine2 = addressLine2;
    }

    public String getAddressLine3() {
        return m_addressLine3;
    }

    public void setAddressLine3(String addressLine3) {
        m_addressLine3 = addressLine3;
    }

    public String getCity() {
        return m_city;
    }

    public void setCity(String city) {
        m_city = city;
    }

    public String getCounty() {
        return m_county;
    }

    public void setCounty(String county) {
        m_county = county;
    }

    public String getPostcode() {
        return m_postcode;
    }

    public void setPostcode(String postcode) {
        m_postcode = postcode;
    }

    public String getCountry() {
        return m_country;
    }

    public void setCountry(String country) {
        m_country = country;
    }

    public Set<Person> getPeople() {
        return m_people;
    }

    public Set<Organisation> getOrganisations() {
        return m_organisations;
    }
}
