package dbms.table.constraints;



public class CheckConstraint {
	private String parentTableName = null;			// Table the constraint is being placed on
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
	public String getParentTable() {return this.parentTableName;}
	public String getParentColumn() {return this.parentColumnName;}
	public String getOperatorString() {return this.operator;}
	public String getConstantString() {return this.constant;}	
	
	/* Domain Verification Methods */
	// TODO: Implement these methods

	
}