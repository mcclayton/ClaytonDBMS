package dbms.help;

import dbms.table.Table;
import dbms.table.TableManager;


public class HelpCommands {
	
	/*
	 * Handles printing of different help commands
	 */
	
	public static boolean parseAndPrintHelpCommand(String statement) {
		if (statement == null) {
			// TODO: Throw an exception
			System.out.println("Parse Error: Invalid command.");
		}
		
		String statementString = statement.toLowerCase();
		if (statementString.matches("([ \t\r\n\f]*)help ([ \t\r\n\f]*)tables([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			printHelpTables();
			return true;
		} else {
			// No help command, return false
			return false;
		}
	}
	
	public static void printHelpTables() {
		System.out.println("\nHelp Tables\n-----------");
		if (TableManager.getTableMap().isEmpty()) {
			System.out.println("No tables found.");
		} else {
			for (Table table : TableManager.getTableMap().values()) {
				System.out.println(table.getTableName());
			}
		}
		System.out.println("");
	}
}