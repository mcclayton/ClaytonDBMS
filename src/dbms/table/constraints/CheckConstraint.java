package dbms.table.constraints;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import dbms.table.TableColumn;
import dbms.table.TableColumn.DataType;
import dbms.table.constraints.CheckConstraintList.LOGICAL_OPERATOR;



public class CheckConstraint {
	private String parentTableName = null;
	private String parentColumnName = null;			// Column on which the constraint is being placed on
	private String operator = null;					// String representation of the operator
	private String constant = null;					// String representation of the constant

	public CheckConstraint(String parentTableName, String parentColumnName, String operator, String constant) {
		this.parentTableName = parentTableName;
		this.parentColumnName = parentColumnName;
		this.operator = operator;
		this.constant = constant;
	}


	/* Getters and Setters */
	public String getParentTableName() {return this.parentTableName;}
	public String getParentColumnName() {return this.parentColumnName;}
	public String getOperatorString() {return this.operator;}
	public String getConstantString() {return this.constant;}	

	/* Domain Verification Methods */
	// TODO: Implement these methods

	/*
	 * Checks @attributeValue and makes sure it satisfies all of the conditions in @constraint
	 */
	public static boolean passesCheckConstraints(String attributeValue, DataType attributeDataType, CheckConstraintList constraintList) throws ScriptException {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");

		// If the attribute data type is char, then add quotes around it so the expression evaluator knows it's a string 
		if (attributeDataType == DataType.CHAR) {
			// TODO: Verify that the attributeValue.length() of type char(LENGTH) is not greater than LENGTH
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

}