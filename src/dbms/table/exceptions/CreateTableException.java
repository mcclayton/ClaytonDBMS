package dbms.table.exceptions;


public class CreateTableException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/*
	 * This type of exception should be thrown if a runtime error occurs in the
	 * creation of a table from the parsing of a CREATE TABLE SQL statement.
	 */
	
	public CreateTableException(String message) {
        super("CreateTable Error: "+message);
    }
	
	public CreateTableException(String message, String tableName) {
        super("CreateTable Error: Cannot create table '"+tableName+"'. "+message);
    }
}