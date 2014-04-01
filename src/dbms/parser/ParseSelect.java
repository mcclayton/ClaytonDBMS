package dbms.parser;

import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import dbms.table.exceptions.SelectException;


public class ParseSelect {

	/* 
	 * Displays the tuples from the result set that is produced from parsing a Select Statement.
	 * 
	 * If successful, displays the tuples from the result set
	 * If unsuccessful, throws an exception and result set is not displayed
	 */

	protected static void parseAndPrintSelect(TSelectSqlStatement pStmt) throws SelectException {
		System.out.println("\nSelect:");
		
		// Make sure syntax of select statement is correct
		veryifySyntax(pStmt);

		//select list
		for(int i=0; i < pStmt.getResultColumnList().size();i++){
			TResultColumn resultColumn = pStmt.getResultColumnList().getResultColumn(i);
			System.out.printf("Column: %s, Alias: %s\n",resultColumn.getExpr().toString(), (resultColumn.getAliasClause() == null)?"":resultColumn.getAliasClause().toString());
		}

		//from clause, check this document for detailed information
		//http://www.sqlparser.com/sql-parser-query-join-table.php
		for(int i=0;i<pStmt.joins.size();i++){
			TJoin join = pStmt.joins.getJoin(i);
			switch (join.getKind()){
			case TBaseType.join_source_fake:
				System.out.printf("table: %s, alias: %s\n",join.getTable().toString(),(join.getTable().getAliasClause() !=null)?join.getTable().getAliasClause().toString():"");
				break;
			case TBaseType.join_source_table:
				System.out.printf("table: %s, alias: %s\n",join.getTable().toString(),(join.getTable().getAliasClause() !=null)?join.getTable().getAliasClause().toString():"");
				for(int j=0;j<join.getJoinItems().size();j++){
					TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
					System.out.printf("Join type: %s\n",joinItem.getJoinType().toString());
					System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
					if (joinItem.getOnCondition() != null){
						System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
					}else  if (joinItem.getUsingColumns() != null){
						System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
					}
				}
				break;
			case TBaseType.join_source_join:
				TJoin source_join = join.getJoin();
				System.out.printf("table: %s, alias: %s\n",source_join.getTable().toString(),(source_join.getTable().getAliasClause() !=null)?source_join.getTable().getAliasClause().toString():"");

				for(int j=0;j<source_join.getJoinItems().size();j++){
					TJoinItem joinItem = source_join.getJoinItems().getJoinItem(j);
					System.out.printf("source_join type: %s\n",joinItem.getJoinType().toString());
					System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
					if (joinItem.getOnCondition() != null){
						System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
					}else  if (joinItem.getUsingColumns() != null){
						System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
					}
				}

				for(int j=0;j<join.getJoinItems().size();j++){
					TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
					System.out.printf("Join type: %s\n",joinItem.getJoinType().toString());
					System.out.printf("table: %s, alias: %s\n",joinItem.getTable().toString(),(joinItem.getTable().getAliasClause() !=null)?joinItem.getTable().getAliasClause().toString():"");
					if (joinItem.getOnCondition() != null){
						System.out.printf("On: %s\n",joinItem.getOnCondition().toString());
					}else  if (joinItem.getUsingColumns() != null){
						System.out.printf("using: %s\n",joinItem.getUsingColumns().toString());
					}
				}

				break;
			default:
				System.out.println("unknown type in join!");
				break;
			}
		}

		//where clause
		if (pStmt.getWhereClause() != null){
			System.out.printf("where clause: \n%s\n", pStmt.getWhereClause().toString());
		}
	}


	protected static void veryifySyntax(TSelectSqlStatement pStmt) throws SelectException {
		// Query is a combined query statement
		if (pStmt.isCombinedQuery()){
			String setOper="";
			switch (pStmt.getSetOperator()){
			case 1: 
				setOper = "UNION";
				break;
			case 2: 
				setOper = "UNION ALL";
				break;
			case 3: 
				setOper = "INTERSECT";
				break;
			case 4: 
				setOper = "INTERSECT ALL";
				break;
			case 5:
				setOper = "MINUS";
				break;
			case 6: 
				setOper = "MINUS ALL";
				break;
			case 7: 
				setOper = "EXCEPT";
				break;
			case 8: 
				setOper = "EXCEPT ALL";
				break;
			}
			throw new SelectException("Set operation '"+setOper+"' is not supported.");
		}

		// group by
		if (pStmt.getGroupByClause() != null){
			throw new SelectException("'GROUP BY' operation is not supported.");
		}
		// order by
		if (pStmt.getOrderbyClause() != null){
			throw new SelectException("'ORDER BY' operation is not supported.");
		}
		// for update
		if (pStmt.getForUpdateClause() != null){
			throw new SelectException("'FOR UPDATE' operation is not supported.");
		}
		// top clause
		if (pStmt.getTopClause() != null){
			throw new SelectException("'TOP' operation is not supported.");
		}
		// limit clause
		if (pStmt.getLimitClause() != null){
			throw new SelectException("'LIMIT' operation is not supported.");
		}
	}

}