package dbms.table;

import java.util.Scanner;

import dbms.table.constraints.NameValidation;
import dbms.table.exceptions.AttributeException;
import dbms.table.exceptions.CreateTableException;


public class TableColumn {

	public enum DataType {
	    INT, DECIMAL, VARCHAR, UNKNOWN
	}
	private String relationName;
	private String attributeName;
	private String attributeTypeString;
	private String checkConstraint;	// TODO: Need to make this a valid constraint expression and validate the expression

	/* Column Constraints */
	String checkExpression = null;	// Domain constraint for attribute
	DataType attributeDataType = null;		// The type of attribute
	int	varCharLength = 0;			// Length constraint if type is of varchar()

	public TableColumn(String relationName, String attributeName, String attributeTypeString, String checkConstraint) throws CreateTableException, AttributeException {
		this.relationName = relationName;
		this.attributeName = attributeName;
		this.attributeTypeString = attributeTypeString;
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
		switch (getAttributeType(attributeTypeString)) {
		case INT:
			attributeDataType = DataType.INT;
			break;
		case DECIMAL:
			attributeDataType = DataType.DECIMAL;
			break;
		case VARCHAR:
			attributeDataType = DataType.VARCHAR;
			Scanner scanner = new Scanner(attributeTypeString).useDelimiter("[^0-9]+");
	        varCharLength = scanner.nextInt();
			break;
		default:
			throw new AttributeException("Invalid attribute datatype '"+attributeTypeString+"'.");
		}
	}	

	/*
	 * Use regex to find out what data type an attribute is.
	 */
	private DataType getAttributeType(String attributeString) {
		// TODO: Perhaps I need to remove spaces
		String nameLower = attributeString.toLowerCase();
		if (nameLower.matches("([ \t]*)int([ \t]*)")) {
			return DataType.INT;
		} else if (nameLower.matches("([ \t]*)decimal([ \t]*)")) {
			return DataType.DECIMAL;
		} else if (nameLower.matches("([ \t]*)varchar\\(([ \t]*)([0-9]{1,5})([ \t]*)\\)([ \t]*)")) { // TODO: Check with regex
			return DataType.VARCHAR;
		} else {
			return DataType.UNKNOWN;
		}
	}


	/* Getters and Setters */
	public String getTableName() {return this.relationName;}
	public String getColumnName() {return this.attributeName;}
	public DataType getAttributeDataType() {return this.attributeDataType;}
	public String getCheckConstraint() {return this.checkConstraint;}
	public int getVarCharLength() {return this.varCharLength;}
}