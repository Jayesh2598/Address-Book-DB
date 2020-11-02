package com.capg.addressbook;

public class Contact {

	private String firstName, lastName, address, city, state, email, addressBookName, addressBookType;
	private int zip;
	private String phoneNo;

	public Contact(String firstName, String lastName, String address, String city, String state, int zip,
			String phoneNo, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.phoneNo = phoneNo;
		this.email = email;
	}

	public Contact(String firstName, String lastName, String address, String city, String state, int zip,
			String phoneNo, String email, String addressBookName, String addressBookType) {
		this(firstName, lastName, address, city, state, zip, phoneNo, email);
		this.addressBookName = addressBookName;
		this.addressBookType = addressBookType;
	}
}
