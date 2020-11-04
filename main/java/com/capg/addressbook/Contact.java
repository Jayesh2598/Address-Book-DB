package com.capg.addressbook;

import java.sql.Date;
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
	public Date addDate;
	
	public Contact(String firstName, String lastName, String email, String phoneNo, Date addDate) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phoneNo = phoneNo;
		this.addDate = addDate;
	}

	public Contact(String firstName, String lastName, String email, String phoneNo, Date addDate, List<Address> addressArray) {
		this(firstName, lastName, email, phoneNo, addDate);
		this.addressList = addressArray;
	}

	public Contact(String firstName, String lastName, String email, String phoneNo, Date addDate, List<Address> addressArray, String addressBookName, String addressBookType) {
		this(firstName, lastName, email, phoneNo, addDate, addressArray);
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
