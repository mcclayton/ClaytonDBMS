package dbms.parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import dbms.table.exceptions.AttributeException;
import dbms.table.exceptions.CreateTableException;
import dbms.table.exceptions.DropTableException;
import dbms.table.exceptions.HelpException;
import dbms.table.exceptions.InsertException;
import dbms.table.exceptions.UpdateException;


public class ParseTester {

	public static void main(String args[])
	{
		// Use Oracle DB Syntax
		EDbVendor dbVendor = EDbVendor.dbvoracle;

		TGSqlParser sqlparser = new TGSqlParser(dbVendor);
		//sqlparser.sqlfilename = "./sql/admin.sql";	// The file to be parsed. Use 'sqltext' if only single statement


		sqlparser.sqltext = "CREATE TABLE DEPARTMENT(deptid INT CHECK(deptid>0 AND deptid<100), dname CHAR(30), location CHAR(30), PRIMARY KEY(deptid));\n";
		sqlparser.sqltext += "CREATE TABLE DEPARTMENT2(deptid INT CHECK(deptid>0 AND deptid<100), dname CHAR(30), location CHAR(30), PRIMARY KEY(deptid), FOREIGN KEY(deptid) REFERENCES DEPARTMENT(deptid));\n";
		sqlparser.sqltext += "INSERT INTO DEPARTMENT VALUES (77, 'Computer Science','West Lafayette');";
		sqlparser.sqltext += "INSERT INTO DEPARTMENT VALUES (9, 'Booooyaaaah','Hello world');";
		sqlparser.sqltext += "INSERT INTO DEPARTMENT2 VALUES (9, 'Booooyaaaah','Hello world');";


		sqlparser.sqltext += "UPDATE DEPARTMENT SET deptid=99, dname='4' WHERE deptid=11 OR deptid=22;";
		//sqlparser.sqltext += "UPDATE DEPARTMENT SET location='WLafayette' WHERE deptid=11 OR deptid=22;";
		//sqlparser.sqltext += "UPDATE STUDENT SET age=21,sname='Smith' WHERE sname='A.Smith';";

		//sqlparser.sqltext = "HELP TABLES; \nhelp create table ;\nhelp drop table; \n help select;\nhelp insert; \n help delete; \n heLP UPdate;\n  Quit ;";

		// TODO: Split .sql files into statements by semicolons so that a parse error in one statement doesn't affect them all.
		// TODO: Add batch and interactive mode
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
				} catch (UpdateException updateExcept) {
					// Updating values into table was unsuccessful
					System.out.println(updateExcept.getMessage());
				}
			}

			//for(int i=0;i<sqlparser.sqlstatements.size();i++){
			//	analyzeStmt(sqlparser.sqlstatements.get(i));
			//}
		} else{
			System.out.println("Parse Error: "+sqlparser.getErrormessage());
		}
	}



	protected static void parseAndPerformStmt(TCustomSqlStatement stmt) throws CreateTableException, AttributeException, DropTableException, InsertException, UpdateException{

		switch(stmt.sqlstatementtype) {
		case sstupdate:
			try {
				ParseUpdate.updateValuesFromStatement((TUpdateSqlStatement)stmt);
			} catch (Exception ex) {
				// Gotta catch 'em all!
				throw new UpdateException(ex.getMessage());
			}
			break;
		case sstinsert:
			try {
				ParseInsert.insertValuesFromStatement((TInsertSqlStatement)stmt);
			} catch (Exception ex) {
				// Gotta catch 'em all!
				throw new InsertException(ex.getMessage());
			}
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
