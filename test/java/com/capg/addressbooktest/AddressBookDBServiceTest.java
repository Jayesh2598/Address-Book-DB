package com.capg.addressbooktest;

import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;

import com.capg.addressbook.AddressBookService;
import com.capg.addressbook.Contact;

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
		List<Contact> contactList = addressBookService.getContactsInDateRange(Date.valueOf(startDate), Date.valueOf(endDate));
		Assert.assertEquals(3, contactList.size());
	}
}
