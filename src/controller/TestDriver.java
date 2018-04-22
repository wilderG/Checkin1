package controller;

import model.Auction;
import model.User;

public class TestDriver {

	public static Storage storage;
	
	public static void main(String[] args) {
		storage = new Storage();
		
		
		/* to create a user and auction
		   uncomment and add auction name and username */
		
		//Auction auction = new Auction("Test Auction 1");
		//User user = new User("tester", auction);
		//addUser(user, auction);
		
		
		/* to load a user and print auction
		   uncomment and change parameter to username */
		
		testLoad("tester");
		//testLoad("admin");
	
	}
	
	//add user and auction to storage
	public static void addUser(final User user, final Auction auction) {
		storage.storeAuction(auction);
		storage.storeUser(user);
		storage.writeData();
		
		System.out.println("Added auction: " + auction); 
		System.out.println("Added user: " + user);
	}

	public static void testLoad(String theUserName) {
		//this entry is saved in the file
		User user = storage.getUser(theUserName);
		
		//get auction from storage using key
		System.out.println(user);
	}
}