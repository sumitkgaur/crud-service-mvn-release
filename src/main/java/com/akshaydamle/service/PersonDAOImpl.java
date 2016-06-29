package com.akshaydamle.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.akshaydamle.model.Person;

@Repository
public class PersonDAOImpl implements PersonDAO {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	public static final String COLLECTIONNAME = "person";
	
	public void clearDB() {
		if(mongoTemplate.collectionExists(Person.class)) {
			mongoTemplate.dropCollection(COLLECTIONNAME);
		}
	}
	
	public void addPerson(Person p) {
		if(!mongoTemplate.collectionExists(Person.class)) {
			mongoTemplate.createCollection(Person.class);
		}
		mongoTemplate.insert(p, COLLECTIONNAME);
		System.out.println("Added person");
		listAllPersons();
	}

	public List<Person> listAllPersons() {
		List<Person> l = mongoTemplate.findAll(Person.class, COLLECTIONNAME);
		for(Person p : l) {
			System.out.println(p.getName());
			System.out.println(p.getId());
		}
		return null;
	}

	public List<Person> searchByName(String name) {
		Query q = new Query();
		q.addCriteria(Criteria.where("name").is(name));
		return mongoTemplate.find(q, Person.class, COLLECTIONNAME);
	}

	public List<Person> searchByID(String id) {
		Query q = new Query();
		q.addCriteria(Criteria.where("id").is(id));
		return mongoTemplate.find(q, Person.class, COLLECTIONNAME);
	}

	public List<Person> searchByBoth(String name, String id) {
		Query q = new Query();
		q.addCriteria(Criteria.where("name").is(name));
		q.addCriteria(Criteria.where("id").is(id));
		return mongoTemplate.find(q, Person.class, COLLECTIONNAME);
	}

	public void deletePerson(String id) {
		mongoTemplate.remove(new Query(Criteria.where("id").is(id)), Person.class);
	}
	
	public void updatePerson(String id, String newName) {
		deletePerson(id);
		addPerson(new Person(newName, id));
	}
}