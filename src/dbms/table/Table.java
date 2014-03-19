package dbms.table;

import java.util.ArrayList;

import dbms.table.constraints.ForeignKeyConstraint;
import dbms.table.constraints.PrimaryKeyConstraint;

public class Table {
	
	/* Table Components */
	private String tableName = null;
	private ArrayList<TableColumn> tableColumns = null;
	private ArrayList<TableRow> tableRows = null;
	
	/* Table Key Constraints */
	//TODO: Need to implement an attribute/constraint system for primary/foreign keys
	private PrimaryKeyConstraint primaryKey = null;
	private ArrayList<ForeignKeyConstraint> foreignKeyList = null;
	
	
	/*				col_index_0		col_index_1
	 *  			|-------------|-------------|...
	 * 				|colName0     |colName1     |...
	 * 				|-------------|-------------|...
	 * row_index_0	|element0,0   |element0,1   |...
	 * 				|-------------|-------------|...
	 * row_index_1	|element1,0   |element1,1   |...
	 * 				|-------------|-------------|...
 	 *							  .
 	 *							  .
 	 *							  .
	 */ 
	
	
	
	public Table(String tableName, ArrayList<TableColumn> tableColumns, ArrayList<TableRow> tableRows) {
		this.foreignKeyList = new ArrayList<ForeignKeyConstraint>();
		this.tableName = tableName;
		this.tableRows = tableRows;
		this.tableColumns = tableColumns;
		
		if (this.tableRows == null) {
			this.tableRows = new ArrayList<TableRow>();
		}
		if (tableName == null) {
			// TODO: Throw an exception
			System.out.println("Error: No table name specified.");
		}
		if (tableColumns == null || tableColumns.size() <= 0) {
			// TODO: Throw an exception
			System.out.println("Error: No attributes specified.");
		}
	}
	
	public Table(String tableName, ArrayList<TableColumn> tableColumns) {
		this(tableName, tableColumns, null);
	}
	
	public void addRow(TableRow row) {
		this.tableRows.add(row);
	}
	
	public void addColumn(String columnName) {
		for (int i=0; i<tableColumns.size(); i++) {
			if (tableColumns.get(i).equals(columnName)) {
				// TODO: Throw an exception
				System.out.println("Error: Column '"+columnName+"' already exists in table '"+this.tableName+"'");
				return;
			}
		}
	}
	
	
	
	/* Getters and Setters */
	public String getTableName() {return this.tableName;}
	public ArrayList<TableColumn> getTableColumns() {return this.tableColumns;}
	public ArrayList<TableRow> getTableRows() {return this.tableRows;}
	public TableColumn getTableColumn(int index) {return this.tableColumns.get(index);}
	public TableColumn getTableColumnByName(String tableColumnName) {
		// Search the tables columns for the column  with name exactly matching the tableColumnName
		for (TableColumn column : this.tableColumns) {
			if (column.getColumnName().equals(tableColumnName)) {
					return column;
			}
		}
		// Couldn't find column with that name
		return null;
	}
	public TableRow getTableRow(int index) {return this.tableRows.get(index);}
	public PrimaryKeyConstraint getPrimaryKeyConstraint() {return this.primaryKey;}
	public ArrayList<ForeignKeyConstraint> getForeignKeyConstraintList() {return this.foreignKeyList;}
	
	
	public void setPrimaryKeyConstraint(PrimaryKeyConstraint primaryKey) {this.primaryKey = primaryKey;}
	public void setForeignKeyConstraintList(ArrayList<ForeignKeyConstraint> foreignKeyList) {this.foreignKeyList = foreignKeyList;}
	
}