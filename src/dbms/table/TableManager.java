package dbms.table;

import java.util.ArrayList;
import java.util.HashMap;

import dbms.table.exceptions.CreateTableException;
import dbms.table.exceptions.DropTableException;

public class TableManager {

	// Key is Table Name, value is table
	private static final HashMap<String, Table> TABLE_MAP = new HashMap<String, Table>();	// Map of all existing tables
	
	// Key is userName, value is User object
	private static final HashMap<String, User> USER_MAP = new HashMap<String, User>();	// Map of all user, which contain subschema information


	public static Table getTable(String tableName) {
		return TABLE_MAP.get(tableName);
	}
	
	public static boolean tableExists(String tableName) {
		return TABLE_MAP.containsKey(tableName);
	}

	public static void addTable(String tableNameKey, Table tableValue) throws CreateTableException {
		if (TABLE_MAP.containsKey(tableNameKey)) {
			throw new CreateTableException("Table with that name already exists.", tableNameKey);
		} else {
			TABLE_MAP.put(tableNameKey, tableValue);
		}
	}
	
	public static void removeTable(String tableName) throws DropTableException {
		if (!TABLE_MAP.containsKey(tableName)) {
			throw new DropTableException("Table does not exist.", tableName);
		} else {
			TABLE_MAP.remove(tableName);
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
	public static HashMap<String, User> getUserMap() {return USER_MAP;}
}