package com.capg.addressbook;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		this.contactList = new ArrayList<Contact>(contactList);
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
	
	public List<Contact> getContactsInDateRange(LocalDate startDate, LocalDate endDate) {
		return addressBookDBService.getContactsInDateRange(startDate, endDate);
	}
	
	public boolean checkContactListInSyncWithDB(String firstName, String lastName) {
		List<Contact> list = addressBookDBService.getContactData(firstName, lastName);
		return list.get(0).equals(getContactData(firstName, lastName));
	}
	

	public boolean checkContactListInSyncWithDB(List<Contact> list) {
		boolean result = true;
		for(Contact contact: list) 
			result = result && contactList.contains(contact);
		return result;
	}
	
	private Contact getContactData(String firstName, String lastName) {
		return this.contactList.stream()
					.filter(item -> item.firstName.equals(firstName) && item.lastName.equals(lastName))
					.findFirst()
					.orElse(null);
	}
	
	public void addContactToAddressBook(String firstName, String lastName, String email, String phNo, LocalDate date, String address, String city, String state, int zip, String bookName, String bookType) {
		try {
			contactList.add(addressBookDBService.addContactToAddressBookDB(firstName, lastName, email, phNo, date, address, city, state, zip, bookName, bookType));
		} catch (AddressBookSystemException e) {
			log.log(Level.SEVERE, e.getMessage());
		}
	}

	public void addContactsWithThreads(List<Contact> contactList) {
		Map<Integer, Boolean> contactAdditionStatus = new HashMap<>();
		contactList.forEach(contact -> {
			Runnable task = () -> {
				contactAdditionStatus.put(contact.hashCode(), false);
				Address address = contact.addressList.get(0);
				log.log(Level.INFO, ()-> "Contact being added: " + Thread.currentThread().getName());
				this.addContactToAddressBook(contact.firstName, contact.lastName, contact.email, contact.phoneNo, contact.addDate, 
						address.getAddress(), address.getCity(), address.getState(), address.getZip(), contact.addressBookName, contact.addressBookType);
				contactAdditionStatus.put(contact.hashCode(), true);
				log.log(Level.INFO, ()-> "Contact added: " + Thread.currentThread().getName());
			};
			Thread thread = new Thread(task, contact.firstName);
			thread.start();
		});
		try {
			while(contactAdditionStatus.containsValue(false));
					Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void addContact(Contact contact) {
		contactList.add(contact);
	}

	public int countEntries() {
		return contactList.size();
	}
}