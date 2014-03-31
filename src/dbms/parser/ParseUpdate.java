package dbms.parser;

import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;

import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableColumn.DataType;
import dbms.table.TableManager;
import dbms.table.TableRow;
import dbms.table.constraints.ConstraintVerifier;
import dbms.table.exceptions.UpdateException;


public class ParseUpdate {

	/* 
	 * Updates values in an existing table from parsing an Update Statement.
	 * 
	 * If successful, updates values in the specified table in the TABLE_MAP
	 * If unsuccessful, throws an exception and values are not updated.
	 */

	protected static void updateValuesFromStatement(TUpdateSqlStatement pStmt) throws UpdateException, Exception {
		int rowsAffected = 0;

		String parentTableName = null;
		if (pStmt.getTargetTable() != null) {
			parentTableName = pStmt.getTargetTable().toString();
			if (!TableManager.tableExists(parentTableName)) {
				throw new UpdateException("Table does not exist.", parentTableName);
			}
		}

		// Get the table object being updated
		Table parentTable = TableManager.getTable(parentTableName);

		// Get the column being updated and the value it's being updated with, check to make sure the value passes all constraints
		TableColumn column = null;
		ArrayList<TableColumn> parentColumnList = new ArrayList<TableColumn>();
		
		ArrayList<TableColumn> columnsBeingUpdated = new ArrayList<TableColumn>();	// The columns that will be updated
		ArrayList<String> valuesToUpdateWith = new ArrayList<String>();				// The values that columnsBeingUpdated will be updated with
		
		for(int i=0; i<pStmt.getResultColumnList().size(); i++) {
			column = null;
			TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);
			TExpression expression = resultColumn.getExpr();

			// Get the column of table being updated
			column = TableManager.getTableColumnByName(parentTableName, expression.getLeftOperand().toString());
			if (column == null) {
				throw new UpdateException("Trying to update value(s) of invalid column '"+expression.getLeftOperand().toString()+"'.", parentTableName);
			}

			parentColumnList.add(column);

			String value = expression.getRightOperand().toString();

			// Check to make sure the value that the column is being updated with passes all constraints and is valid 	
			if (!passesAllConstraints(parentTableName, column, value)) {
				// passesAllConstraints should throw an exception if it doesn't pass, this is just an added precaution.
				throw new UpdateException("Value '"+value+"' violates a domain constraint.", parentTableName);
			}
			
			// If type of value is CHAR, remove the quotes before using it
			if (ConstraintVerifier.getValueDataType(value) == DataType.CHAR) {
				// Remove quotes from value
				if (value.startsWith("'")) {
					value = value.replace("'", "");
				} else if (value.startsWith("\"")) {
					value = value.replace("\"", "");
				}
			}

			// Now that we know the value is valid, store the column and value somewhere so we can add them later after we have parsed the where clause
			columnsBeingUpdated.add(column);
			valuesToUpdateWith.add(value);
		}

		// Parse the where clause and update the matched rows with the new column values
		String expression = null;
		if (pStmt.getWhereClause() != null) {
			expression = ParseWhereClause.parseList(pStmt.getWhereClause(), parentTable);
		} else {
			// Update all rows
			expression = "true;";
		}
		// Update the values of rows that match the expression and get the number of rows affected
		rowsAffected = updateValues(parentTable, expression, columnsBeingUpdated, valuesToUpdateWith);

		// Update command successful
		System.out.println(rowsAffected+" row(s) affected.");
	}


	public static boolean passesAllConstraints(String tableName, TableColumn column, String value) throws UpdateException, Exception {
		// Check to make sure the value that the column is being updated with passes all constraints and is valid 			
		if (value == null) {
			throw new UpdateException("Trying to update column '"+column.getColumnName()+"' with invalid value.", tableName);
		}

		// Check to make sure that the datatypes of the values and the columns they are being inserted into have the same datatype
		if (ConstraintVerifier.getValueDataType(value) != column.getAttributeDataType()) {
			if (ConstraintVerifier.getValueDataType(value) == DataType.CHAR) {
				throw new UpdateException("Value "+value+" has different datatype than attribute '"+column.getColumnName()+"'.", tableName);
			} else {
				throw new UpdateException("Value '"+value+"' has different datatype than attribute '"+column.getColumnName()+"'.", tableName);
			}
		}

		// If type of value is CHAR, ensure the length constraint is not violated
		if (ConstraintVerifier.getValueDataType(value) == DataType.CHAR) {
			// Remove quotes from value
			if (value.startsWith("'")) {
				value = value.replace("'", "");
			} else if (value.startsWith("\"")) {
				value = value.replace("\"", "");
			}
			if (value.length() > column.getVarCharLength()) {
				throw new UpdateException("Trying to update column '"+column.getColumnName()+"' with value that violates 'CHAR("+column.getVarCharLength()+")' length constraint.", tableName);
			}
		}

		// Check domain constrains
		if (column.getCheckConstraintList() != null) {
			try {
				if (!ConstraintVerifier.passesCheckConstraints(value, ConstraintVerifier.getValueDataType("'"+value+"'"), column.getCheckConstraintList())) {
					throw new UpdateException("Value '"+value+"' violates a domain constraint.", tableName);
				}
			} catch (ScriptException e) {
				throw new UpdateException("Value '"+value+"' violates a domain constraint.", tableName);
			}
		}
		return true;
	}


	/*
	 * Updates the column values if they pass the where clause constraint.
	 * 
	 * Returns the number of rows affected
	 */
	public static int updateValues(Table tableBeingUpdated, String whereClause, ArrayList<TableColumn> columnsBeingUpdated, ArrayList<String> values) throws Exception {
		int rowsAffected = 0;
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");

		ArrayList<TableColumn> parentTableColumns = tableBeingUpdated.getTableColumns();
		ArrayList<TableRow> rowList = tableBeingUpdated.getTableRows();
		String expression = null;
		for (int rowIndex=0; rowIndex <  rowList.size(); rowIndex++) {
			expression = whereClause;
			// Replace all column names with values from that column for the given row index
			for (int columnIndex = 0; columnIndex < parentTableColumns.size(); columnIndex++) {
				if (rowList.get(rowIndex).getElement(columnIndex) instanceof String) {
					expression = expression.replace(parentTableColumns.get(columnIndex).getColumnName(), "'"+rowList.get(rowIndex).getElement(columnIndex)+"'");
				} else {
					expression = expression.replace(parentTableColumns.get(columnIndex).getColumnName(), (String) rowList.get(rowIndex).getElement(columnIndex));
				}
			}
			try {
				if (engine.eval(expression).toString().equals("true")) {
					// Update the values in the matched row
					int indexOfUpdatingColumn = 0;
					for (int columnIndex = 0; columnIndex < columnsBeingUpdated.size(); columnIndex++) {
						indexOfUpdatingColumn = parentTableColumns.indexOf(columnsBeingUpdated.get(columnIndex));
						// Update the value of the current matched row at columnsBeingUpdated with the new values
						//TODO: if the value being updated is of type CHAR, remove the quotes when updating
						rowList.get(rowIndex).setElement(indexOfUpdatingColumn, values.get(columnIndex));
					}
					rowsAffected++;
				}
			} catch (ScriptException e) {
				throw new Exception("Invalid where clause '"+expression+"'.");
			}
		}
		
		return rowsAffected;
	}
}