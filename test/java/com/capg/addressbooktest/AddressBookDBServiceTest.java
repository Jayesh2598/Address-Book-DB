package com.capg.addressbooktest;

import java.util.List;

import org.junit.Test;

import com.capg.addressbook.AddressBookService;
import com.capg.addressbook.Contact;

import org.junit.Assert;

public class AddressBookDBServiceTest {

	@Test
	public void givenAddressBookDB_WhenRetrieved_ShouldMatchCount() {
		AddressBookService addressBookService = new AddressBookService();
		List<Contact> book = addressBookService.readDataFromDB();
		Assert.assertEquals(6, book.size());
	}
}
