package dbms.parser;

import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.util.ArrayList;
import java.util.HashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableManager;
import dbms.table.TableRow;
import dbms.table.exceptions.SelectException;


public class ParseSelect {

	// TODO: Add multiple threads perhaps when crossing > 2 tables.

	/* 
	 * Displays the tuples from the result set that is produced from parsing a Select Statement.
	 * 
	 * If successful, displays the tuples from the result set
	 * If unsuccessful, throws an exception and result set is not displayed
	 */

	protected static void parseAndPrintSelect(TSelectSqlStatement pStmt, TableManager tableManager) throws SelectException {
		// Make sure syntax of select statement is correct
		veryifySyntax(pStmt);

		// Get the tables from the FROM clause
		ArrayList<Table> tablesInFromClause = new ArrayList<Table>();
		for(int i=0; i<pStmt.joins.size(); i++) {
			TJoin join = pStmt.joins.getJoin(i);
			switch (join.getKind()) {
			case TBaseType.join_source_fake:
				// Valid Implicit Join
				if (join.getTable().getAliasClause() != null) {
					throw new SelectException("Aliases are not supported.");
				}

				if (tableManager.tableExists(join.getTable().toString())) {
					tablesInFromClause.add(tableManager.getTable(join.getTable().toString()));
				} else {
					throw new SelectException("Table '"+join.getTable().toString()+"' in FROM clause does not exist.");
				}
				break;
			default:
				// Invalid Explicit Join
				throw new SelectException("Explicit joins are not supported.");
			}
		}

		// Select list
		ArrayList<String> projectionColumns = new ArrayList<String>();	// The names of all columns that will be in the final result
		boolean asteriskFlag = false;
		boolean validColumn;
		String columnNameInSelect;
		for(int i=0; i < pStmt.getResultColumnList().size(); i++) {
			TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);

			// Don't allow aliases
			if (resultColumn.getAliasClause() != null) {
				throw new SelectException("Aliases are not supported.");
			}

			// Add the names of the columns that will be projected to a list
			if (resultColumn.getExpr().toString().equals("*")) {
				if (!projectionColumns.isEmpty()) {
					throw new SelectException("Invalid attribute '*'.");
				}
				asteriskFlag = true;
			} else if (!projectionColumns.contains(resultColumn.getExpr().toString())) {
				if (asteriskFlag) {
					throw new SelectException("Invalid select statement.");
				}
				// Ensure that the below column exists in one of the tables
				validColumn = false;
				columnNameInSelect = resultColumn.getExpr().toString();
				for (Table table : tablesInFromClause) {
					if (table.getTableColumnByName(columnNameInSelect) != null) {
						projectionColumns.add(columnNameInSelect);
						validColumn = true;
					}
				}
				if (!validColumn) {
					throw new SelectException("Invalid attribute '"+columnNameInSelect+"'.");
				}
			}
		}

		// Get the columns from the WHERE clause
		ArrayList<String> columnNamesToCross = new ArrayList<String>();	// The names of all the columns that will be involved in the cross product
		// If pStmt.getWhereClause() == null, handle this by making sure ALL columns are crossed
		if (pStmt.getWhereClause() != null) {
			try {
				columnNamesToCross = ParseWhereClause.getAttributeNames(pStmt.getWhereClause(), tablesInFromClause);
			} catch (Exception e) {
				throw new SelectException(e.getMessage());
			}
		} else {
			for (Table table : tablesInFromClause) {
				for (TableColumn column : table.getTableColumns()) {
					if (!columnNamesToCross.contains(column.getColumnName())) {
						columnNamesToCross.add(column.getColumnName());
					}
				}
			}
		}


		// Add the projected columns to columnNamesToCross if they aren't already in there
		for (String columnName : projectionColumns) {
			if (!columnNamesToCross.contains(columnName)) {
				columnNamesToCross.add(columnName);
			}
		}

		// Find out which columns go in which table and put them into a hashmap to keep track of them.
		HashMap<Table, ArrayList<TableColumn>> columnHashMap = new HashMap<Table, ArrayList<TableColumn>>();	// Used to keep track of which tables columns belong to.
		for (Table table : tablesInFromClause) {
			columnHashMap.put(table, table.getTableColumns());
		}

		// Get all of the columns from the select statement
		ArrayList<TableColumn> allTableColumns = new ArrayList<TableColumn>();
		for (Table table : columnHashMap.keySet()) {
			for (TableColumn column : columnHashMap.get(table)) {
				allTableColumns.add(column);
			}
		}

		// Take the cartesian product of each of the tables.
		ArrayList<TableRow> masterRowList = getCrossProductOfAllTables(columnHashMap);

		// Get the where clause expression
		String expression = null;
		if (pStmt.getWhereClause() != null) {
			expression = ParseWhereClause.parseList(pStmt.getWhereClause(), allTableColumns);
		} else {
			expression = "true;";
		}		


		// Print each of the selected rows that pass the where clause
		try {
			printSelectRows(masterRowList, allTableColumns, projectionColumns, expression, asteriskFlag);
		} catch (Exception e) {
			throw new SelectException(e.getMessage());
		} 
	}


	protected static void veryifySyntax(TSelectSqlStatement pStmt) throws SelectException {
		// Query is a combined query statement
		if (pStmt.isCombinedQuery()) {
			String setOper="";
			switch (pStmt.getSetOperator()){
			case 1: 
				setOper = "UNION";
				break;
			case 2: 
				setOper = "UNION ALL";
				break;
			case 3: 
				setOper = "INTERSECT";
				break;
			case 4: 
				setOper = "INTERSECT ALL";
				break;
			case 5:
				setOper = "MINUS";
				break;
			case 6: 
				setOper = "MINUS ALL";
				break;
			case 7: 
				setOper = "EXCEPT";
				break;
			case 8: 
				setOper = "EXCEPT ALL";
				break;
			}
			throw new SelectException("Set operation '"+setOper+"' is not supported.");
		}

		// group by
		if (pStmt.getGroupByClause() != null){
			throw new SelectException("'GROUP BY' operation is not supported.");
		}
		// order by
		if (pStmt.getOrderbyClause() != null){
			throw new SelectException("'ORDER BY' operation is not supported.");
		}
		// for update
		if (pStmt.getForUpdateClause() != null){
			throw new SelectException("'FOR UPDATE' operation is not supported.");
		}
		// top clause
		if (pStmt.getTopClause() != null){
			throw new SelectException("'TOP' operation is not supported.");
		}
		// limit clause
		if (pStmt.getLimitClause() != null){
			throw new SelectException("'LIMIT' operation is not supported.");
		}
	}

	private static ArrayList<TableRow> getCrossProduct(Table tableOne, ArrayList<TableColumn> projectedColumnsOne, Table tableTwo, ArrayList<TableColumn> projectedColumnsTwo) {
		// Get the row indexes of the projected columns
		ArrayList<Integer> rowOneProjectedIndexes = new ArrayList<Integer>();
		ArrayList<Integer> rowTwoProjectedIndexes = new ArrayList<Integer>();
		for (TableColumn column : projectedColumnsOne) {
			rowOneProjectedIndexes.add(tableOne.getTableColumns().indexOf(column));
		}
		for (TableColumn column : projectedColumnsTwo) {
			rowTwoProjectedIndexes.add(tableTwo.getTableColumns().indexOf(column));
		}

		// For each of the projected rows in tableOne, go through each of the projected rows in tableTwo, create new Row (projectedRows1+projectedRows2)
		ArrayList<TableRow> resultRows = new ArrayList<TableRow>();
		for (TableRow rowOne : tableOne.getTableRows()) {
			for (TableRow rowTwo : tableTwo.getTableRows()) {
				ArrayList<Object> elementList = new ArrayList<Object>();
				for (Integer rowOneIndex : rowOneProjectedIndexes) {
					elementList.add(rowOne.getElement(rowOneIndex));
				}
				for (Integer rowTwoIndex : rowTwoProjectedIndexes) {
					elementList.add(rowTwo.getElement(rowTwoIndex));
				}
				resultRows.add(new TableRow(elementList));
			}
		}
		return resultRows;
	}

	private static ArrayList<TableRow> getCrossProduct(ArrayList<TableRow> rowListOne, ArrayList<TableRow> rowListTwo) {
		// For each of the rows in tableOne, go through each of rows in tableTwo, create new Row (Rows1+Rows2)
		ArrayList<TableRow> resultRows = new ArrayList<TableRow>();
		for (TableRow rowOne : rowListOne) {
			for (TableRow rowTwo : rowListTwo) {
				ArrayList<Object> elementList = new ArrayList<Object>();
				for (Object element : rowOne.getElementList()) {
					elementList.add(element);
				}
				for (Object element : rowTwo.getElementList()) {
					elementList.add(element);
				}
				resultRows.add(new TableRow(elementList));
			}
		}
		return resultRows;
	}

	private static ArrayList<TableRow> getCrossProductOfAllTables(HashMap<Table, ArrayList<TableColumn>> columnHashMap) throws SelectException {
		// If only one table, return the table rows

		HashMap<Table, ArrayList<TableColumn>> columnHashMapCopy = columnHashMap;

		if (columnHashMapCopy.size() == 1) {
			return ((Table) columnHashMap.keySet().toArray()[0]).getTableRows();
		} else if (columnHashMapCopy.size() > 1){
			// There are more than one tables to get cartesian product of rows from

			ArrayList<TableRow> crossedRows = new ArrayList<TableRow>();
			Table tableOne = (Table) columnHashMap.keySet().toArray()[0];
			Table tableTwo = (Table) columnHashMap.keySet().toArray()[1];
			// Cross the first two tables
			crossedRows = getCrossProduct(tableOne, columnHashMap.get(tableOne), tableTwo, columnHashMap.get(tableTwo));
			// Remove those two tables from the hash map
			columnHashMap.remove(tableOne);
			columnHashMap.remove(tableTwo);
			// If there are more tables, cross them one by one with crossedRows
			for (Table table : columnHashMap.keySet()) {
				crossedRows = getCrossProduct(crossedRows, table.getTableRows());
			}
			return crossedRows;
		} else {
			throw new SelectException("No tables found in statement.");
		}
	}

	/*
	 * Prints out each of the rows that pass the where clause in the select statement.
	 * 
	 * Returns the number of rows printed
	 */
	public static int printSelectRows(ArrayList<TableRow> allTablesRows, ArrayList<TableColumn> allTablesColumns, ArrayList<String> projectedColumnNames, String whereClause, boolean asteriskFlag) throws Exception {
		int rowsAffected = 0;
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");

		ArrayList<TableRow> rowsToPrint = new ArrayList<TableRow>();
		String expression = null;
		for (int rowIndex=0; rowIndex < allTablesRows.size(); rowIndex++) {
			expression = whereClause;
			// Replace all column names with values from that column for the given row index
			for (int columnIndex = 0; columnIndex < allTablesColumns.size(); columnIndex++) {
				if (allTablesRows.get(rowIndex).getElement(columnIndex) instanceof String) {
					expression = expression.replace(allTablesColumns.get(columnIndex).getColumnName(), "'"+allTablesRows.get(rowIndex).getElement(columnIndex)+"'");
				} else {
					expression = expression.replace(allTablesColumns.get(columnIndex).getColumnName(), (String) allTablesRows.get(rowIndex).getElement(columnIndex));
				}
			}
			try {
				if (engine.eval(expression).toString().equals("true")) {
					// Keep track of the rows that will be deleted
					rowsToPrint.add(allTablesRows.get(rowIndex));					
					rowsAffected++;
				}
			} catch (ScriptException e) {
				throw new Exception("Invalid where clause '"+expression+"'.");
			}
		}

		// Print all columns of result
		if (asteriskFlag) {
			for (TableColumn column : allTablesColumns) {
				System.out.print(column.getColumnName()+"\t");
			}
			System.out.println();
			for (TableRow row : rowsToPrint) {
				for (Object element : row.getElementList()) {
					System.out.print(element+"\t");
				}
				System.out.println();
			}
		} else {
			// Get the indexes of columns to project onto the rows
			ArrayList<Integer> projectionIndexes = new ArrayList<Integer>();
			ArrayList<String> allTablesColumnNames = new ArrayList<String>();
			for (TableColumn column : allTablesColumns) {
				allTablesColumnNames.add(column.getColumnName());
			}
			for (String projectedColumnName : projectedColumnNames) {
				System.out.print(projectedColumnName+"\t");
				projectionIndexes.add(allTablesColumnNames.indexOf(projectedColumnName));
			}
			System.out.println();

			// Print the rows, but only the ones that are projected
			for (TableRow row : rowsToPrint) {
				for (Integer projectionIndex : projectionIndexes) {
					System.out.print(row.getElement(projectionIndex)+"\t");
				}
				System.out.println();
			}
		}
		return rowsAffected;
	}
}