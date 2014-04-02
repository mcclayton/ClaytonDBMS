package dbms.parser;

import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.nodes.TWhereClause;

import java.util.ArrayList;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableColumn.DataType;
import dbms.table.constraints.CheckConstraintList.LOGICAL_OPERATOR;
import dbms.table.exceptions.SelectException;
import dbms.table.exceptions.UpdateException;


public class ParseWhereClause {

	public static String parseList(TWhereClause whereClause, Table parentTable) throws UpdateException {
		TSourceTokenList tokenList = new TSourceTokenList();
		whereClause.getCondition().addAllMyTokensToTokenList(tokenList, 0); // Put all tokens into list

		String fullConstraintExpression = "";
		LOGICAL_OPERATOR logicalOperator = LOGICAL_OPERATOR.NONE;
		String attributeOne = null;
		String attributeTwo = null;
		String operator = null;
		String constant = null;
		String logicalOperatorString = null;
		DataType constantType = DataType.UNKNOWN;

		ArrayList<TableColumn> columnList = parentTable.getTableColumns();
		ArrayList<String> columnNameList = new ArrayList<String>();
		// Add the parent column names to list
		for (TableColumn column : columnList) {
			columnNameList.add(column.getColumnName());
		}

		// Determine what kind of token each is
		for (int i=0; i < tokenList.size(); i++) {
			String token = tokenList.get(i).toString();

			if (token.matches("(?i)([ \t\r\n\f]*)and([ \t\r\n\f]*)")) {
				// Logical And
				if (logicalOperator == LOGICAL_OPERATOR.OR) {
					throw new UpdateException("Only one type of logical operator allowed per where clause constraint.", parentTable.getTableName());
				}
				logicalOperatorString = "&&";
				logicalOperator = LOGICAL_OPERATOR.AND;
			} else if (token.matches("(?i)([ \t\r\n\f]*)or([ \t\r\n\f]*)")) {
				// Logical Or
				if (logicalOperator == LOGICAL_OPERATOR.AND) {
					throw new UpdateException("Only one type of logical operator allowed per where clause constraint.", parentTable.getTableName());
				}
				logicalOperatorString = "||";
				logicalOperator = LOGICAL_OPERATOR.OR;
			} else if (token.matches("[0-9]+")) {
				// Constant Int
				constantType = DataType.INT;
				constant = token;
			} else if (isDouble(token)) {
				// Constant Decimal
				constantType = DataType.DECIMAL;
				constant = token;
			} else if (token.matches("[a-zA-Z0-9_]+")) {
				// Attribute Name
				if (attributeOne == null) {
					attributeOne = token;
					if (!columnNameList.contains(attributeOne)) {
						throw new UpdateException("Where clause places constraint on invalid attribute '"+attributeOne+"'.", parentTable.getTableName());
					}
				} else {
					// TODO: Check to see if attribute two is in this/a table?
					attributeTwo = token;
				}
			} else if (token.matches("'(.*?)'") || token.matches("\"(.*?)\"")) {
				// Constant String
				constantType = DataType.CHAR;
				constant = token;
			} else if (token.equals("=") || token.equals("!=") || token.equals(">") || token.equals("<") || token.equals(">=") || token.equals("<=")) {
				// Operator
				if (token.equals("=")) {
					operator = "==";
				} else {
					operator = token;
				}
			} else if (token.matches("([ \t\r\n\f]*)")) {
				// Skip white space
			} else {
				// Invalid token
				throw new UpdateException("Invalid token '"+token+"' in where clause.", parentTable.getTableName());
			}

			// Full expression found
			if (attributeOne != null && operator != null && (constant != null || attributeTwo != null)) {
				// Throw an exception if the constant type does not match the datatype of the parentColumn
				if (constant != null) {
					DataType parentColumnDataType = columnList.get(columnNameList.indexOf(attributeOne)).getAttributeDataType();
					if (parentColumnDataType != constantType) {
						throw new UpdateException("Where clause compares attribute '"+attributeOne+"' of datatype '"+parentColumnDataType.toString()+"' to constant of different type '"+constantType.toString()+"'.", parentTable.getTableName());
					}
				} else {
					// TODO: Check to make sure attributeOne and attributeTwo have same datatype
				}

				// Add to the clause string using the information
				if (logicalOperatorString != null) {
					fullConstraintExpression += " "+logicalOperatorString+" ";
				}

				if (constantType != null) {
					fullConstraintExpression += attributeOne+" "+operator+" "+constant;
				} else if (attributeTwo != null){
					fullConstraintExpression += attributeOne+" "+operator+" "+attributeTwo;
				}

				// Reset information
				attributeOne = null;
				attributeTwo = null;
				operator = null;
				constant = null;
				logicalOperatorString = null;
			}
		}
		// Throw an exception if there is a partial domain constraint.
		if (attributeOne != null || operator != null || constant != null || attributeTwo != null) {
			// This is really just added precaution. This should be handled as a syntax error by the sql parser.
			throw new UpdateException("Invalid where clause ("+whereClause.getCondition().toString()+").", parentTable.getTableName());
		}

		// Add ending semicolon to where clause expression
		fullConstraintExpression += ";";

		return fullConstraintExpression;
	}
	
	public static String parseList(TWhereClause whereClause, ArrayList<TableColumn> parentTableColumnns) throws SelectException {
		TSourceTokenList tokenList = new TSourceTokenList();
		whereClause.getCondition().addAllMyTokensToTokenList(tokenList, 0); // Put all tokens into list

		String fullConstraintExpression = "";
		LOGICAL_OPERATOR logicalOperator = LOGICAL_OPERATOR.NONE;
		String attributeOne = null;
		String attributeTwo = null;
		String operator = null;
		String constant = null;
		String logicalOperatorString = null;
		DataType constantType = DataType.UNKNOWN;

		ArrayList<TableColumn> columnList = parentTableColumnns;
		ArrayList<String> columnNameList = new ArrayList<String>();
		// Add the parent column names to list
		for (TableColumn column : columnList) {
			columnNameList.add(column.getColumnName());
		}

		// Determine what kind of token each is
		for (int i=0; i < tokenList.size(); i++) {
			String token = tokenList.get(i).toString();

			if (token.matches("(?i)([ \t\r\n\f]*)and([ \t\r\n\f]*)")) {
				// Logical And
				if (logicalOperator == LOGICAL_OPERATOR.OR) {
					throw new SelectException("Only one type of logical operator allowed per where clause constraint.");
				}
				logicalOperatorString = "&&";
				logicalOperator = LOGICAL_OPERATOR.AND;
			} else if (token.matches("(?i)([ \t\r\n\f]*)or([ \t\r\n\f]*)")) {
				// Logical Or
				if (logicalOperator == LOGICAL_OPERATOR.AND) {
					throw new SelectException("Only one type of logical operator allowed per where clause constraint.");
				}
				logicalOperatorString = "||";
				logicalOperator = LOGICAL_OPERATOR.OR;
			} else if (token.matches("[0-9]+")) {
				// Constant Int
				constantType = DataType.INT;
				constant = token;
			} else if (isDouble(token)) {
				// Constant Decimal
				constantType = DataType.DECIMAL;
				constant = token;
			} else if (token.matches("[a-zA-Z0-9_]+")) {
				// Attribute Name
				if (attributeOne == null) {
					attributeOne = token;
					if (!columnNameList.contains(attributeOne)) {
						throw new SelectException("Where clause places constraint on invalid attribute '"+attributeOne+"'.");
					}
				} else {
					// TODO: Check to see if attribute two is in this/a table?
					attributeTwo = token;
				}
			} else if (token.matches("'(.*?)'") || token.matches("\"(.*?)\"")) {
				// Constant String
				constantType = DataType.CHAR;
				constant = token;
			} else if (token.equals("=") || token.equals("!=") || token.equals(">") || token.equals("<") || token.equals(">=") || token.equals("<=")) {
				// Operator
				if (token.equals("=")) {
					operator = "==";
				} else {
					operator = token;
				}
			} else if (token.matches("([ \t\r\n\f]*)")) {
				// Skip white space
			} else {
				// Invalid token
				throw new SelectException("Invalid token '"+token+"' in where clause.");
			}

			// Full expression found
			if (attributeOne != null && operator != null && (constant != null || attributeTwo != null)) {
				// Throw an exception if the constant type does not match the datatype of the parentColumn
				if (constant != null) {
					DataType parentColumnDataType = columnList.get(columnNameList.indexOf(attributeOne)).getAttributeDataType();
					if (parentColumnDataType != constantType) {
						throw new SelectException("Where clause compares attribute '"+attributeOne+"' of datatype '"+parentColumnDataType.toString()+"' to constant of different type '"+constantType.toString()+"'.");
					}
				} else {
					// TODO: Check to make sure attributeOne and attributeTwo have same datatype
				}

				// Add to the clause string using the information
				if (logicalOperatorString != null) {
					fullConstraintExpression += " "+logicalOperatorString+" ";
				}

				if (constantType != null) {
					fullConstraintExpression += attributeOne+" "+operator+" "+constant;
				} else if (attributeTwo != null){
					fullConstraintExpression += attributeOne+" "+operator+" "+attributeTwo;
				}

				// Reset information
				attributeOne = null;
				attributeTwo = null;
				operator = null;
				constant = null;
				logicalOperatorString = null;
			}
		}
		// Throw an exception if there is a partial domain constraint.
		if (attributeOne != null || operator != null || constant != null || attributeTwo != null) {
			// This is really just added precaution. This should be handled as a syntax error by the sql parser.
			throw new SelectException("Invalid where clause ("+whereClause.getCondition().toString()+").");
		}

		// Add ending semicolon to where clause expression
		fullConstraintExpression += ";";

		return fullConstraintExpression;
	}

	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}


	/*
	 * Returns a list of the attribute names found inside the where clause
	 */
	public static ArrayList<String> getAttributeNames(TWhereClause whereClause, ArrayList<Table> tablesInFromClause) throws Exception {
		TSourceTokenList tokenList = new TSourceTokenList();
		whereClause.getCondition().addAllMyTokensToTokenList(tokenList, 0); // Put all tokens into list

		ArrayList<String> columnNames = new ArrayList<String>();

		// Find the attributes and add their names to the array list
		for (int i=0; i < tokenList.size(); i++) {
			String token = tokenList.get(i).toString();

			if (token.matches("(?i)([ \t\r\n\f]*)and([ \t\r\n\f]*)")) {
				// Skip non-attribute token
			} else if (token.matches("(?i)([ \t\r\n\f]*)or([ \t\r\n\f]*)")) {
				// Skip non-attribute token
			} else if (token.matches("[0-9]+")) {
				// Skip non-attribute token
			} else if (isDouble(token)) {
				// Skip non-attribute token
			} else if (token.matches("[a-zA-Z0-9_]+")) {
				// Attribute Name
				boolean validColumn = false;
				for (Table table : tablesInFromClause) {
					for (TableColumn column : table.getTableColumns()) {
						if (column.getColumnName().equals(token)) {
							validColumn = true;
						}
					}
				}
				if (!validColumn) {
					throw new Exception("Invalid column '"+token+"' in statement.");
				}
				columnNames.add(token);
			} else {
				// Skip non-attribute token
			}

		}

		return columnNames;
	}

}