package dbms.parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;

import java.util.ArrayList;

import dbms.help.HelpCommands;
import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableColumn.DataType;
import dbms.table.TableManager;
import dbms.table.TableRow;
import dbms.table.constraints.ForeignKeyConstraint;
import dbms.table.exceptions.AttributeException;
import dbms.table.exceptions.CreateTableException;
import dbms.table.exceptions.DropTableException;


public class ParseTester {
	
	public static void main(String args[])
	{
		// Use Oracle DB Syntax
		EDbVendor dbVendor = EDbVendor.dbvoracle;

		TGSqlParser sqlparser = new TGSqlParser(dbVendor);
		sqlparser.sqlfilename = "./sql/table.sql";	// The file to be parsed. Use 'sqltext' if only single statement

		//sqlparser.sqltext = "\t     HelP tAbles ; ";
		
		// TODO: Split .sql files into statements by semicolons so that a parse error in one statement doesn't affect them all.
		int ret = sqlparser.parse();
		if (ret == 0) {
			// Parse was successful
			
			for(int i=0; i<sqlparser.sqlstatements.size(); i++) {
				try {
					parseAndPerformStmt(sqlparser.sqlstatements.get(i));
				} catch (CreateTableException cTabExcept) {
					// Parsing/Creating table was unsuccessful
					System.out.println(cTabExcept.getMessage());
				} catch (AttributeException aExcept) {
					// Parsing/Creating table was unsuccessful
					System.out.println(aExcept.getMessage());
				} catch (DropTableException dTabExcept) {
					// Parsing/Dropping table was unsuccessful
					System.out.println(dTabExcept.getMessage());
				}
			}
			
			
			// Print Each Table
			for (Table table : TableManager.getTableMap().values()) {
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
						
						System.out.println("\tCOLUMN: "+foreignKey.getColumn().getColumnName());
						
						System.out.println("\tREFERENCED_COLUMN: "+foreignKey.getReferencedColumn().getColumnName());
					}
				}
				
				// Print column names/types/constraints
				for (TableColumn column : table.getTableColumns()) {
					System.out.print("COLUMN: "+column.getColumnName());
					
					if (column.getAttributeDataType() != null) {
						if (column.getAttributeDataType() == DataType.CHAR) {
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
	
	
	
	protected static void parseAndPerformStmt(TCustomSqlStatement stmt) throws CreateTableException, AttributeException, DropTableException{

		switch(stmt.sqlstatementtype) {
		case sstdroptable:
			ParseDropTable.dropTableFromStatement((TDropTableSqlStatement) stmt);
			break;
		case sstcreatetable:
			// Try to parse and create a new table
			// New table will be added to TABLE_MAP if successful
			ParseCreateTable.createTableFromStatement((TCreateTableSqlStatement) stmt);
			break;
		case sstsqlpluscmd:
			String statementString = stmt.toString();
			boolean ret;
			ret = HelpCommands.parseAndPrintHelpCommand(statementString);
			if (ret == false) {
				// No Help command was matched
				// TODO: Throw an exception
				System.out.println("Parse Error: Invalid command.");
			}
			break;
		default:
			System.out.println("<<< DEFAULT (UNHANDLED) >>>");
			System.out.println(stmt.sqlstatementtype.toString());
			System.out.println(stmt.toString());
		}
	}

}
