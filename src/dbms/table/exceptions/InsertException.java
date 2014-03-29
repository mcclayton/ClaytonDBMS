package dbms.table.exceptions;


public class InsertException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/*
	 * This type of exception should be thrown if a runtime error occurs in the
	 * insertion of values into a table from the parsing of an INSERT SQL statement.
	 */
	
	public InsertException(String message) {
        super("Insert Error: "+message);
    }
	
	public InsertException(String message, String tableName) {
        super("Insert Error: Cannot insert values into table '"+tableName+"'. "+message);
    }
}