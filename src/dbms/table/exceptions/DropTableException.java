package dbms.table.exceptions;


public class DropTableException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/*
	 * This type of exception should be thrown if a runtime error occurs in the
	 * deletion of a table from the parsing of a DROP TABLE SQL statement.
	 */
	
	public DropTableException(String message) {
        super("DropTable Error: "+message);
    }
	
	public DropTableException(String message, String tableName) {
        super("DropTable Error: Cannot drop table '"+tableName+"'. "+message);
    }
}