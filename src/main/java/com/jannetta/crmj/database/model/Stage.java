package com.jannetta.crmj.database.model;

import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "stages")
public class Stage {
    @SerializedName("stage_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stage_id")
    private int m_id;

    @SerializedName("name")
    @Column(name = "name", length = 50)
    private String m_name;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Project> m_projects;

    public Stage() {

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

    public Set<Project> getProjects() {
        return m_projects;
    }
}
