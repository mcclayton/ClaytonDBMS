package dbms.table.exceptions;


public class DeleteUserException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public DeleteUserException(String message) {
        super("DeleteUser Error: "+message);
    }
}