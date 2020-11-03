package com.capg.addressbook;

import java.util.List;
import java.util.logging.Level;
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
	
	public void updateContactPhoneNumber(String firstName, String lastName, String phoneNumber) {
		int result = addressBookDBService.updateContactData(firstName, lastName, phoneNumber);
		if (result == 0)
			log.log(Level.INFO, "No updation performed.");
		Contact contact = this.getContactData(firstName, lastName);
		if (contact != null)
			contact.phoneNo = phoneNumber;
	}
	
	public boolean checkContactListInSyncWithDB(String firstName, String lastName) {
		List<Contact> list = addressBookDBService.getContactData(firstName, lastName);
		return list.get(0).equals(getContactData(firstName, lastName));
	}
	
	private Contact getContactData(String firstName, String lastName) {
		return this.contactList.stream()
					.filter(item -> item.firstName.equals(firstName) && item.lastName.equals(lastName))
					.findFirst()
					.orElse(null);
	}
}