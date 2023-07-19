package com.jannetta.crmj.database.model;

import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "organisations")
public class Organisation {
    @SerializedName("organisation_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organisation_id")
    private int id;
    @SerializedName("name")
    @Column(name = "name", length = 50)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "m_organisations")
    private Set<Address> m_addresses;

    public Organisation() {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Address> getAddresses() {
        return m_addresses;
    }
}
