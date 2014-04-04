package dbms.table.constraints;

import java.io.Serializable;



@SuppressWarnings("serial")
public class CheckConstraint implements Serializable {
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
}