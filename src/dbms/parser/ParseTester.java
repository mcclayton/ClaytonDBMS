package dbms.parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

import java.util.ArrayList;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableColumn.DataType;
import dbms.table.TableRow;
import dbms.table.TableSearch;
import dbms.table.constraints.ForeignKeyConstraint;
import dbms.table.exceptions.AttributeException;
import dbms.table.exceptions.CreateTableException;


public class ParseTester {
	public static void main(String args[])
	{
		// Use Oracle DB Syntax
		EDbVendor dbVendor = EDbVendor.dbvoracle;

		TGSqlParser sqlparser = new TGSqlParser(dbVendor);
		sqlparser.sqlfilename = "./sql/table.sql";	// The file to be parsed. Use 'sqltext' if only single statement

		//sqlparser.sqltext = "CREATE TABLE TEST_TABLE (deptid	int	PRIMARY KEY, deptname varchar(50) PRIMARY KEY);";
		
		int ret = sqlparser.parse();
		if (ret == 0) {
			// Parse was successful
			
			for(int i=0; i<sqlparser.sqlstatements.size(); i++) {
				try {
					// Try to parse and create a new table
					// New table will be added to TABLE_MAP if successful
					ParseCreateTable.createTableFromStatement((TCreateTableSqlStatement) sqlparser.sqlstatements.get(i));
				} catch (CreateTableException cTabExcept) {
					// Parsing/Creating table was unsuccessful
					System.out.println(cTabExcept.getMessage());
				} catch (AttributeException aExcept) {
					// Parsing/Creating table was unsuccessful
					System.out.println(aExcept.getMessage());
				}
			}
			
			
			// Print Each Table
			for (Table table : TableSearch.getTableMap().values()) {
				// Print table name
				System.out.println("\nTABLE_NAME: "+table.getTableName());
				
				// Print primary key constraint
				if (table.getPrimaryKeyConstraint() != null) {
					System.out.print("Primary Key (");
					for (TableColumn column : table.getPrimaryKeyConstraint().getPrimaryColumnList()) {
						System.out.print(column.getColumnName()+",");
					}
					System.out.println(")");
				} else {
					System.out.println("PRIMARY KEY WAS NULL...");
				}
				
				// Print foreign key constraint
				if (!table.getForeignKeyConstraintList().isEmpty()) {
					for (ForeignKeyConstraint foreignKey : table.getForeignKeyConstraintList()) {
						System.out.println("FOREIGN KEY:");
						for (TableColumn column : foreignKey.getColumnList()) {
							System.out.println("\tCOLUMN: "+column.getColumnName());
						}
						for (TableColumn column : foreignKey.getReferencedColumnList()) {
							System.out.println("\tREFERENCED_COLUMN: "+column.getColumnName());
						}
					}
				}
				
				// Print column names/types/constraints
				for (TableColumn column : table.getTableColumns()) {
					System.out.print("COLUMN: "+column.getColumnName());
					
					if (column.getAttributeDataType() != null) {
						if (column.getAttributeDataType() == DataType.VARCHAR) {
							System.out.print("\t\tDATATYPE: "+column.getAttributeDataType().toString()+" "+column.getVarCharLength());
						} else {
							System.out.print("\t\tDATATYPE: "+column.getAttributeDataType().toString());
						}
					}
					
					if (column.getCheckConstraint() != null) {
						System.out.println("\t\t CONSTRAINT: "+column.getCheckConstraint());
					} else {
						System.out.println("");
					}
				}
				
				// Add dummy rows
				ArrayList<Object> elements = new ArrayList<Object>();
				for (int i=0; i<table.getTableColumns().size(); i++) {
					elements.add("elem_"+i);
				}
				TableRow rowOfElems = new TableRow(elements);
				table.addRow(rowOfElems);
				
				// Print Elements
				for (TableRow row : table.getTableRows()) {
					for (int i=0; i<table.getTableColumns().size(); i++) {
						System.out.print(row.getElement(i).toString()+"\t");
					}
				}
					
				System.out.print("\n");
			}
			
			
			
			
			
			
			
			
			
			//for(int i=0;i<sqlparser.sqlstatements.size();i++){
			//	analyzeStmt(sqlparser.sqlstatements.get(i));
			//}
		} else{
			System.out.println("Parse Error: "+sqlparser.getErrormessage());
		}
	}

}
