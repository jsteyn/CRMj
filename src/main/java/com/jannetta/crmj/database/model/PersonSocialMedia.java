package com.jannetta.crmj.database.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "people_social_media")
public class PersonSocialMedia {
    @EmbeddedId
    private PersonSocialMediaId m_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("m_personId")
    private Person m_person;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("m_socialMediaId")
    private SocialMedia m_socialMedia;

    @Column(name = "user_name", length = 50)
    private String m_userName;

    @Column(name = "url", length = 100)
    private String m_url;

    private PersonSocialMedia() {

    }

    public PersonSocialMedia(Person person, SocialMedia socialMedia) {
        m_person = person;
        m_socialMedia = socialMedia;
        m_id = new PersonSocialMediaId(person.getId(), socialMedia.getId());
    }

    public PersonSocialMediaId getId() {
        return m_id;
    }

    public Person getPerson() {
        return m_person;
    }

    public SocialMedia getSocialMedia() {
        return m_socialMedia;
    }

    public String getUserName() {
        return m_userName;
    }

    public void setUserName(String userName) {
        m_userName = userName;
    }

    public String getUrl() {
        return m_url;
    }

    public void setUrl(String url) {
        m_url = url;
    }

    @Embeddable
    public class PersonSocialMediaId implements Serializable {
        @Column(name = "person_id")
        private int m_personId;
        @Column(name = "social_media_id")
        private int m_socialMediaId;

        private PersonSocialMediaId() {

        }

        public PersonSocialMediaId(int personId, int socialMediaId) {
            m_personId = personId;
            m_socialMediaId = socialMediaId;
        }

        public int getPersonId() {
            return m_personId;
        }

        public int getSocialMediaId() {
            return m_socialMediaId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            PersonSocialMediaId that = (PersonSocialMediaId) o;
            return Objects.equals(m_personId, that.m_personId) && Objects.equals(m_socialMediaId, that.m_socialMediaId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(m_personId, m_socialMediaId);
        }
    }
}
