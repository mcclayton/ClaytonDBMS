package dbms.parser;

import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import java.util.ArrayList;
import java.util.HashMap;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableManager;
import dbms.table.TableRow;
import dbms.table.exceptions.SelectException;


public class ParseSelect {

	/* 
	 * Displays the tuples from the result set that is produced from parsing a Select Statement.
	 * 
	 * If successful, displays the tuples from the result set
	 * If unsuccessful, throws an exception and result set is not displayed
	 */

	protected static void parseAndPrintSelect(TSelectSqlStatement pStmt) throws SelectException {
		System.out.println("\n\nSELECT:");
		// Make sure syntax of select statement is correct
		veryifySyntax(pStmt);

		// Select list
		ArrayList<String> projectionColumns = new ArrayList<String>();	// The names of all columns that will be in the final result
		for(int i=0; i < pStmt.getResultColumnList().size(); i++) {
			TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);

			// Don't allow aliases
			if (resultColumn.getAliasClause() != null) {
				throw new SelectException("Aliases are not supported.");
			}

			// TODO: Implement projection and handle '*' operator here
			// Add the names of the columns that will be projected to a list
			if (!projectionColumns.contains(resultColumn.getExpr().toString())) {
				// TODO Ensure that the below column exists in one of the tables
				projectionColumns.add(resultColumn.getExpr().toString());
			}
		}

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

				if (TableManager.tableExists(join.getTable().toString())) {
					tablesInFromClause.add(TableManager.getTable(join.getTable().toString()));
				} else {
					throw new SelectException("Table '"+join.getTable().toString()+"' in FROM clause does not exist.");
				}
				break;
			default:
				// Invalid Explicit Join
				throw new SelectException("Explicit joins are not supported.");
			}
		}

		// Get the columns from the WHERE clause
		ArrayList<String> columnNamesToCross;	// The names of all the columns that will be involved in the cross product
		try {
			columnNamesToCross = ParseWhereClause.getAttributeNames(pStmt.getWhereClause(), tablesInFromClause);
		} catch (Exception e) {
			throw new SelectException(e.getMessage());
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
			ArrayList<TableColumn> columnsInTable = new ArrayList<TableColumn>();
			for (String columnName : columnNamesToCross) {
				if (table.getTableColumnByName(columnName) != null) {
					// Add the column to the list of columns that belong to table
					columnsInTable.add(table.getTableColumnByName(columnName));
				}
			}
			columnHashMap.put(table, columnsInTable);
		}

		// Then take the cartesian product of each of the tables. Then apply the where constraint and return the matching rows.

		// TODO: Remove this test output
		for (Object table : columnHashMap.keySet().toArray()) {
			for (TableColumn column : columnHashMap.get((Table) table)) {
				System.out.println(">>> COLUMN: "+column.getColumnName()+" belongs to TABLE: "+((Table) table).getTableName());
			}
		}

		// TODO: Remove this test
		Table tableOne = (Table) columnHashMap.keySet().toArray()[0];
		Table tableTwo = (Table) columnHashMap.keySet().toArray()[1];
		ArrayList<TableRow> crossedRows = getCrossProduct(tableOne, columnHashMap.get(tableOne), tableTwo, columnHashMap.get(tableTwo));
		System.out.println(">>> CROSS PRODUCT OF "+tableOne.getTableName()+" AND "+tableTwo.getTableName());
		for (TableRow row : crossedRows) {
			for (Object element : row.getElementList()) {
				System.out.print((String) element+"\t");
			}
			System.out.println();
		}
		
		// TODO: For each table, take the cross product of them. 
		// i.e. for T1, T2, T3. Perform T1 X T2 = T12. Then perform T12 X T3
		// TODO: After this final cross product, run through where clause and then project resulting columns

		// WHERE clause
		if (pStmt.getWhereClause() != null) {
			System.out.printf("WHERE CLAUSE: \n%s\n", pStmt.getWhereClause().getCondition().toString());
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

}