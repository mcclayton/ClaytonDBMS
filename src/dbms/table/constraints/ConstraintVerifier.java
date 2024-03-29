package dbms.table.constraints;

import java.io.Serializable;
import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import dbms.parser.ParseCheckConstraint;
import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableColumn.DataType;
import dbms.table.TableRow;
import dbms.table.constraints.CheckConstraintList.LOGICAL_OPERATOR;
import dbms.table.exceptions.InsertException;



@SuppressWarnings("serial")
public class ConstraintVerifier implements Serializable {

	/* Domain Verification
	 * NOTE: If attributeValue is of type char, it should be passed in without quotes
	 * Checks @attributeValue and makes sure it satisfies all of the conditions in @constraint
	 */
	public static boolean passesCheckConstraints(String attributeValue, DataType attributeDataType, CheckConstraintList constraintList) throws ScriptException {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");

		// If the attribute data type is char, then add quotes around it so the expression evaluator knows it's a string 
		if (attributeDataType == DataType.CHAR) {
			attributeValue = "'"+attributeValue+"'";
		}

		if (constraintList.getLogicalOperator() == LOGICAL_OPERATOR.AND) {
			// Run through each domain constraint test, if it fails even one, then return false. If it makes it through them all, it will return true.
			for (CheckConstraint constraint : constraintList.getCheckConstraintList()) {
				String expression = attributeValue+" "+constraint.getOperatorString()+" "+constraint.getConstantString();
				if (engine.eval(expression).toString().equals("false")) {
					return false;
				}
			}
			return true;
		} else if (constraintList.getLogicalOperator() == LOGICAL_OPERATOR.OR) {
			// Run through each domain constraint test, if it passes even one, then return true. If it fails them all, it will return false.
			for (CheckConstraint constraint : constraintList.getCheckConstraintList()) {
				String expression = attributeValue+" "+constraint.getOperatorString()+" "+constraint.getConstantString()+";";
				if (engine.eval(expression).toString().equals("true")) {
					return true;
				}
			}
			return false;
		} else if (constraintList.getCheckConstraintList().size() == 1) {
			CheckConstraint constraint = constraintList.getCheckConstraintList().get(0);
			String expression = attributeValue+" "+constraint.getOperatorString()+" "+constraint.getConstantString();
			// Return the boolean result of evaluating the single domain constraint
			if (engine.eval(expression).toString().equals("false")) {
				return false;
			} else {
				return true;
			}
		}
		// No constraint, so return true
		else return true;
	}


	/*
	 * Checks to make sure that every value in @row that is part of a primary key is collectively unique
	 * as compared to the values that already exists in @table.
	 * 
	 * Returns true if the values in row pass the primary key constraint
	 * Returns false if the values in row fail the primary key constraint
	 */
	public static boolean passesPrimaryKeyConstraint(Table table, ArrayList<Object> row) {
		// Check to make sure all values in primary key are unique 
		ArrayList<Integer> indexList = new ArrayList<Integer>();		// List of indexes of the columns that are primary keys
		ArrayList<TableColumn> columns = table.getTableColumns();
		ArrayList<TableColumn> primaryColumns = table.getPrimaryKeyConstraint().getPrimaryColumnList();
		// Get the indexes of the columns that are primary keys
		for (TableColumn primaryColumn : primaryColumns) {
			if (columns.contains(primaryColumn)) {
				indexList.add(columns.indexOf(primaryColumn));
			}
		}
		// Make sure all attributes that form a primary key are collectively unique
		// Linear scan through each row of first primary column. When match is found, check rest of row for duplicates. If entire row is duplicates, throw exception. Otherwise continue linear scan through rows.
		if (indexList.size() > 0) {
			for (TableRow tableRow : table.getTableRows()) {
				if (row.get(indexList.get(0)).equals((String) tableRow.getElement(indexList.get(0)))) {
					boolean noneUnique = true;
					for (Integer index : indexList) {
						if (!row.get(index).equals((String) tableRow.getElement(index))) {
							noneUnique = false;
						}
					}
					if (noneUnique) {
						return false;	// Primary key constraint violated
					}
				}
			}
		}
		return true;
	}


	public static boolean passesForeignKeysConstraint(Table table, ArrayList<Object> row) {
		if (table.getForeignKeyConstraintList().size() > 0) { 
			// Check to make sure all values in primary key are unique 
			ArrayList<TableColumn> columns = table.getTableColumns();
			ArrayList<ForeignKeyConstraint> foreignKeyConstraints = table.getForeignKeyConstraintList();

			int index;
			String foreignKeyValue = null; 
			for (ForeignKeyConstraint foreignKey : foreignKeyConstraints) {
				if (columns.contains(foreignKey.getColumn())) {
					index = columns.indexOf(foreignKey.getColumn()); 	// Get the index of the columns that are the foreign keys
					foreignKeyValue = (String) row.get(index);			// Get the value of the row corresponding to this column

					// Get referenced table
					boolean foundMatch = false;
					Table referencedTable = foreignKey.getReferencedTable();
					int referencedColumnIndex = referencedTable.getTableColumns().indexOf(foreignKey.getReferencedColumn());
					for (TableRow referencedRow : referencedTable.getTableRows()) {
						if (foreignKeyValue.equals((String) referencedRow.getElement(referencedColumnIndex))) {
							foundMatch = true;
							break;
						}
					}
					if (foundMatch == false) {
						return false;
					}
				}
			}
			return true;
		} else {
			return true;
		}
	}
	
	
	public static DataType getValueDataType(String value) throws Exception {
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