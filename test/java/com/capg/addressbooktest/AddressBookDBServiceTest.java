package com.capg.addressbooktest;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.capg.addressbook.AddressBookService;
import com.capg.addressbook.Contact;

import org.junit.Assert;

public class AddressBookDBServiceTest {

	@Test //UC1
	public void givenAddressBookDB_WhenRetrieved_ShouldMatchCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> book = addressBookService.readDataFromDB();
		Assert.assertEquals(4, book.size());
	}
	
	@Test //UC2
	public void givenDatabase_WhenUpdated_ShouldSyncWithMemory() {
		AddressBookService addressBookService = new AddressBookService();
		addressBookService.readDataFromDB();
		addressBookService.updateContactPhoneNumber("Ajeesh", "Ajayan", "7045279238");
		boolean result = addressBookService.checkContactListInSyncWithDB("Ajeesh", "Ajayan");
		assertTrue(result);
	}
}
