package dbms.parser;

import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;

import java.util.ArrayList;

import javax.script.ScriptException;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableColumn.DataType;
import dbms.table.TableManager;
import dbms.table.TableRow;
import dbms.table.constraints.ConstraintVerifier;
import dbms.table.exceptions.InsertException;


public class ParseInsert {

	/* 
	 * Inserts values into an existing table from parsing an InsertStatement.
	 * 
	 * If successful, inserts values into the specified table in the TABLE_MAP
	 * If unsuccessful, throws an exception and values are not inserted.
	 */

	protected static void insertValuesFromStatement(TInsertSqlStatement pStmt) throws InsertException{
		String tableName = null;
		if (pStmt.getTargetTable() != null) {
			tableName = pStmt.getTargetTable().toString();
			if (!TableManager.tableExists(tableName)) {
				throw new InsertException("Table does not exist.", tableName);
			}
		}

		// User tried to specify which particular values go into which tables
		if (pStmt.getColumnList() != null) {
			throw new InsertException("Unsupported form of insertion method. Run 'HELP INSERT' for more information.", tableName);
		}

		// User tried to use nested SQL statements
		if (pStmt.getSubQuery() != null) {
			throw new InsertException("Unsupported nested SQL statements. Run 'HELP INSERT' for more information.", tableName);
		}

		// Get the table object being inserted into
		Table table = TableManager.getTable(tableName);

		ArrayList<TableColumn> columnList = table.getTableColumns();
		// Check to see if the number of values being inserted is equal to the number of columns in the table
		if (pStmt.getValues() == null) {
			throw new InsertException("No values specified to insert.", tableName);
		} else if (pStmt.getValues().size() != 1) {
			// User tried to insert multiple lists of values at a time. Not supported by Oracle syntax.
			throw new InsertException("Unsupported form of insertion method. Run 'HELP INSERT' for more information.", tableName);
		} else if (columnList != null) {
			// Throw an exception if user tries to insert fewer or more values than attributes in the table
			if (columnList.size() != ((TMultiTarget)pStmt.getValues().getMultiTarget(0)).getColumnList().size()) {
				throw new InsertException("Number of values being inserted don't match number of table attributes.", tableName);
			}
		}


		ArrayList<Object> row = new ArrayList<Object>();	// The list of objects (values) that will form a row
		// Get the values being inserted into the table
		if (pStmt.getValues() != null) {
			TMultiTarget mt = pStmt.getValues().getMultiTarget(0);
			String value;
			TableColumn column;
			// For every value, check to make sure constraints aren't violated, add to a row, and add the row to the table
			for(int i=0; i<mt.getColumnList().size(); i++) {
				value = mt.getColumnList().getResultColumn(i).toString();
				column = columnList.get(i);

				// Check to make sure that the datatypes of the values and the columns they are being inserted into have the same datatype
				if (getValueDataType(value) != column.getAttributeDataType()) {
					if (getValueDataType(value) == DataType.CHAR) {
						throw new InsertException("Value "+value+" has different datatype than attribute '"+column.getColumnName()+"'.", tableName);
					} else {
						throw new InsertException("Value '"+value+"' has different datatype than attribute '"+column.getColumnName()+"'.", tableName);
					}
				}

				// If type of value is CHAR, ensure the length constraint is not violated
				if (getValueDataType(value) == DataType.CHAR) {
					// Remove quotes from value
					if (value.startsWith("'")) {
						value = value.replace("'", "");
					} else if (value.startsWith("\"")) {
						value = value.replace("\"", "");
					}
					if (value.length() > column.getVarCharLength()) {
						throw new InsertException("Value '"+value+"' is too long for type 'CHAR("+column.getVarCharLength()+")'.", tableName);
					}
				}

				// Check domain constrains
				if (column.getCheckConstraintList() != null) {
					try {
						if (!ConstraintVerifier.passesCheckConstraints(value, getValueDataType(value), column.getCheckConstraintList())) {
							throw new InsertException("Value '"+value+"' violates a domain constraint.", tableName);
						}
					} catch (ScriptException e) {
						throw new InsertException("Value '"+value+"' violates a domain constraint.", tableName);
					}
				}

				// TODO: Check foreign key constraints


				// Add the object value to a value list (row).
				row.add(value);
			}
			
			// Check primary key constraints
			if(!ConstraintVerifier.passesPrimaryKeyConstraint(table, row)) {
				throw new InsertException("Primary key constraint violated.", tableName); 
			}
			
			
		}
		// Add the value list (row) to the table.
		table.addRow(new TableRow(row));
		System.out.println("Tuple inserted successfully.");
	}

	public static DataType getValueDataType(String value) throws InsertException {
		if (value.matches("[0-9]+")) {
			// Constant Int
			return DataType.INT;
		} else if (ParseCheckConstraint.isDouble(value)) {
			// Constant Decimal
			return DataType.DECIMAL;
		} else if (value.matches("'(.*?)'") || value.matches("\"(.*?)\"")) {
			// Constant String
			return DataType.CHAR;
		} else if (value.matches("([ \t\r\n\f]*)")) {
			// Skip white space
		}
		// Invalid token if at this point
		throw new InsertException("Invalid value '"+value+"'.");
	}

}
