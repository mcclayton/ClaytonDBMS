package dbms.help;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableColumn.DataType;
import dbms.table.TableManager;
import dbms.table.constraints.ForeignKeyConstraint;
import dbms.table.exceptions.HelpException;


public class HelpCommands {

	/*
	 * Handles printing of different help commands
	 */	


	/*
	 * Determine which help command was entered and execute it.
	 * If no valid help command was entered, then return false 
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
		
		// TODO: Add parameter 'userid' and only print subschema that 'userid' is allowed to see. 
		
		System.out.println("\nHelp Describe Table\n--------------------");
		if (!TableManager.tableExists(tableName)) {
			throw new HelpException("Table '"+tableName+"' does not exist.");
		}

		// If the table does not exist, throw an exception
		Table table = TableManager.getTable(tableName);
		if (table == null) {
			throw new HelpException("Table '"+tableName+"' does not exist.");
		}

		// Print column names/datatypes/constraints
		for (TableColumn column : table.getTableColumns()) {
			// Print column name
			System.out.print(column.getColumnName());

			// Print datatype
			if (column.getAttributeDataType() != null) {
				if (column.getAttributeDataType() == DataType.CHAR) {
					System.out.print(" -- char("+column.getVarCharLength()+")");
				} else {
					System.out.print(" -- "+column.getAttributeDataType().toString().toLowerCase());
				}
			}

			// Print the primary key constraint if this column is one
			if (table.getPrimaryKeyConstraint().getPrimaryColumnList().contains(column)) {
				System.out.print(" -- primary key");
			}

			// Print the foreign key constraint if this column is one
			for (ForeignKeyConstraint foreignKey : table.getForeignKeyConstraintList()) {
				if (foreignKey.getColumn().equals(column)) {
					System.out.print(" -- foreign key references "+foreignKey.getReferencedTable().getTableName()+"("+foreignKey.getReferencedColumn().getColumnName()+")");
				}
			}

			// Print domain constraints
			if (column.getCheckConstraint() != null) {
				System.out.println(" -- "+column.getCheckConstraint());
			} 
		}

		System.out.println("");
	}
}