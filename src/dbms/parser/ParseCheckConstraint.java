package dbms.parser;

import gudusoft.gsqlparser.TSourceTokenList;
import gudusoft.gsqlparser.nodes.TConstraint;
import dbms.table.TableColumn.DataType;
import dbms.table.constraints.CheckConstraint;
import dbms.table.constraints.CheckConstraintList;
import dbms.table.constraints.CheckConstraintList.LOGICAL_OPERATOR;
import dbms.table.exceptions.CreateTableException;


public class ParseCheckConstraint {
	
	public static CheckConstraintList parseList(TConstraint constraint, String parentTableName, String parentColumnName, DataType columnDataType) throws CreateTableException {
			TSourceTokenList tokenList = new TSourceTokenList();
			constraint.getCheckCondition().addAllMyTokensToTokenList(tokenList, 0); // Put all tokens into list
			CheckConstraintList checkConstraintList = new CheckConstraintList(constraint.getCheckCondition().toString());
			String attribute = null;
			String operator = null;
			String constant = null;
			DataType constantType = DataType.UNKNOWN;
			
			// Determine what kind of token each is
			for (int i=0; i < tokenList.size(); i++) {
				String token = tokenList.get(i).toString();
				
				if (token.matches("(?i)([ \t\r\n\f]*)and([ \t\r\n\f]*)")) {
					// Logical And
					if (checkConstraintList.getLogicalOperator() == LOGICAL_OPERATOR.OR) {
						throw new CreateTableException("Only one type of logical operator allowed per domain constraint.", parentTableName);
					}
					checkConstraintList.setLogicalOperator(LOGICAL_OPERATOR.AND);
				} else if (token.matches("(?i)([ \t\r\n\f]*)or([ \t\r\n\f]*)")) {
					// Logical Or
					if (checkConstraintList.getLogicalOperator() == LOGICAL_OPERATOR.AND) {
						throw new CreateTableException("Only one type of logical operator allowed per domain constraint.", parentTableName);
					}
					checkConstraintList.setLogicalOperator(LOGICAL_OPERATOR.OR);
				} else if (token.matches("[0-9]+")) {
					// Constant Int
					constantType = DataType.INT;
					constant = token;
				} else if (isDouble(token)) {
					// Constant Decimal
					constantType = DataType.DECIMAL;
					constant = token;
				} else if (token.matches("[a-zA-Z0-9_]+")) {
					// Attribute Name
					if (attribute != null) {
						throw new CreateTableException("Invalid domain constraint ("+constraint.getCheckCondition().toString()+").", parentTableName);
					}
					attribute = token;
					if (!attribute.equals(parentColumnName)) {
						throw new CreateTableException("Domain constraint on invalid attribute '"+attribute+"'.", parentTableName);
					}
				} else if (token.matches("'(.*?)'") || token.matches("\"(.*?)\"")) {
					// Constant String
					constantType = DataType.CHAR;
					constant = token;
				} else if (token.equals("=") || token.equals("!=") || token.equals(">") || token.equals("<") || token.equals(">=") || token.equals("<=")) {
					// Operator
					if (token.equals("=")) {
						operator = "==";
					} else {
						operator = token;
					}
				} else if (token.matches("([ \t\r\n\f]*)")) {
					// Skip white space
				} else {
					// Invalid token
					throw new CreateTableException("Invalid token '"+token+"' in domain constraint for attribite '"+parentColumnName+"'.", parentTableName);
				}
				
				// Full expression found
				if (attribute != null && operator != null && constant != null) {
					// Throw an exception if the constant type does not match the datatype of the parentColumn
					if (columnDataType != constantType) {
						throw new CreateTableException("Domain constraint compares attribute '"+attribute+"' of datatype '"+columnDataType.toString()+"' to constant of different type '"+constantType.toString()+"'.", parentTableName);
					}
					
					// Create a constraint using the information
					checkConstraintList.addConstraint(new CheckConstraint(parentTableName, parentColumnName, operator, constant));
					// Reset information
					attribute = null;
					operator = null;
					constant = null;
				}
			}
			// Throw an exception if there is a partial domain constraint.
			if (attribute != null || operator != null || constant != null) {
				// This is really just added precaution. This should be handled as a syntax error by the sql parser.
				throw new CreateTableException("Invalid domain constraint ("+constraint.getCheckCondition().toString()+").", parentTableName);
			}
			
			return checkConstraintList;
	}
	
	public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
	
}