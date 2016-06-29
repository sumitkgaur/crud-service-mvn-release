package com.akshaydamle.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.akshaydamle.model.Person;
import com.akshaydamle.service.PersonDAO;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Controller
@EnableWebMvc
public class CRUDController {

	@Autowired
	private PersonDAO PD;

	@RequestMapping(method = RequestMethod.GET, value = "/hello")
	@ApiOperation(value = "Says \"Hello World!\"", hidden = true, response = String.class)
	@ResponseBody
	public String welcomeMessage() {
		return "Hello World!";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/add")
	@ApiOperation(value = "Creates a new person", code = 201)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Record created successfully"),
			@ApiResponse(code = 409, message = "ID already taken")
	})
	public ResponseEntity<String> add(@RequestParam(value = "name", required = true) String name,
			@RequestParam(value = "id", required = true) String id) {
		if (PD.searchByID(id).size() == 0) {
			Person p = new Person(name, id);
			PD.addPerson(p);
			System.out.println("Person added.");
			return new ResponseEntity<String>(HttpStatus.CREATED);
		} else {
			System.out.println("ID already taken.");
			return new ResponseEntity<String>(HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(method = RequestMethod.GET, produces = "application/json", value = "/search")
	@ApiOperation(value = "Searches for a person with given name and/or ID",
				  response = Person.class,
				  responseContainer = "List")
	@ApiResponses(value = {
			@ApiResponse(code = 400, message = "Both name and ID fields cannot be absent"),
			@ApiResponse(code = 200, message = "Search completed")
	})
	public ResponseEntity<List<Person>> search(
			@RequestParam(value = "name", required = false, defaultValue = "") String name,
			@RequestParam(value = "id", required = false, defaultValue = "") String id) {
		List<Person> l = null;
		if (name.equals("")) {
			if (id.equals("")) {
				System.out.println("Please provide name or ID to search for.");
				return new ResponseEntity<List<Person>>(l, HttpStatus.BAD_REQUEST);
			} else {
				l = PD.searchByID(id);
			}
		} else {
			if (id.equals("")) {
				l = PD.searchByName(name);
			} else {
				l = PD.searchByBoth(name, id);
			}
		}
		if (l == null || l.size() == 0) {
			System.out.println("No records found.");
		}
		return new ResponseEntity<List<Person>>(l, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, produces = "application/json", value = "/update")
	@ApiOperation(value = "Update the name of person with given ID",
				  response = Person.class)
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Record with given ID does not exist"),
			@ApiResponse(code = 200, message = "Record updated successfully")
	})
	public ResponseEntity<Person> update(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "name", required = true) String name) {
		if(PD.searchByID(id).size() == 0) {
			return new ResponseEntity<Person>(HttpStatus.NOT_FOUND);
		} else {
			PD.updatePerson(id, name);
			return new ResponseEntity<Person>(PD.searchByID(id).get(0), HttpStatus.OK);
		}
	}

	@RequestMapping(method = RequestMethod.GET, value = "/remove")
	@ApiOperation(value = "Remove the person with given ID")
	@ApiResponses(value = {
			@ApiResponse(code = 404, message = "Record with given ID does not exist"),
			@ApiResponse(code = 200, message = "Record deleted successfully")
	})
	public ResponseEntity<String> delete(@RequestParam(value = "id", required = true) String id) {
		if (PD.searchByID(id).size() == 0) {
			System.out.println("Record with given ID does not exist.");
			return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
		} else {
			PD.deletePerson(id);
			System.out.println("Record deleted successfully.");
			return new ResponseEntity<String>(HttpStatus.OK);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/drop")
	@ApiOperation(value = "Removes all records - clears the entire data")
	public ResponseEntity<String> drop() {
		PD.clearDB();
		System.out.println("Table cleared.");
		return new ResponseEntity<String>(HttpStatus.OK);
	}

	public String makeOutputString(List<Person> l) {
		String res = "";
		for (Person p : l) {
			res += "name: " + p.getName() + " id: " + p.getId();
		}
		return res;
	}

	public void printList(List<Person> l) {
		for (Person p : l) {
			System.out.println("name: " + p.getName() + ", id: " + p.getId());
		}
	}
}