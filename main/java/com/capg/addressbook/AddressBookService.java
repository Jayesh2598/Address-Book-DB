package com.capg.addressbook;

import java.util.List;
import java.util.logging.Logger;

public class AddressBookService {

	private static Logger log = Logger.getLogger(AddressBookService.class.getName());

	private List<Contact> contactList;
	private AddressBookDBService addressBookDBService;

	public AddressBookService() {
		addressBookDBService = AddressBookDBService.getInstance();
	}

	public AddressBookService(List<Contact> contactList) {
		this();
		this.contactList = contactList;
	}

	// Reading and returning list of contact from DB
	public List<Contact> readDataFromDB() {
		contactList = addressBookDBService.readAddressBookDB();
		return contactList;
	}
}