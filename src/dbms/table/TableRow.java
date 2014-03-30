package dbms.table;

import java.util.ArrayList;


public class TableRow {
	private ArrayList<Object> row;
	
	public TableRow(ArrayList<Object> row) {
		this.row = row;
	}
	
	
	/* Getters and Setters */
	public Object getElement(int columnIndex) {return this.row.get(columnIndex);}
	public ArrayList<Object> getElementList() {return this.row;}
		
}