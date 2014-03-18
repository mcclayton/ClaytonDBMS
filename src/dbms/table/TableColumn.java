package dbms.table;

import java.util.ArrayList;

public class TableColumn {
	private static final int MAX_ATTRIBUTE_NAME_LENGTH = 256;

	private String relationName;
	private String attributeName;
	private String attributeType;
	private ArrayList<String> constraintList;	// TODO: Need to make this a valid constraint system

	public TableColumn(String relationName, String attributeName, String attributeType, ArrayList<String> constraintList) {
		this.relationName = relationName;
		this.attributeName = attributeName;
		this.attributeType = attributeType;

		if (!isValidAttributeName(attributeName)) {
			// TODO: Throw an exception
			System.out.println("Error: Invalid attribute name.");
		}
		if (relationName == null) {
			// TODO: Throw an exception
			System.out.println("Error: No table name specified.");
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

	private boolean isValidAttributeName(String attributeName) {
		if (attributeName == null) {
			return false;
		}
		if (!attributeName.matches("[a-zA-Z0-9_]+")) {
			return false;
		}
		if (attributeName.length() > MAX_ATTRIBUTE_NAME_LENGTH) {
			return false;
		}
		return true;
	}
	
	private int getAttributeType(String attributeString) {
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
	public ArrayList<String> getConstraintList() {return this.constraintList;}
}