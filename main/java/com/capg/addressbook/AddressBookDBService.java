package com.capg.addressbook;

import java.sql.Connection;
import java.sql.Date;
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

	public enum Column {
		CITY, STATE;
	}

	private static Logger log = Logger.getLogger(AddressBookDBService.class.getName());

	private PreparedStatement addressBookPreparedStatement;
	private static AddressBookDBService addressBookDBService;

	public AddressBookDBService() {
		// Does not let other classes make AddressBookDBService object
	}

	public static AddressBookDBService getInstance() {
		if (addressBookDBService == null)
			addressBookDBService = new AddressBookDBService();
		return addressBookDBService;
	}

	public List<Contact> readAddressBookDB() {
		String sql = "select c.FirstName, c.LastName, a.address, a.city, a.state, a.ZIP, c.Phone_Number, c.Email, b.name, b.type, c.date "
				+ "from address a, contact c, book b WHERE c.book_name = b.name AND a.id = c.id;";
		return this.getAddressBookDataAfterExecutingQuery(sql);
	}

	public int updateContactData(String firstName, String lastName, String phoneNumber) {
		String sql = String.format("UPDATE contact SET Phone_Number = '%s' WHERE firstName = '%s' && lastName = '%s';",
				phoneNumber, firstName, lastName);
		try (Connection connection = this.getConnection(); Statement statement = connection.createStatement();) {
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<Contact> getContactsInDateRange(Date startDate, Date endDate) {
		String sql = String.format(
				"select c.FirstName, c.LastName, a.address, a.city, a.state, a.ZIP, c.Phone_Number, c.Email, b.name, b.type, c.date "
						+ "from address a, contact c, book b " + "WHERE c.book_name = b.name AND a.id = c.id "
						+ "AND date BETWEEN CAST('%s' AS DATE) AND CAST('%s' AS DATE)",
				startDate, endDate);
		return getAddressBookDataAfterExecutingQuery(sql);
	}

	public int getCountOfContactsFromCityOrState(Column columnName, String value) throws AddressBookSystemException {
		String column = null;
		if (columnName == Column.CITY)
			column = "city";
		else if (columnName == Column.STATE)
			column = "state";
		String sql = String.format("select COUNT(c.firstName) as No_Of_Contacts " + "from address a, contact c, book b " 
									+ "WHERE c.book_name = b.name AND a.id = c.id "
									+ "AND %s = '%s';", column, value);
		try (Connection connection = this.getConnection(); 
				Statement statement = connection.createStatement();) {
			ResultSet resultSet = statement.executeQuery(sql);
			int noOfContacts = -1;
			while(resultSet.next())
				noOfContacts = resultSet.getInt("No_Of_Contacts");
			return noOfContacts;
		} catch (SQLException e) {
			throw new AddressBookSystemException("Exception occurred.");
		}
	}
	
	public Contact addContactToAddressBookDB(String firstName, String lastName, String email, String phNo, Date date,
			String address, String city, String state, int zip, String bookName, String bookType) throws AddressBookSystemException {
		int id = -1;
		Contact contact = null;
		Connection connection = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		}
		catch (SQLException e) {
			throw new AddressBookSystemException("Couldn't establish connection.");
		}
		
		try (Statement statement = connection.createStatement();) {
			String sql1 = String.format("INSERT INTO book (name, type) VALUES ('%s', '%s');", bookName, bookType);
			statement.executeUpdate(sql1);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new AddressBookSystemException("Unable to insert into book.");
		}
		
		try (Statement statement = connection.createStatement();) {
			String sql2 = String.format("INSERT INTO contact (FirstName, LastName, Email, Phone_Number, date, book_name)"
										+" VALUES ('%s', '%s', '%s', '%s', '%s', '%s');"
										, firstName, lastName, email, phNo, date, bookName);
			int rowsAffected = statement.executeUpdate(sql2);
			if(rowsAffected == 1) {
				String sql = String.format("select c.id from contact c WHERE "
								+ "FirstName = '%s' AND LastName = '%s';", firstName, lastName);
				ResultSet resultSet = statement.executeQuery(sql);
				if(resultSet.next())
					id = resultSet.getInt("id");
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new AddressBookSystemException("Unable to insert into contact.");
		}
		
		try (Statement statement = connection.createStatement();) {
			String sql3 = String.format("INSERT INTO address (id, address, city, state, ZIP)"
										+ " VALUES ('%s', '%s', '%s', '%s', %s);"
										,id, address, city, state, zip);
			List<Address> addressArray = new ArrayList<>();
			addressArray.add(new Address(address, city, state, zip));
			int rowsAffected = statement.executeUpdate(sql3);
			if(rowsAffected == 1) {
				contact = new Contact(firstName, lastName, email, phNo, date, addressArray, bookName, bookType);
				connection.commit();
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new AddressBookSystemException("Unable to insert into address.");
		} finally {
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return contact;
	}

	private List<Contact> getAddressBookDataAfterExecutingQuery(String sql) {
		List<Contact> contactList = new ArrayList<>();
		try (Connection connection = this.getConnection(); 
				Statement statement = connection.createStatement();) {
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
				String firstName = resultSet.getString("FirstName");
				String lastName = resultSet.getString("LastName");
				String email = resultSet.getString("Email");
				String phoneNumber = resultSet.getString("Phone_Number");
				Date addDate = resultSet.getDate("date");
				String address = resultSet.getString("address");
				String city = resultSet.getString("city");
				String state = resultSet.getString("state");
				int zip = resultSet.getInt("ZIP");
				String bookName = resultSet.getString("name");
				String bookType = resultSet.getString("type");
				Contact contact = new Contact(firstName, lastName, email, phoneNumber, addDate);
				Address addressObj = new Address(address, city, state, zip);
				List<Address> addList = new ArrayList<>();
				if (contactList.contains(contact)) {
					contact.addressList.add(addressObj);
				} else {
					addList.add(addressObj);
					contactList.add(new Contact(firstName, lastName, email, phoneNumber, addDate, addList, bookName, bookType));
				}
			}
		} catch (SQLException e) {
			System.out.println("getAddressBookData exception.");
			e.printStackTrace();
		}
		return contactList;
	}

	public List<Contact> getContactData(String firstName, String lastName) {
		List<Contact> employeePayrollList = null;
		if (this.addressBookPreparedStatement == null)
			this.prepareStatementForAddressBook();
		try {
			addressBookPreparedStatement.setString(1, firstName);
			addressBookPreparedStatement.setString(2, lastName);
			ResultSet resultSet = addressBookPreparedStatement.executeQuery();
			employeePayrollList = this.getAddressBookData(resultSet);
		} catch (SQLException e) {
			System.out.println("getContactData() exception.");
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private void prepareStatementForAddressBook() {
		try {
			Connection connection = this.getConnection();
			String sql = "select c.FirstName, c.LastName, a.address, a.city, a.state, a.ZIP, c.Phone_Number, c.Email, b.name, b.type, c.date "
					+ "from address a, contact c, book b "
					+ "WHERE c.book_name = b.name AND a.id = c.id AND firstName = ? AND lastName = ?;";
			addressBookPreparedStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			System.out.println("Couldn't prepare prepared statement.");
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
