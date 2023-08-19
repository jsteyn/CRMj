package com.jannetta.crmj.database.model;

import java.util.ArrayList;
import java.util.List;

public class People {
    List<Person> people = new ArrayList<>();

    public List<Person> getPeople() {
        return people;
    }

    public void setPeople(List<Person> people) {
        this.people = people;
    }

    public boolean add(Person person) {
        return people.add(person);
    }

}
