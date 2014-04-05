package dbms.table;

import java.io.Serializable;
import java.util.ArrayList;

import dbms.table.constraints.ForeignKeyConstraint;
import dbms.table.constraints.NameValidation;
import dbms.table.constraints.PrimaryKeyConstraint;
import dbms.table.exceptions.AttributeException;
import dbms.table.exceptions.CreateTableException;

@SuppressWarnings("serial")
public class Table implements Serializable {
	
	/* Table Components */
	private String tableName = null;
	private ArrayList<TableColumn> tableColumns = null;
	private ArrayList<TableRow> tableRows = null;
	
	/* Table Key Constraints */
	private PrimaryKeyConstraint primaryKey = null;
	private ArrayList<ForeignKeyConstraint> foreignKeyList = null;
	
	/* Array list of columns that are in the subschema (No subschema if isEmpty() == true) */
	private ArrayList<TableColumn> subschemaColumnList = new ArrayList<TableColumn>();
	
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
	
	
	
	public Table(String tableName, ArrayList<TableColumn> tableColumns, ArrayList<TableRow> tableRows) throws CreateTableException {
		this.foreignKeyList = new ArrayList<ForeignKeyConstraint>();
		this.tableName = tableName;
		this.tableRows = tableRows;
		this.tableColumns = tableColumns;
		
		if (this.tableRows == null) {
			this.tableRows = new ArrayList<TableRow>();
		}
		if (tableName == null) {
			throw new CreateTableException("Table cannot have null name.");
		}
		if (!NameValidation.isValidAlphaNumUnderscoreName(tableName)) {
			throw new CreateTableException("Invalid table name '"+tableName+"'.");
		}
		if (!NameValidation.isValidNameLength(tableName)) {
			throw new CreateTableException("Invalid table name length '"+tableName+"'.");
		}
		if (tableColumns == null || tableColumns.size() <= 0) {
			throw new CreateTableException("At least one attribute must be specified to create a table.", this.tableName);
		}
		
	}
	
	public Table(String tableName, ArrayList<TableColumn> tableColumns) throws CreateTableException {
		this(tableName, tableColumns, null);
	}
	
	public void addRow(TableRow row) {
		this.tableRows.add(row);
	}
	
	public void addColumn(TableColumn column) throws AttributeException {
		String columnName = column.getColumnName();
		for (int i=0; i<tableColumns.size(); i++) {
			if (tableColumns.get(i).equals(column)) {
				throw new AttributeException("Table attribute names must be unique. Attribute '"+columnName+"' is already defined in this table.");
			}
		}
		this.tableColumns.add(column);
	}
	
	/*
	 * Sets the boolean value for subschema in all table's columns
	 */
	public void setAllSubschemaBoolean(boolean bool) {
		for (TableColumn col : this.tableColumns) {
			col.setSubschemaBoolean(bool);
		}
		this.subschemaColumnList.clear();
	}
	
	public void clearSubschemaList() {
		this.subschemaColumnList.clear();
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
	public ArrayList<TableColumn> getSubschemaList() {return this.subschemaColumnList;}

	
	
	public void setPrimaryKeyConstraint(PrimaryKeyConstraint primaryKey) {this.primaryKey = primaryKey;}
	public void setForeignKeyConstraintList(ArrayList<ForeignKeyConstraint> foreignKeyList) {this.foreignKeyList = foreignKeyList;}
	
}