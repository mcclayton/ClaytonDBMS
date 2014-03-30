package dbms.table.exceptions;


public class UpdateException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/*
	 * This type of exception should be thrown if a runtime error occurs in the
	 * insertion of values into a table from the parsing of an INSERT SQL statement.
	 */
	
	public UpdateException(String message) {
        super("Update Error: "+message);
    }
	
	public UpdateException(String message, String tableName) {
        super("Update Error: Cannot update values in table '"+tableName+"'. "+message);
    }
}