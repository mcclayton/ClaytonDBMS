package dbms.table.exceptions;


public class AttributeException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/*
	 * This type of exception should be thrown if a runtime error occurs
	 * involving the attribute level of a relation. For example, an improperly
	 * named attribute.
	 */
	
	public AttributeException(String message) {
        super("Attribute Error: "+message);
    }
}