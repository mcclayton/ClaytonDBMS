package dbms.table;

import java.util.ArrayList;

public class TableSchema {
	String relationName;
	private ArrayList<TableSchemaRow> schemaRows;
	
	public TableSchema(String relationName, ArrayList<TableSchemaRow> schemaRows) {
		this.relationName = relationName;
		
		if (relationName == null) {
			// TODO: Throw an exception
			System.out.println("Error: No relation name specified.");
		}
		if (schemaRows == null || schemaRows.size() <= 0) {
			// TODO: Throw an exception
			System.out.println("Error: Schema must have at least one attribute.");
		}
	}	
}