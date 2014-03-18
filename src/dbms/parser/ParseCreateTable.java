package dbms.parser;

import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TConstraint;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

import java.util.ArrayList;

import dbms.table.Table;
import dbms.table.TableColumn;


public class ParseCreateTable {
	protected static Table createTableFromStatement(TCreateTableSqlStatement pStmt) {
		String tableName = pStmt.getTargetTable().toString();
		
		// Parse Columns
		ArrayList<TableColumn> tableColumnList = new ArrayList<TableColumn>();
		TColumnDefinition column;
		String columnName;
		String columnDataType;
		for(int i=0;i<pStmt.getColumnList().size();i++){
			column = pStmt.getColumnList().getColumn(i);
			
			// Get column name
			columnName = column.getColumnName().toString();
			
			// Get the column data type
			if (column.getDatatype() != null) {
				columnDataType = column.getDatatype().toString();
			} else {
				// TODO: Throw Exception
				System.out.println("Create_Table Error:\nColumn '"+columnName+"' cannot have null datatype.");
				return null;
			}
			
			// TODO: Add column constraints
			if (column.getConstraints() != null) {
				System.out.println("\t("+columnName+") inline constraints:");
				for(int j=0;j<column.getConstraints().size();j++){
					printConstraint(column.getConstraints().getConstraint(j),false);
				}
			}	
			
			// TODO: Add constraint list to column instead of null
			tableColumnList.add(new TableColumn(tableName, columnName, columnDataType, null));
		}		
		
		// TODO: Add outline constraints
		if(pStmt.getTableConstraints().size() > 0) {
			//System.out.println("\toutline constraints:");
			for(int i=0;i<pStmt.getTableConstraints().size();i++) {
				printConstraint(pStmt.getTableConstraints().getConstraint(i), true);
				System.out.println("");
			}
		}
		
		Table table = new Table(tableName, tableColumnList);
		System.out.println("Table created successfully");
		return table;
	}
	
	
	protected static void printConstraint(TConstraint constraint, Boolean outline) {
		System.out.println("***"+constraint.getConstraint_type().toString());
		
		switch(constraint.getConstraint_type()){
		case notnull:
			System.out.println("\t\t-not null-");
			break;
		case primary_key:
			System.out.println("\t\t-primary key-");
			if (outline){
				String lcstr = "";
				if (constraint.getColumnList() != null){
					for(int k=0;k<constraint.getColumnList().size();k++){
						if (k !=0 ){lcstr = lcstr+",";}
						lcstr = lcstr+constraint.getColumnList().getObjectName(k).toString();
					}
					System.out.println("\t\tprimary key columns:"+lcstr);
				}
			}
			break;
		case unique:
			System.out.println("\t\t-unique key-");
			if(outline){
				String lcstr="";
				if (constraint.getColumnList() != null){
					for(int k=0;k<constraint.getColumnList().size();k++){
						if (k !=0 ){lcstr = lcstr+",";}
						lcstr = lcstr+constraint.getColumnList().getObjectName(k).toString();
					}
				}
				System.out.println("\t\tcolumns:"+lcstr);
			}
			break;
		case check:
			System.out.println("\t\t-check:-"+constraint.getCheckCondition().toString());
			break;
		case foreign_key:
		case reference:
			System.out.println("\t\t-foreign key-");
			if(outline){
				String lcstr="";
				if (constraint.getColumnList() != null){
					for(int k=0;k<constraint.getColumnList().size();k++){
						if (k !=0 ){lcstr = lcstr+",";}
						lcstr = lcstr+constraint.getColumnList().getObjectName(k).toString();
					}
				}
				System.out.println("\t\tcolumns:"+lcstr);
			}
			System.out.println("\t\treferenced table:"+constraint.getReferencedObject().toString());
			if (constraint.getReferencedColumnList() != null){
				String lcstr="";
				for(int k=0;k<constraint.getReferencedColumnList().size();k++){
					if (k !=0 ){lcstr = lcstr+",";}
					lcstr = lcstr+constraint.getReferencedColumnList().getObjectName(k).toString();
				}
				System.out.println("\t\treferenced columns:"+lcstr);
			}
			break;
		default:
			break;
		}
	}

}
