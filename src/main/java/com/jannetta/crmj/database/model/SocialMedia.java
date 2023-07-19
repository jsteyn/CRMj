package com.jannetta.crmj.database.model;

import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "social_media")
public class SocialMedia {
    @SerializedName("social_media_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_media_id")
    private int m_id;

    @SerializedName("name")
    @Column(name = "name", length = 50)
    private String m_name;

    @OneToMany(
        mappedBy = "m_socialMedia",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<PersonSocialMedia> m_people;

    public SocialMedia() {

    }

    public int getId() {
        return m_id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public Set<PersonSocialMedia> getPeople() {
        return m_people;
    }
}
