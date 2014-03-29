package dbms.parser;

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
			System.out.println("Help Create Table\n-----------------");
			System.out.println("\nThis command creates a new table in the database.");
			System.out.println("\nSyntax:\n\tCREATE TABLE table_name (\n\tattribute_1 attribute1_type CHECK (constraint1),\n\tattribute_2 attribute2_type, ...,\n\tPRIMARY KEY ( attribute_1, attribute_2 ),\n\tFOREIGN KEY ( attribute_y ) REFERENCES table_x ( attribute_t ),\n\tFOREIGN KEY ( attribute_w ) REFERENCES table_y ( attribute_z )\n\t...\n\t);\n");
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)drop ([ \t\r\n\f]*)table([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			System.out.println("Help Drop Table\n---------------");
			System.out.println("\nThis command removes an existing table from the database.");
			System.out.println("\nSyntax:\n\tDROP TABLE table_name;\n");
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)select([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			System.out.println("Help Select\n-----------");
			System.out.println("\nThis command selects data from a database. It outputs the list of matching tuples if there is no error.");
			System.out.println("\nSyntax:\n\tSELECT attribute_1, attribute2, ...\n\tFROM table_1, table_2, ...");
			System.out.println("\t[Optional] WHERE condition_1, condition_2, ... ;\n\t\tEach condition has the form: attribute_1 operator value_1\n\t\tOR\n\t\tattribute_1 operator value_1 AND/OR attribute_2 operator value_2 AND/OR attribute_3 ...\n");
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)insert([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			System.out.println("Help Insert\n-----------");
			System.out.println("\nThis command inserts a new record into a table.");
			System.out.println("\nSyntax:\n\tINSERT INTO table_name VALUES ( val1, val2, ... );\n");
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)delete([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			System.out.println("Help Delete\n-----------");
			System.out.println("\nThis command removes records from a table.");
			System.out.println("\nSyntax:\n\tDELETE FROM table_name");
			System.out.println("\t[Optional] WHERE condition_1, condition_2, ... ;\n\t\tEach condition has the form: attribute_1 operator value_1\n\t\tOR\n\t\tattribute_1 operator value_1 AND/OR attribute_2 operator value_2 AND/OR attribute_3 ...\n");
			return true;
		} else if (statementString.matches("(?i)([ \t\r\n\f]*)help ([ \t\r\n\f]*)update([ \t\r\n\f]*);([ \t\r\n\f]*)")) {
			System.out.println("Help Update\n-----------");
			System.out.println("\nThis command updates records in a table.");
			System.out.println("\nSyntax:\n\tUPDATE table_name SET attr_1 = val_1, attr_2 = val_2...");
			System.out.println("\t[Optional] WHERE condition_1, condition_2, ... ;\n\t\tEach condition has the form: attribute_1 operator value_1\n\t\tOR\n\t\tattribute_1 operator value_1 AND/OR attribute_2 operator value_2 AND/OR attribute_3 ...\n");
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
			if (column.getCheckConstraintList() != null) {
				if (column.getCheckConstraintList().getFullCheckConstraintString() != null) {
					System.out.println(" -- "+column.getCheckConstraintList().getFullCheckConstraintString());
				}
			} 
		}

		System.out.println("");
	}
}