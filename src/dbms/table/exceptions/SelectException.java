package dbms.table.exceptions;


public class SelectException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/*
	 * This type of exception should be thrown if a runtime error occurs in the
	 * parsing or printing of a select statement.
	 */
	
	public SelectException(String message) {
        super("Select Error: "+message);
    }
}