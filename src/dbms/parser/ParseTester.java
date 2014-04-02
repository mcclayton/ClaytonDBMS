package dbms.parser;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import dbms.table.exceptions.AttributeException;
import dbms.table.exceptions.CreateTableException;
import dbms.table.exceptions.DeleteRowsException;
import dbms.table.exceptions.DropTableException;
import dbms.table.exceptions.HelpException;
import dbms.table.exceptions.InsertException;
import dbms.table.exceptions.SelectException;
import dbms.table.exceptions.UpdateException;


public class ParseTester {

	public static void main(String args[])
	{
		// Use Oracle DB Syntax
		EDbVendor dbVendor = EDbVendor.dbvoracle;

		TGSqlParser sqlparser = new TGSqlParser(dbVendor);
		sqlparser.sqlfilename = "./sql/admin.sql";	// The file to be parsed. Use 'sqltext' if only single statement

		//sqlparser. = "SELECT sname,cname,fname FROM STUDENT,CLASS,FACULTY,ENROLLED WHERE snum=student_num AND fid=faculty_id AND cname=class_name;";


		// TODO: Split .sql files into statements by semicolons so that a parse error in one statement doesn't affect them all.
		// TODO: Add batch and interactive mode
		int ret = -1;
		try {
			ret = sqlparser.parse();
		} catch(NullPointerException nullExc) {
			//TODO: Reprint console
			ret = 0;
		}
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
				} catch (DeleteRowsException updateExcept) {
					// Deleting rows from table was unsuccessful
					System.out.println(updateExcept.getMessage());
				} catch (SelectException selectExcept) {
					// Query was unsuccessful
					System.out.println(selectExcept.getMessage());
				}
			}

			//for(int i=0;i<sqlparser.sqlstatements.size();i++){
			//	analyzeStmt(sqlparser.sqlstatements.get(i));
			//}
		} else{
			System.out.println("Parse Error: "+sqlparser.getErrormessage());
		}
	}



	protected static void parseAndPerformStmt(TCustomSqlStatement stmt) throws CreateTableException, AttributeException, DropTableException, InsertException, UpdateException, DeleteRowsException, SelectException{

		switch(stmt.sqlstatementtype) {
		case sstselect:
			ParseSelect.parseAndPrintSelect((TSelectSqlStatement)stmt);
			break;
		case sstdelete:
			try {
				ParseDeleteRows.deleteRowsFromStatement((TDeleteSqlStatement)stmt);
			} catch (Exception ex) {
				// Gotta catch 'em all!
				throw new DeleteRowsException(ex.getMessage());
			}
			break;
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
