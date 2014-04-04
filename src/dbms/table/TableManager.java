package dbms.table;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import dbms.table.exceptions.CreateTableException;
import dbms.table.exceptions.DropTableException;

@SuppressWarnings("serial")
public class TableManager implements Serializable {

	// Key is Table Name, value is table
	private HashMap<String, Table> TABLE_MAP = null;	// Map of all existing tables

	// Key is userName, value is User object
	private HashMap<String, User> USER_MAP = null;	// Map of all user, which contain subschema information

	public TableManager() {
		TABLE_MAP = new HashMap<String, Table>();	// Map of all existing tables		
		USER_MAP = new HashMap<String, User>();	// Map of all user, which contain subschema information
	}


	public Table getTable(String tableName) {
		return TABLE_MAP.get(tableName);
	}

	public boolean tableExists(String tableName) {
		return TABLE_MAP.containsKey(tableName);
	}

	public void addTable(String tableNameKey, Table tableValue) throws CreateTableException {
		if (TABLE_MAP.containsKey(tableNameKey)) {
			throw new CreateTableException("Table with that name already exists.", tableNameKey);
		} else {
			TABLE_MAP.put(tableNameKey, tableValue);
		}
	}

	public void removeTable(String tableName) throws DropTableException {
		if (!TABLE_MAP.containsKey(tableName)) {
			throw new DropTableException("Table does not exist.", tableName);
		} else {
			TABLE_MAP.remove(tableName);
		}
	}

	public TableColumn getTableColumnByName(String tableName, String tableColumnName) {
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

	public TableColumn getTableColumnByName(ArrayList<TableColumn> columnList, String tableColumnName) {
		// Search the columns for the column  with name exactly matching the tableColumnName
		for (TableColumn column : columnList) {
			if (column.getColumnName().equals(tableColumnName)) {
				return column;
			}
		}
		// Couldn't find column with that name
		return null;
	}
	
	
	public void saveDatabase() {
		try {
			// Write to disk with FileOutputStream
			FileOutputStream f_out = new FileOutputStream("./database.data");

			// Write object with ObjectOutputStream
			ObjectOutputStream obj_out = new ObjectOutputStream (f_out);

			// Write object out to disk
			obj_out.writeObject(this);
			obj_out.close();
		} catch (Exception e) {
			System.out.println("QuitError: "+e.getMessage());
		}
	}


	/* Getters and Setters */
	public HashMap<String, Table> getTableMap() {return TABLE_MAP;}
	public HashMap<String, User> getUserMap() {return USER_MAP;}

	
}