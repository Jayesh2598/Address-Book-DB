package com.capg.addressbook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddressBookDBService {

	private static Logger log = Logger.getLogger(AddressBookDBService.class.getName());

	private PreparedStatement employeePayrollDataStatement;
	private static AddressBookDBService addressBookDBService;

	public AddressBookDBService() {
	}

	public static AddressBookDBService getInstance() {
		if (addressBookDBService == null)
			addressBookDBService = new AddressBookDBService();
		return addressBookDBService;
	}

	public List<Contact> readAddressBookDB() {
		String sql = "select c.firstName, c.lastName, a.address, a.city, a.state, a.zip, c.Phone_Number, c.Email, b.name, b.type "
				+ "FROM contact c, book b, address a " + "WHERE c.book_name = b.name AND a.id = c.id;";
		return this.getAddressBookDataAfterExecutingQuery(sql);
	}

	private List<Contact> getAddressBookDataAfterExecutingQuery(String sql) {
		List<Contact> contactList = new ArrayList<>();
		try (Connection connection = this.getConnection(); Statement statement = connection.createStatement();) {
			ResultSet resultSet = statement.executeQuery(sql);
			contactList = this.getAddressBookData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactList;
	}

	private List<Contact> getAddressBookData(ResultSet resultSet) {
		List<Contact> contactList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				String firstName = resultSet.getString("firstName");
				String lastName = resultSet.getString("lastName");
				String address = resultSet.getString("address");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				int zip = resultSet.getInt("zip");
				String phone_Number = resultSet.getString("Phone_Number");
				String email = resultSet.getString("Email");
				String book_Name = resultSet.getString("name");
				String book_Type = resultSet.getString("type");
				contactList.add(new Contact(firstName, lastName, address, city, state, zip, phone_Number, email,
						book_Name, book_Type));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactList;
	}

	private Connection getConnection() throws SQLException {
		String dbURL = "jdbc:mysql://localhost:3306/address_book_service?useSSL=false";
		String userName = "root";
		String password = "Interference@SQL1";
		Connection connection;
		log.log(Level.INFO, () -> "Connecting to database : " + dbURL);
		connection = DriverManager.getConnection(dbURL, userName, password);
		log.log(Level.INFO, () -> "Connection Successful : " + connection);
		return connection;
	}
}
