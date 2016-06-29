package com.akshaydamle.service;

import java.util.List;

import com.akshaydamle.model.Person;

public interface PersonDAO {
	public void addPerson(Person p);
	public List<Person> listAllPersons();
	public List<Person> searchByName(String name);
	public List<Person> searchByID(String id);
	public List<Person> searchByBoth(String name, String id);
	public void deletePerson(String id);
	public void clearDB();
	public void updatePerson(String id, String name);
}
