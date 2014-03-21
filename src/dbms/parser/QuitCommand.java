package dbms.parser;



public class QuitCommand {

	/*
	 * Handles the quit command
	 */	


	/*
	 * Determine if the quit command was entered and execute it.
	 * If no valid quit command was entered, then return false 
	 */
	public static boolean parseAndPerformQuitCommand(String statementString) {
		if (statementString == null) {
			return false;
		}
		// (?i) In regex is to make the command case insensitive
		if (statementString.matches("(?i)([ \t\r\n\f]*)quit([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			// TODO: Implement the quit command
			System.out.println(" *** The quit command will go here. ***");
			return true;
		} else {
			// No quit command, return false
			return false;
		}
	}

}