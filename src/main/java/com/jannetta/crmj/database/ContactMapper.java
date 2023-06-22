package com.jannetta.crmj.database;

import org.apache.ibatis.annotations.*;

import java.util.List;

public interface ContactMapper {
    @Update("CREATE TABLE IF NOT EXISTS contacts (id INTEGER PRIMARY KEY AUTOINCREMENT, firstName TEXT, middleNames TEXT, lastName TEXT)")
    void createTable();

    @Insert("INSERT INTO contacts(firstName, middleNames, lastName) VALUES(#{m_firstName}, #{m_middleNames}, #{m_lastName})")
    @Options(useGeneratedKeys = true, keyProperty = "m_id")
    void insertContact(Contact contact);

    @Select("Select * FROM contacts WHERE id = #{m_id}")
    @Results({
        @Result(property = "m_id", column = "id"),
        @Result(property = "m_firstName", column = "firstName"),
        @Result(property = "m_middleNames", column = "middleNames"),
        @Result(property = "m_lastName", column = "lastName"),
    })
    Contact getContact(int id);

    @Select("SELECT * FROM contacts")
    @Results({
        @Result(property = "m_id", column = "id"),
        @Result(property = "m_firstName", column = "firstName"),
        @Result(property = "m_middleNames", column = "middleNames"),
        @Result(property = "m_lastName", column = "lastName"),
    })
    List<Contact> getAllContacts();
}
