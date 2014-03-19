package dbms.table.constraints;



public class NameValidation {
	private static final int MAX_NAME_LENGTH = 256;	// Limit the name size of the attribute

	
	/*
	 * Checks to see if name is alpha numeric, allows underscores, and ensures the name is less than MAX_NAME_LENGTH characters
	 */
	public static boolean isValidAlphaNumUnderscoreName(String name) {
		if (name == null) {
			return false;
		}
		if (!name.matches("[a-zA-Z0-9_]+")) {
			return false;
		}
		return true;
	}
	public static boolean isValidNameLength(String name) {
		if (name == null) {
			return false;
		}
		if (name.length() > MAX_NAME_LENGTH) {
			return false;
		}
		return true;
	}
		
}