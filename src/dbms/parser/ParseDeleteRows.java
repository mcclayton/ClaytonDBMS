package dbms.parser;

import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;

import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableManager;
import dbms.table.TableRow;
import dbms.table.exceptions.DeleteRowsException;


public class ParseDeleteRows {

	/* 
	 * Deletes rows from an existing table from parsing an Delete Statement.
	 * 
	 * If successful, deletes rows from the specified table in the TABLE_MAP
	 * If unsuccessful, throws an exception and rows are not deleted.
	 */

	protected static void deleteRowsFromStatement(TDeleteSqlStatement pStmt, TableManager tableManager) throws DeleteRowsException, Exception {
		int rowsAffected = 0;

		String parentTableName = null;
		if (pStmt.getTargetTable() != null) {
			parentTableName = pStmt.getTargetTable().toString();
			if (!tableManager.tableExists(parentTableName)) {
				throw new DeleteRowsException("Table does not exist.", parentTableName);
			}
		}

		// Get the table object whose rows are being deleted
		Table parentTable = tableManager.getTable(parentTableName);

		// Parse the where clause and delete the matched rows
		String expression = null;
		if (pStmt.getWhereClause() != null) {
			expression = ParseWhereClause.parseList(pStmt.getWhereClause(), parentTable);
		} else {
			// Delete all rows
			expression = "true;";
		}
		// Delete the rows that match the expression and get the number of rows affected
		rowsAffected = deleteRows(parentTable, expression);

		// Delete command successful
		System.out.println(rowsAffected+" row(s) affected.");
	}


	/*
	 * Removes the rows that pass the where clause constraint.
	 * 
	 * Returns the number of rows affected
	 */
	public static int deleteRows(Table parentTable, String whereClause) throws Exception {
		int rowsAffected = 0;
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");

		ArrayList<TableColumn> parentTableColumns = parentTable.getTableColumns();
		ArrayList<TableRow> rowList = parentTable.getTableRows();
		ArrayList<TableRow> rowsToRemove = new ArrayList<TableRow>();
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
					// Keep track of the rows that will be deleted
					rowsToRemove.add(rowList.get(rowIndex));					
					rowsAffected++;
				}
			} catch (ScriptException e) {
				throw new Exception("Invalid where clause '"+expression+"'.");
			}
		}
		
		// Remove the rows now
		for (TableRow row : rowsToRemove) {
			if (rowList.contains(row)) {
				rowList.remove(rowList.indexOf(row));
			}
		}
		return rowsAffected;
	}
}