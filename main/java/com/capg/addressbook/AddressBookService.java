package com.capg.addressbook;

import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.capg.addressbook.AddressBookDBService.Column;

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
	
	public int getCountOfContactsFromCityOrState(Column columnName, String value) {
		int noOfContacts;
		try {
			noOfContacts = addressBookDBService.getCountOfContactsFromCityOrState(columnName, value);
			return noOfContacts;
		} catch (AddressBookSystemException e) {
			log.log(Level.SEVERE, "Exception occured.");
			return -1;
		}
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

	public List<Contact> getContactsInDateRange(Date startDate, Date endDate) {
		return addressBookDBService.getContactsInDateRange(startDate, endDate);
	}

	public void addContactToAddressBook(String firstName, String lastName, String email, String phNo, Date date, String address, String city, String state, int zip, String bookName, String bookType) {
		try {
			contactList.add(addressBookDBService.addContactToAddressBookDB(firstName, lastName, email, phNo, date, address, city, state, zip, bookName, bookType));
		} catch (AddressBookSystemException e) {
			log.log(Level.SEVERE, e.getMessage());
		}
	}
}