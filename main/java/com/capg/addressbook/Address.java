package com.capg.addressbook;

public class Address {
	private String address, city, state;
	private int zip;

	public Address(String address, String city, String state, int zip) {
		this.address = address;
		this.city = city;
		this.state = state;
		this.zip = zip;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public int getZip() {
		return zip;
	}
}
