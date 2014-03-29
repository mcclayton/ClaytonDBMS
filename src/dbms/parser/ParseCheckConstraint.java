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
			CheckConstraintList checkConstraintList = new CheckConstraintList();
			String attribute = null;
			String operator = null;
			String constant = null;
			DataType constantType = DataType.UNKNOWN;
			
			// Determine what kind of token each is
			for (int i=0; i < tokenList.size(); i++) {
				String token = tokenList.get(i).toString();
				
				if (token.matches("(?i)([ \t\r\n\f]*)and([ \t\r\n\f]*)")) {
					//System.out.println("LOGICAL_OPERATOR: '"+token+"'");
					checkConstraintList.setLogicalOperator(LOGICAL_OPERATOR.AND);
				} else if (token.matches("(?i)([ \t\r\n\f]*)or([ \t\r\n\f]*)")) {
					//System.out.println("LOGICAL_OPERATOR: '"+token+"'");
					checkConstraintList.setLogicalOperator(LOGICAL_OPERATOR.OR);
				} else if (token.matches("[0-9]+")) {
					//System.out.println("CONSTANT_INT: '"+token+"'");
					constantType = DataType.INT;
					constant = token;
				} else if (isDouble(token)) {
					//System.out.println("CONSTANT_DOUBLE: '"+token+"'");
					constantType = DataType.DECIMAL;
					constant = token;
				} else if (token.matches("[a-zA-Z0-9_]+")) {
					attribute = token;
					if (!attribute.equals(parentColumnName)) {
						throw new CreateTableException("Domain constraint on invalid attribute '"+attribute+"'.", parentTableName);
					}
					//System.out.println("ATTRIBUTE: '"+token+"'");
				} else if (token.matches("'(.*?)'") || token.matches("\"(.*?)\"")) {
					//System.out.println("CONSTANT_STRING: '"+token+"'");
					constantType = DataType.CHAR;
					constant = token;
				} else if (token.equals("=") || token.equals("!=") || token.equals(">") || token.equals("<") || token.equals(">=") || token.equals("<=")) {
					//System.out.println("OPERATOR: '"+token+"'");
					if (token.equals("=")) {
						operator = "==";
					} else {
						operator = token;
					}
				} else if (token.matches("([ \t\r\n\f]*)")) {
					// Skip white space
				} else {
					// Likely error if at this point
					throw new CreateTableException("Invalid token '"+token+"' in domain constraint for attribite '"+parentColumnName+"'.", parentTableName);
				}
				
				// TODO: Throw an exception if there is ever an incomplete expression i.e. ATTRIBUTE OPERATOR _______
				// TODO: Throw an exception if more than one of the above tokens are found in the same condition. i.e. ATTRIBUTE OPERATOR OPERATOR CONSTANT
				
				// Full expression found
				if (attribute != null && operator != null && constant != null) {
					// Throw an exception if the constant type does not match the datatype of the parentColumn
					if (columnDataType != constantType) {
						throw new CreateTableException("Domain constraint compares attribute '"+attribute+"' of datatype '"+columnDataType.toString()+"' to constant of different type '"+constantType.toString()+"'.", parentTableName);
					}
					
					// Create a constraint using information
					checkConstraintList.addConstraint(new CheckConstraint(parentTableName, parentColumnName, operator, constant));
					// Reset information
					attribute = null;
					operator = null;
					constant = null;
				}
			}
			return checkConstraintList;
	}
	
	private static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
	
}