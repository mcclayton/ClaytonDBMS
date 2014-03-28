package dbms.table.constraints;

import java.util.ArrayList;



public class CheckConstraintList {
	public enum LOGICAL_OPERATOR {
		AND, OR, NONE
	}
	
	ArrayList<CheckConstraint> checkConstraintList = null;
	LOGICAL_OPERATOR logicalOperator = LOGICAL_OPERATOR.NONE;
	
	public CheckConstraintList(ArrayList<CheckConstraint> checkConstraintList, LOGICAL_OPERATOR logicalOperator) {
		this.checkConstraintList = checkConstraintList;
		this.logicalOperator = logicalOperator;
	}
	
	public CheckConstraintList() {
		this.checkConstraintList = new ArrayList<CheckConstraint>();
		logicalOperator = null;
	}
	
	public void addConstraint(CheckConstraint checkConstraint) {
		checkConstraintList.add(checkConstraint);
	}
	
	/* Getters and Setters */
	public ArrayList<CheckConstraint> getCheckConstraintList() {return this.checkConstraintList;}
	public LOGICAL_OPERATOR getLogicalOperator() {return this.logicalOperator;}	
	
	public void setLogicalOperator(LOGICAL_OPERATOR logicalOperator) {this.logicalOperator = logicalOperator;}
}