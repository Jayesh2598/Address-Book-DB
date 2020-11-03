package com.capg.addressbook;

import java.util.ArrayList;
import java.util.List;

class Address {

	private String address, city, state;
	private int zip;

	public Address(String address, String city, String state, int zip) {
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}
}

public class Contact {

	public String firstName, lastName, email, phoneNo, addressBookName, addressBookType;
	public List<Address> addressList = new ArrayList<>();
	
	public Contact(String firstName, String lastName, String email, String phoneNo) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNo = phoneNo;
	}

	public Contact(String firstName, String lastName, String phoneNo, String email, List<Address> addressArray) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNo = phoneNo;
		this.email = email;
		this.addressList = addressArray;
	}

	public Contact(String firstName, String lastName, String phoneNo, String email, List<Address> addressArray, String addressBookName, String addressBookType) {
		this(firstName, lastName, phoneNo, email, addressArray);
		this.addressBookName = addressBookName;
		this.addressBookType = addressBookType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Contact that = (Contact) obj;
		return firstName.equals(that.firstName) && lastName.equals(that.lastName) && 
				phoneNo.equals(that.phoneNo) && email.equals(that.email);
	}
}
