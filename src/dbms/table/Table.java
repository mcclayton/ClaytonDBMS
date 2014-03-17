package dbms.table;

import java.util.ArrayList;

public class Table {
	private String tableName;
	private ArrayList<String> columnNames;
	private ArrayList<TableRow> tableRows;
	//TODO: Need to add an attribute/constraint system
	
	public Table(String tableName, ArrayList<String> columnNames, ArrayList<TableRow> tableRows) {
		this.tableName = tableName;
		this.tableRows = tableRows;
		this.columnNames = columnNames;
		if (tableName == null) {
			// TODO: Throw an exception
			System.out.println("Error: No table name specified.");
		}
		if (columnNames == null || columnNames.size() <= 0) {
			// TODO: Throw an exception
			System.out.println("Error: No attributes specified.");
		}
	}
	
	public Table(String tableName, ArrayList<String> columnNames) {
		this(tableName, columnNames, null);
	}
	
	public void addRow(TableRow row) {
		this.tableRows.add(row);
	}
	
	public void addColumn(String columnName) {
		for (int i=0; i<columnNames.size(); i++) {
			if (columnNames.get(i).equals(columnName)) {
				// TODO: Throw an exception
				System.out.println("Error: Column '"+columnName+"' already exists in table '"+this.tableName+"'");
				return;
			}
		}
	}
		
}