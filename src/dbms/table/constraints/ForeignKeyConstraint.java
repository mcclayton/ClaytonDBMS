package dbms.table.constraints;

import java.util.ArrayList;

import dbms.table.Table;
import dbms.table.TableColumn;


public class ForeignKeyConstraint {
	private Table referencedTable = null;	// TODO: Perhaps make this a Table object instead of the name for faster lookup
	private ArrayList<TableColumn> referencedColumnList = new ArrayList<TableColumn>();
	
	public ForeignKeyConstraint(Table referencedTable, ArrayList<TableColumn> referencedColumnList) {
		this.referencedTable = referencedTable;
		this.referencedColumnList = referencedColumnList;
	}
	
	
	/* Getters and Setters */
	public Table getReferencedTable() {return this.referencedTable;}
	public ArrayList<TableColumn> getReferencedColumnList() {return this.referencedColumnList;}
		
}