package dbms.table;

import java.util.ArrayList;
import java.util.HashMap;


public class User {
	public enum UserLevel {
	    LEVEL_A, LEVEL_B, LEVEL_ADMIN;
	}
	
	private String userName;
	private UserLevel userLevel;
	private HashMap<Table, ArrayList<String>> subschemaMap; // Key is Table, value is arraylist of column names in subschema

	public User(String userName, UserLevel level) {
		this.userName = userName;	
		this.userLevel = level;
		this.subschemaMap = new HashMap<Table, ArrayList<String>>();
	}

	/* Getters and Setters */
	public String getUserName() {return this.userName;}
	public UserLevel getUserLevel() {return this.userLevel;}
	public HashMap<Table, ArrayList<String>> getSubschemaMap() {return this.subschemaMap;}
	
	public void setUserName(String userName) {this.userName = userName;}
}