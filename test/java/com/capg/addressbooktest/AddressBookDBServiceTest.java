package com.capg.addressbooktest;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;
import org.junit.Before;

import com.capg.addressbook.Address;
import com.capg.addressbook.AddressBookDBService.Column;
import com.capg.addressbook.AddressBookService;
import com.capg.addressbook.Contact;
import com.google.gson.Gson;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class AddressBookDBServiceTest {

	@Test //UC16
	public void givenAddressBookDB_WhenRetrieved_ShouldMatchCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> book = addressBookService.readDataFromDB();
		Assert.assertEquals(4, book.size());
	}
	
	@Test //UC17
	public void givenDatabase_WhenUpdated_ShouldSyncWithMemory() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readDataFromDB();
		addressBookService.updateContactPhoneNumber("Ajeesh", "Ajayan", "7045279238");
		boolean result = addressBookService.checkContactListInSyncWithDB("Ajeesh", "Ajayan");
		assertTrue(result);
	}
	
	@Test //UC18
	public void givenDateRange_ShouldRetrieveContacts_AndMatchContactCount() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readDataFromDB();
		LocalDate startDate = LocalDate.of(2016, 01, 02);
		LocalDate endDate = LocalDate.now();
		List<Contact> contactList = addressBookService.getContactsInDateRange(startDate, endDate);
		Assert.assertEquals(3, contactList.size());
	}
	
	@Test //UC19
	public void givenDatabase_ShouldReturnContactsInACityOrState_AndMatchCount() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readDataFromDB();
		int contactListByCity = addressBookService.getCountOfContactsFromCityOrState(Column.CITY, "Mumbai");
		int contactListByState = addressBookService.getCountOfContactsFromCityOrState(Column.STATE, "Maharashtra");
		Assert.assertEquals(2, contactListByCity);
		Assert.assertEquals(2, contactListByState);
	}
	
	@Test //UC20
	public void givenNewContact_WhenAdded_ShouldSyncWithDB() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readDataFromDB();
		LocalDate date = LocalDate.now();
		addressBookService.addContactToAddressBook("Buddh", "Dev", "buddh@gmail.com", "7045279239", date, "Nagar", "Diu", "Gujarat", 400607, "Casual", "Acquaintance");
		boolean result = addressBookService.checkContactListInSyncWithDB("Buddh", "Dev");
		assertTrue(result); 
	}
	
	@Test //UC21
	public void givenMultipleContacts_WhenAdded_ShouldMatchContactCount() {
		LocalDate date = LocalDate.now();
		List<Address> list = new ArrayList<>();
		list.add(new Address("City Square", "Tokyo", "Japan", 700700));
		Contact[] contactArray = {
				new Contact("Hiruzen", "Sarutobi", "hiruzen@gmail.com", "9999999991", date, list, "A", "A"),
				new Contact("Minato", "Namikaze", "minato@gmail.com", "9999999992", date, list, "B", "B"),
				new Contact("Tsunade", "Senju", "tsu@gmail.com", "9999999993", date, list, "C", "C"),
				new Contact("Kakashi", "Hatake", "kakashi@gmail.com", "9999999994", date, list, "D", "D")
		};
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readDataFromDB();
		addressBookService.addContactsWithThreads(Arrays.asList(contactArray));
		List<Contact> book = addressBookService.readDataFromDB();
		Assert.assertEquals(9, book.size());
	}
	
	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}
	
	public Contact[] getContactList() {
		Response response = RestAssured.get("/address_book");
		System.out.println("Entries in address_book:" + response.asString());
		Contact[] contactList = new Gson().fromJson(response.asString(), Contact[].class);
		return contactList;
	}

	private Response addContactToJSONServer(Contact contact) {
		String empJson = new Gson().toJson(contact);
		RequestSpecification requestSpecification = RestAssured.given();
		requestSpecification.header("Content-Type", "application/json");
		requestSpecification.body(empJson);
		return requestSpecification.post("/address_book");
	}
	
	@Test
	public void givenEmployee_WhenAdded_ShouldMatchCount() {
		Contact[] contacts = getContactList();
		AddressBookService addressBookService = new AddressBookService(Arrays.asList(contacts));
		Contact contact = new Contact("Elon", "Musk", "elon@gmail.com", "9999888873", LocalDate.now());
		Response response = addContactToJSONServer(contact);
		int statusCode = response.getStatusCode();
		Assert.assertEquals(201, statusCode);
		
		contact = new Gson().fromJson(response.asString(), Contact.class);
		addressBookService.addContact(contact);
		
		int entries = addressBookService.countEntries();
		Assert.assertEquals(3, entries);
	}
}
