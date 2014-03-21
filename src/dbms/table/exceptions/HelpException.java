package dbms.table.exceptions;


public class HelpException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/*
	 * This type of exception should be thrown if a runtime error occurs in the
	 * parsing/printing of a help command.
	 */
	
	public HelpException(String message) {
        super("HelpCommand Error: "+message);
    }
}