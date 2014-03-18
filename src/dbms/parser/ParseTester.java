package dbms.parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

import java.util.ArrayList;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableRow;
import dbms.table.TableSearch;


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
				Table table = ParseCreateTable.createTableFromStatement((TCreateTableSqlStatement) sqlparser.sqlstatements.get(i));
				TableSearch.addTable(table.getTableName(), table);
			}
			
			
			// Print Each Table
			for (Table table : TableSearch.getTableMap().values()) {
				// Print table name
				System.out.println("\nTABLE_NAME: "+table.getTableName());
				
				if (table.getPrimaryKeyConstraint() != null) {
					System.out.print("Primary Key (");
					for (TableColumn column : table.getPrimaryKeyConstraint().getPrimaryColumnList()) {
						System.out.print(column.getColumnName()+",");
					}
					System.out.println(")");
				} else {
					System.out.println("PRIMARY KEY WAS NULL...");
				}
				
				// Print column names
				for (TableColumn column : table.getTableColumns()) {
					System.out.print("COLUMN: "+column.getColumnName());
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
			System.out.println("Parse Error:\n"+sqlparser.getErrormessage());
		}
	}

}
