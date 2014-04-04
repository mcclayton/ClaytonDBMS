package dbms.table;

import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("serial")
public class TableRow implements Serializable {
	private ArrayList<Object> row;
	
	public TableRow(ArrayList<Object> row) {
		this.row = row;
	}
	
	
	/* Getters and Setters */
	public Object getElement(int columnIndex) {return this.row.get(columnIndex);}
	public ArrayList<Object> getElementList() {return this.row;}
	
	public Object setElement(int columnIndex, Object element) {return this.row.set(columnIndex, element);}
		
}