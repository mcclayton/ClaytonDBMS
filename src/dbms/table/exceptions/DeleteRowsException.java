package dbms.table.exceptions;


public class DeleteRowsException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/*
	 * This type of exception should be thrown if a runtime error occurs in the
	 * deletion of rows of a table from the parsing of a DELETE SQL statement.
	 */
	
	public DeleteRowsException(String message) {
        super("Delete Row(s) Error: "+message);
    }
	
	public DeleteRowsException(String message, String tableName) {
        super("Delete Row(s) Error: Cannot delete row(s) in table '"+tableName+"'. "+message);
    }
}