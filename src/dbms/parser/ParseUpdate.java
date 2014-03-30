package dbms.parser;

import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;

import javax.script.ScriptException;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableColumn.DataType;
import dbms.table.TableManager;
import dbms.table.constraints.ConstraintVerifier;
import dbms.table.exceptions.UpdateException;


public class ParseUpdate {

	/* 
	 * Inserts values into an existing table from parsing an InsertStatement.
	 * 
	 * If successful, inserts values into the specified table in the TABLE_MAP
	 * If unsuccessful, throws an exception and values are not inserted.
	 */

	protected static void updateValuesFromStatement(TUpdateSqlStatement pStmt) throws UpdateException, Exception {
		// TODO: increment this counter each time a row is changed due to this update command
		int rowsAffected = 0;

		String tableName = null;
		if (pStmt.getTargetTable() != null) {
			tableName = pStmt.getTargetTable().toString();
			if (!TableManager.tableExists(tableName)) {
				throw new UpdateException("Table does not exist.", tableName);
			}
		}

		// Get the table object being updated
		Table table = TableManager.getTable(tableName);

		// Get the column being updated and the value it's being updated with, check to make sure the value passes all constraints
		TableColumn column;
		for(int i=0; i<pStmt.getResultColumnList().size(); i++) {
			column = null;
			TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);
			TExpression expression = resultColumn.getExpr();

			// Get the column of table being updated
			column = TableManager.getTableColumnByName(tableName, expression.getLeftOperand().toString());
			if (column == null) {
				throw new UpdateException("Trying to update value(s) of invalid column '"+expression.getLeftOperand().toString()+"'.", tableName);
			}

			String value = expression.getRightOperand().toString();

			// Check to make sure the value that the column is being updated with passes all constraints and is valid 	
			if (!passesAllConstraints(tableName, column, value)) {
				// passesAllConstraints should throw an exception if it doesn't pass, this is just an added precaution.
				throw new UpdateException("Value '"+value+"' violates a domain constraint.", tableName);
			}
			
			// TODO: Now that we know the value is valid, store the column and value somewhere so we can add them later after we have parsed the where clause

			System.out.println("\tcolumn:"+expression.getLeftOperand().toString()+"\tvalue:"+expression.getRightOperand().toString());
		}

		// TODO: Parse the where clause and update the matched rows with the new column values
		if (pStmt.getWhereClause() != null) {
			System.out.println("where clause:\n"+pStmt.getWhereClause().getCondition().toString());
		}

		// Update command successful
		System.out.println(rowsAffected+" row(s) affected.");
	}


	public static boolean passesAllConstraints(String tableName, TableColumn column, String value) throws UpdateException, Exception {
		// Check to make sure the value that the column is being updated with passes all constraints and is valid 			
		if (value == null) {
			throw new UpdateException("Trying to update column '"+column.getColumnName()+"' with invalid value.", tableName);
		}

		// Check to make sure that the datatypes of the values and the columns they are being inserted into have the same datatype
		if (ConstraintVerifier.getValueDataType(value) != column.getAttributeDataType()) {
			if (ConstraintVerifier.getValueDataType(value) == DataType.CHAR) {
				throw new UpdateException("Value "+value+" has different datatype than attribute '"+column.getColumnName()+"'.", tableName);
			} else {
				throw new UpdateException("Value '"+value+"' has different datatype than attribute '"+column.getColumnName()+"'.", tableName);
			}
		}

		// If type of value is CHAR, ensure the length constraint is not violated
		if (ConstraintVerifier.getValueDataType(value) == DataType.CHAR) {
			// Remove quotes from value
			if (value.startsWith("'")) {
				value = value.replace("'", "");
			} else if (value.startsWith("\"")) {
				value = value.replace("\"", "");
			}
			if (value.length() > column.getVarCharLength()) {
				throw new UpdateException("Trying to update column '"+column.getColumnName()+"' with value that violates 'CHAR("+column.getVarCharLength()+")' length constraint.", tableName);
			}
		}

		// Check domain constrains
		if (column.getCheckConstraintList() != null) {
			try {
				if (!ConstraintVerifier.passesCheckConstraints(value, ConstraintVerifier.getValueDataType("'"+value+"'"), column.getCheckConstraintList())) {
					throw new UpdateException("Value '"+value+"' violates a domain constraint.", tableName);
				}
			} catch (ScriptException e) {
				throw new UpdateException("Value '"+value+"' violates a domain constraint.", tableName);
			}
		}
		return true;
	}

}
