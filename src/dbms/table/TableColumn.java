package dbms.table;

import dbms.table.constraints.NameValidation;
import dbms.table.exceptions.AttributeException;
import dbms.table.exceptions.CreateTableException;


public class TableColumn {
	
	
	private String relationName;
	private String attributeName;
	private String attributeType;
	private String checkConstraint;	// TODO: Need to make this a valid constraint expression and validate the expression
	
	/* Column Constraints */
	String checkExpression = null;								// Domain constraint for attribute

	public TableColumn(String relationName, String attributeName, String attributeType, String checkConstraint) throws CreateTableException, AttributeException {
		this.relationName = relationName;
		this.attributeName = attributeName;
		this.attributeType = attributeType;
		this.checkConstraint = checkConstraint;

		if (!NameValidation.isValidAlphaNumUnderscoreName(attributeName)) {
			throw new AttributeException("Invalid attribute name '"+attributeName+"'.");
		}
		if (!NameValidation.isValidNameLength(attributeName)) {
			throw new AttributeException("Invalid attribute name length '"+attributeName+"'.");
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