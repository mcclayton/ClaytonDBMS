package dbms.parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

import java.util.ArrayList;
import java.util.HashMap;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableRow;


public class ParseTester {
	public static void main(String args[])
	{
		// Use Oracle DB Syntax
		EDbVendor dbVendor = EDbVendor.dbvoracle;

		TGSqlParser sqlparser = new TGSqlParser(dbVendor);
		sqlparser.sqlfilename = "./sql/tables.sql";	// The file to be parsed. Use 'sqltext' if only single statement

		int ret = sqlparser.parse();
		if (ret == 0) {
			// Parse was successful

			// Key is Table Name, value is table
			HashMap<String, Table> tableMap = new HashMap<String, Table>();
			
			for(int i=0; i<sqlparser.sqlstatements.size(); i++) {
				Table table = ParseCreateTable.createTableFromStatement((TCreateTableSqlStatement) sqlparser.sqlstatements.get(i));
				tableMap.put(table.getTableName(), table);
			}
			
			
			// Print Each Table
			for (Table table : tableMap.values()) {
				// Print table name
				System.out.println("\nTABLE_NAME: "+table.getTableName());
				
				// Print column names
				for (TableColumn column : table.getTableColumns()) {
					System.out.print(column.getColumnName()+"\t");
				}
				System.out.print("\n");
				
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
