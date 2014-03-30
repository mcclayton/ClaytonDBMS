package dbms.table.constraints;

import java.util.ArrayList;



public class CheckConstraintList {
	public enum LOGICAL_OPERATOR {
		AND, OR, NONE
	}
	
	ArrayList<CheckConstraint> checkConstraintList = null;
	LOGICAL_OPERATOR logicalOperator = LOGICAL_OPERATOR.NONE;
	String fullCheckConstraintString = null;
	
	public CheckConstraintList(ArrayList<CheckConstraint> checkConstraintList, LOGICAL_OPERATOR logicalOperator, String fullCheckConstraintString) {
		this.checkConstraintList = checkConstraintList;
		this.logicalOperator = logicalOperator;
		this.fullCheckConstraintString = fullCheckConstraintString;
	}
	
	public CheckConstraintList(String fullCheckConstraintString) {
		this.checkConstraintList = new ArrayList<CheckConstraint>();
		logicalOperator = LOGICAL_OPERATOR.NONE;
		this.fullCheckConstraintString = fullCheckConstraintString;
	}
	
	public void addConstraint(CheckConstraint checkConstraint) {
		checkConstraintList.add(checkConstraint);
	}
	
	/* Getters and Setters */
	public ArrayList<CheckConstraint> getCheckConstraintList() {return this.checkConstraintList;}
	public LOGICAL_OPERATOR getLogicalOperator() {return this.logicalOperator;}
	public String getFullCheckConstraintString() {return fullCheckConstraintString;}
	
	public void setLogicalOperator(LOGICAL_OPERATOR logicalOperator) {this.logicalOperator = logicalOperator;}
}