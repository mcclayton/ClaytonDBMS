package dbms.table.constraints;

import java.util.ArrayList;

import dbms.table.Table;
import dbms.table.TableColumn;


public class ForeignKeyConstraint {
	private Table referencedTable = null;
	private ArrayList<TableColumn> referencedColumnList;
	private ArrayList<TableColumn> columnList;
	
	public ForeignKeyConstraint(Table referencedTable, ArrayList<TableColumn> columnList, ArrayList<TableColumn> referencedColumnList) {
		this.referencedTable = referencedTable;
		this.columnList = columnList;
		this.referencedColumnList = referencedColumnList;
	}
	
	public ForeignKeyConstraint(Table referencedTable) {
		this.referencedTable = referencedTable;
		this.columnList = new ArrayList<TableColumn>();
		this.referencedColumnList = new ArrayList<TableColumn>();
	}
	
	public void addColumn(TableColumn column){
		this.columnList.add(column);
	}
	
	public void addReferencedColumn(TableColumn column){
		this.referencedColumnList.add(column);
	}
	
	/* Getters and Setters */
	public Table getReferencedTable() {return this.referencedTable;}
	public ArrayList<TableColumn> getColumnList() {return this.columnList;}
	public ArrayList<TableColumn> getReferencedColumnList() {return this.referencedColumnList;}
		
}