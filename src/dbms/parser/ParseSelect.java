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
		for(int i=0; i < pStmt.getResultColumnList().size(); i++) {
			TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);

			// Don't allow aliases
			if (resultColumn.getAliasClause() != null) {
				throw new SelectException("Aliases are not supported.");
			}

			System.out.printf("\tSELECT COLUMN: %s\n", resultColumn.getExpr().toString());
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
		ArrayList<String> columnNames;
		try {
			columnNames = ParseWhereClause.getAttributeNames(pStmt.getWhereClause(), tablesInFromClause);
		} catch (Exception e) {
			throw new SelectException(e.getMessage());
		}

		// TODO: Find out which columns go in which table. Then take the cartesian product of each of the tables. Then apply the where constraint and return the matching rows.
		HashMap<Table, ArrayList<TableColumn>> columnHashMap = new HashMap<Table, ArrayList<TableColumn>>();	// Used to keep track of which tables columns belong to.
		for (Table table : tablesInFromClause) {
			ArrayList<TableColumn> columnsInTable = new ArrayList<TableColumn>();
			for (String columnName : columnNames) {
				if (table.getTableColumnByName(columnName) != null) {
					// Add the column to the list of columns that belong to table
					columnsInTable.add(table.getTableColumnByName(columnName));
				}
			}
			columnHashMap.put(table, columnsInTable);
		}
		
		// TODO: Remove this test output
		for (Object table : columnHashMap.keySet().toArray()) {
			for (TableColumn column : columnHashMap.get((Table) table)) {
				System.out.println(">>> COLUMN: "+column.getColumnName()+" belongs to TABLE: "+((Table) table).getTableName());
			}
		}


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

}