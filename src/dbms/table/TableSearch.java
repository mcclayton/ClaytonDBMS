package dbms.table;

import java.util.ArrayList;
import java.util.HashMap;

public class TableSearch {

	// Key is Table Name, value is table
	private static final HashMap<String, Table> TABLE_MAP = new HashMap<String, Table>();


	public static Table getTable(String tableName) {
		return TABLE_MAP.get(tableName);
	}

	public static void addTable(String tableNameKey, Table tableValue) {
		if (TABLE_MAP.containsKey(tableNameKey)) {
			// TODO: Throw an exception
			System.out.println("Error: Could not add table. Table with that name already exists.");
		} else {
			TABLE_MAP.put(tableNameKey, tableValue);
		}
	}

	public static TableColumn getTableColumnByName(String tableName, String tableColumnName) {
		Table table = getTable(tableName);
		if (table == null) {
			// Table did not exist
			return null;
		} else {
			// Search the tables columns for the column  with name exactly matching the tableColumnName
			for (TableColumn column : table.getTableColumns()) {
				if (column.getColumnName().equals(tableColumnName)) {
					return column;
				}
			}
			// Couldn't find column with that name
			return null;
		}
	}

	public static TableColumn getTableColumnByName(ArrayList<TableColumn> columnList, String tableColumnName) {
		// Search the columns for the column  with name exactly matching the tableColumnName
		for (TableColumn column : columnList) {
			if (column.getColumnName().equals(tableColumnName)) {
				return column;
			}
		}
		// Couldn't find column with that name
		return null;
	}


	/* Getters and Setters */
	public static HashMap<String, Table> getTableMap() {return TABLE_MAP;}
}