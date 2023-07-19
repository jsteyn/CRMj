package com.jannetta.crmj.database.model;

import com.google.gson.annotations.SerializedName;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "projects")
public class Project {
    @SerializedName("project_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private int m_id;

    @SerializedName("name")
    @Column(name = "name", length = 50)
    private String m_name;

    @SerializedName("start_date")
    @Column(name = "start_date")
    private Date m_startDate;
    @SerializedName("close_date")
    @Column(name = "close_date")
    private Date m_closeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Stage m_stage;

    public Project() {

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

    public Date getStartDate() {
        return m_startDate;
    }

    public void setStartDate(Date startDate) {
        m_startDate = startDate;
    }

    public Date getCloseDate() {
        return m_closeDate;
    }

    public void setCloseDate(Date closeDate) {
        m_closeDate = closeDate;
    }

    public Stage getStage() {
        return m_stage;
    }

    public void setStage(Stage stage) {
        m_stage = stage;
    }
}
