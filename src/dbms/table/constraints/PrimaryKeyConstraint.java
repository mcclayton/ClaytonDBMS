package dbms.table.constraints;

import java.util.ArrayList;

import dbms.table.TableColumn;


public class PrimaryKeyConstraint {
	private ArrayList<TableColumn> primaryColumnList;	// Columns who must be not null and be unique as a whole
	
	public PrimaryKeyConstraint(ArrayList<TableColumn> primaryColumnList) {
		this.primaryColumnList = primaryColumnList;
	}
	
	public PrimaryKeyConstraint() {
		this.primaryColumnList = new ArrayList<TableColumn>();
	}
	
	
	/* Getters and Setters */
	public ArrayList<TableColumn> getPrimaryColumnList() {return this.primaryColumnList;}
	
	public void addPrimaryColumn(TableColumn column){
		this.primaryColumnList.add(column);
	}
		
}