package dbms.parser;

import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TConstraint;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

import java.util.ArrayList;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableSearch;
import dbms.table.constraints.PrimaryKeyConstraint;


public class ParseCreateTable {
	protected static Table createTableFromStatement(TCreateTableSqlStatement pStmt) {
		String tableName = pStmt.getTargetTable().toString();

		// Parse Columns
		ArrayList<TableColumn> tableColumnList = new ArrayList<TableColumn>();
		TColumnDefinition column;
		String columnName;
		String columnDataType;
		String columnCheckConstraint = null;
		PrimaryKeyConstraint primaryKey = new PrimaryKeyConstraint();
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

			// Get in-line column 'check' constraints
			if (column.getConstraints() != null) {
				for(int j=0;j<column.getConstraints().size();j++){
					columnCheckConstraint = getCheckConstraint(column.getConstraints().getConstraint(j));
				}
			}	

			// Add the new column to the column list
			tableColumnList.add(new TableColumn(tableName, columnName, columnDataType, columnCheckConstraint));
		}		

		// TODO: Add table key constraints
		if(pStmt.getTableConstraints().size() > 0) {
			System.out.println("\toutline constraints:");
			for(int i=0;i<pStmt.getTableConstraints().size();i++) {
				getOutlineConstraints(pStmt.getTableConstraints().getConstraint(i), true, primaryKey, tableColumnList);
				System.out.println("");
			}
		}

		Table table = new Table(tableName, tableColumnList);
		table.setPrimaryKeyConstraint(primaryKey);
		System.out.println("Table created successfully");
		return table;
	}


	protected static String getCheckConstraint(TConstraint constraint) {
		switch(constraint.getConstraint_type()){
		case check:
			return constraint.getCheckCondition().toString();
		case primary_key:
			// TODO: Throw Exception
			System.out.println("Create_Table Error: Primary key must be specified after all attributes are listed.");
			break;
		case foreign_key:
		case reference:
			// TODO: Throw Exception
			System.out.println("Create_Table Error: Foreign keys must be specified after all attributes are listed..");
			break;
		default:
			// TODO: Throw Exception
			System.out.println("Create_Table Error: Only 'CHECK' domain constraints are supported in-line. Cannot specify '"+constraint.toString()+"' constraint here.");
			break;
		}
		// No valid check constraint found
		return null;
	}


	protected static void getOutlineConstraints(TConstraint constraint, Boolean outline, PrimaryKeyConstraint primaryKey, ArrayList<TableColumn> columnList) {
		// TODO: Ensure that the user HAS to specify a primary key

		switch(constraint.getConstraint_type()){
		case primary_key:
			if (constraint.getColumnList() != null) {
				if (!primaryKey.getPrimaryColumnList().isEmpty()) {
					// TODO: Throw Exception
					System.out.println("Create_Table Error: Only one primary key or composite primary key is allowed per table.");
					return;
				}
				for(int k=0; k<constraint.getColumnList().size(); k++) {
					TableColumn column = TableSearch.getTableColumnByName(columnList, constraint.getColumnList().getObjectName(k).toString());
					if (column != null) {
						primaryKey.addPrimaryColumn(column);
					} else {
						// TODO: Throw Exception
						System.out.println("Create_Table Error: Specifying primary key on an invalid attribute '"+constraint.getColumnList().getObjectName(k).toString()+"'.");
						return;
					}
				}
			}
			break;
		case foreign_key:
		case reference:
			System.out.println("\t\t>foreign key");
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
			// TODO: Throw Exception
			System.out.println("Create_Table Error: Expected primary or foreign key constraint. Cannot specify '"+constraint.toString()+"' constraint here.");
			break;
		}
	}

}
