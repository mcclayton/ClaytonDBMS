package dbms.table;

import dbms.table.exceptions.AttributeException;
import dbms.table.exceptions.CreateTableException;


public class TableColumn {
	
	
	private String relationName;
	private String attributeName;
	private String attributeType;
	private String checkConstraint;	// TODO: Need to make this a valid constraint expression and validate the expression
	
	/* Column Constraints */
	String checkExpression = null;								// Domain constraint for attribute
	private static final int MAX_NAME_LENGTH = 256;	// Limit the name size of the attribute

	public TableColumn(String relationName, String attributeName, String attributeType, String checkConstraint) throws CreateTableException, AttributeException {
		this.relationName = relationName;
		this.attributeName = attributeName;
		this.attributeType = attributeType;
		this.checkConstraint = checkConstraint;

		if (!isValidAlphaNumUnderscoreName(attributeName)) {
			throw new AttributeException("Invalid attribute name '"+attributeName+"'.");
		}
		if (relationName == null) {
			throw new CreateTableException("No relation name specified.");
		}
		
		// TODO: Handle the attribute type
		switch (getAttributeType(attributeType)) {
		case 0:
			break;
		case 1:
			break;
		case 2:
			break;
		}
	}	

	/*
	 * Checks to see if name is alpha numeric, allows underscores, and ensures the name is less than MAX_NAME_LENGTH characters
	 */
	private boolean isValidAlphaNumUnderscoreName(String attributeName) {
		if (attributeName == null) {
			return false;
		}
		if (!attributeName.matches("[a-zA-Z0-9_]+")) {
			return false;
		}
		if (attributeName.length() > MAX_NAME_LENGTH) {
			return false;
		}
		return true;
	}
	
	private int getAttributeType(String attributeString) {
		// TODO: Perhaps I need to remove spaces
		String nameLower = attributeString.toLowerCase();
		if (nameLower.equals("int")) {
			return 0;
		} else if (nameLower.equals("decimal")) {
			return 1;
		} else if (nameLower.equals("char()")) {	// TODO: Check with regex
			return 2;
		} else {
			return 3;
		}
	}
	
	
	/* Getters and Setters */
	public String getTableName() {return this.relationName;}
	public String getColumnName() {return this.attributeName;}
	public String getAttributeType() {return this.attributeType;}
	public String getCheckConstraint() {return this.checkConstraint;}
}