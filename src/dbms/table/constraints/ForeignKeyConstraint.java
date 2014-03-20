package dbms.table.constraints;

import dbms.table.Table;
import dbms.table.TableColumn;


public class ForeignKeyConstraint {
	private Table referencedTable = null;
	private TableColumn referencedColumn = null;
	private TableColumn column = null;
	
	public ForeignKeyConstraint(Table referencedTable, TableColumn column, TableColumn referencedColumn) {
		this.referencedTable = referencedTable;
		this.column = column;
		this.referencedColumn = referencedColumn;
	}
	
	public ForeignKeyConstraint(Table referencedTable) {
		this.referencedTable = referencedTable;
		this.column = null;
		this.referencedColumn = null;
	}
	
	public void setColumn(TableColumn column){
		this.column = column;
	}
	
	public void setReferencedColumn(TableColumn column){
		this.referencedColumn = column;
	}
	
	/* Getters and Setters */
	public Table getReferencedTable() {return this.referencedTable;}
	public TableColumn getColumn() {return this.column;}
	public TableColumn getReferencedColumn() {return this.referencedColumn;}
		
}