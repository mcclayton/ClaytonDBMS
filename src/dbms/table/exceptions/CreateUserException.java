package dbms.table.exceptions;


public class CreateUserException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public CreateUserException(String message) {
        super("CreateUser Error: "+message);
    }
}