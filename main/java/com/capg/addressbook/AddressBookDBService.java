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

	private PreparedStatement addressBookPreparedStatement;
	private static AddressBookDBService addressBookDBService;

	public AddressBookDBService() {
		// Does not make AddressBookDBService obj
	}

	public static AddressBookDBService getInstance() {
		if (addressBookDBService == null)
			addressBookDBService = new AddressBookDBService();
		return addressBookDBService;
	}

	public List<Contact> readAddressBookDB() {
		String sql = "select c.firstName, c.lastName, a.address, a.city, a.state, a.zip, c.Phone_Number, c.Email, b.name, b.type "
				+ "from address a, contact c, book b WHERE c.book_name = b.name AND a.id = c.id;";
		return this.getAddressBookDataAfterExecutingQuery(sql);
	}
	
	public int updateContactData(String firstName, String lastName, String phoneNumber) {
		String sql = String.format("UPDATE contact SET Phone_Number = '%s' WHERE firstName = '%s' && lastName = '%s';", phoneNumber, firstName, lastName);
		try (Connection connection = this.getConnection();
				Statement statement = connection.createStatement();) {
				return statement.executeUpdate(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return 0;
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
				String phoneNumber = resultSet.getString("Phone_Number");
				String email = resultSet.getString("Email");
				String bookName = resultSet.getString("name");
				String bookType = resultSet.getString("type");
				Contact contact = new Contact(firstName, lastName, email, phoneNumber);
				Address addressObj = new Address(address, city, state, zip);
				List<Address> addressList = new ArrayList<>();
				if (contactList.contains(contact)) {
					contact.addressList.add(addressObj);
				} else {
					addressList.add(addressObj);
					contactList.add(new Contact(firstName, lastName, phoneNumber, email, addressList, bookName, bookType));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return contactList;
	}

	public List<Contact> getContactData(String firstName, String lastName) {
		List<Contact> employeePayrollList = null;
		if(this.addressBookPreparedStatement == null)
			this.prepareStatementForAddressBook();
		try {
			addressBookPreparedStatement.setString(1, firstName);
			addressBookPreparedStatement.setString(2, lastName);
			ResultSet resultSet = addressBookPreparedStatement.executeQuery();
			employeePayrollList = this.getAddressBookData(resultSet);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private void prepareStatementForAddressBook() {
		try {
			Connection connection = this.getConnection();
			String sql = "select c.firstName, c.lastName, a.address, a.city, a.state, a.zip, c.Phone_Number, c.Email, b.name, b.type "
					+ "from address a, contact c, book b "
					+ "WHERE c.book_name = b.name AND a.id = c.id AND firstName = ? AND lastName = ?;";
			addressBookPreparedStatement = connection.prepareStatement(sql);
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
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
