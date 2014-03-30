package dbms.parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import dbms.table.exceptions.AttributeException;
import dbms.table.exceptions.CreateTableException;
import dbms.table.exceptions.DropTableException;
import dbms.table.exceptions.HelpException;
import dbms.table.exceptions.InsertException;


public class ParseTester {
	
	public static void main(String args[])
	{
		// Use Oracle DB Syntax
		EDbVendor dbVendor = EDbVendor.dbvoracle;

		TGSqlParser sqlparser = new TGSqlParser(dbVendor);
		//sqlparser.sqlfilename = "./sql/table.sql";	// The file to be parsed. Use 'sqltext' if only single statement

		sqlparser.sqltext = "CREATE TABLE DEPARTMENT(deptid INT CHECK(deptid>0 AND deptid<100), dname CHAR(30), location CHAR(30), PRIMARY KEY(deptid));\n";
        sqlparser.sqltext += "INSERT INTO DEPARTMENT VALUES (101, 'ComputerScience','West Lafayette');";
		//sqlparser.sqltext = "HELP TABLES; \nhelp create table ;\nhelp drop table; \n help select;\nhelp insert; \n help delete; \n heLP UPdate;\n  Quit ;";
		
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
				} catch (InsertException insertExcept) {
					// Inserting values into table was unsuccessful
					System.out.println(insertExcept.getMessage());
				}
			}
			
			
			//for(int i=0;i<sqlparser.sqlstatements.size();i++){
			//	analyzeStmt(sqlparser.sqlstatements.get(i));
			//}
		} else{
			System.out.println("Parse Error: "+sqlparser.getErrormessage());
		}
	}
	
	
	
	protected static void parseAndPerformStmt(TCustomSqlStatement stmt) throws CreateTableException, AttributeException, DropTableException, InsertException{

		switch(stmt.sqlstatementtype) {
		case sstinsert:
			ParseInsert.insertValuesFromStatement((TInsertSqlStatement)stmt);
			break;
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
			try {
				ret = HelpCommands.parseAndPrintHelpCommand(statementString);
				if (ret == false) {
					// Help command was not found, so try to see if it is a quit command
					ret = QuitCommand.parseAndPerformQuitCommand(statementString);
					if (ret == false) {
						// No Help/Quit command was matched
						System.out.println("Parse Error: Invalid command.");	
					}
				}
			} catch (HelpException e) {
				System.out.println(e.getMessage());
			}
			break;
		default:
			System.out.println("Parse Error: Invalid command.");
			//System.out.println("<<< DEFAULT (UNHANDLED) >>>");
			//System.out.println(stmt.sqlstatementtype.toString());
			//System.out.println(stmt.toString());
		}
	}

}
