package dbms.help;

import dbms.table.Table;
import dbms.table.TableManager;
import dbms.table.exceptions.HelpException;


public class HelpCommands {
	
	/*
	 * Handles printing of different help commands
	 */	
	
	public static boolean parseAndPrintHelpCommand(String statementString) throws HelpException {
		if (statementString == null) {
			return false;
		}
		// (?i) In regex is to make the command case insensitive
		if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)tables([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			printTables();
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)describe ([ \t\r\n\f]*)[a-zA-Z0-9_]+([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			// Get the table name from statement
			String tableName = statementString.replaceAll("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)describe ([ \t\r\n\f]*)", "").replaceAll("(?i)([ \t\r\n\f]*);([ \t\r\n\f]*)", "");
			printTableSchema(tableName);
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)create ([ \t\r\n\f]*)table([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			// TODO: Implement this help command
			System.out.println("Help on 'create table' command goes here.");
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)drop ([ \t\r\n\f]*)table([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			// TODO: Implement this help command
			System.out.println("Help on 'drop table' command goes here.");
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)select([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			// TODO: Implement this help command
			System.out.println("Help on 'select' command goes here.");
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)insert([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			// TODO: Implement this help command
			System.out.println("Help on 'insert' command goes here.");
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)delete([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			// TODO: Implement this help command
			System.out.println("Help on 'delete' command goes here.");
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)update([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			// TODO: Implement this help command
			System.out.println("Help on 'update' command goes here.");
			return true;
		} else {
			// No help command, return false
			return false;
		}
	}
	
	public static void printTables() {
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
	
	public static void printTableSchema(String tableName) throws HelpException {
		System.out.println("\nHelp Describe Table\n--------------------");
		if (!TableManager.tableExists(tableName)) {
			throw new HelpException("Table '"+tableName+"' does not exist.");
		} else {
			// TODO: Print the schema for the table
			System.out.println("**** SCHEMA GOES HERE!");
		}
		System.out.println("");
	}
}