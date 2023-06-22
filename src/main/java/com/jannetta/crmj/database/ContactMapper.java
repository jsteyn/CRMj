package com.jannetta.crmj.database;

import org.apache.ibatis.annotations.*;

public interface ContactMapper {
    @Insert("INSERT INTO contacts(firstName, middleNames, lastName) VALUES(#{firstName}, #{middleNames}, #{lastName})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertContact(Contact contact);

    @Select("Select * FROM contacts WHERE id = #{id}")
    Contact getContact(int id);
}
