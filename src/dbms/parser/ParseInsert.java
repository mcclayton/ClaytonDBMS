package dbms.parser;

import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import dbms.table.exceptions.InsertException;


public class ParseInsert {

	/* 
	 * Inserts values into an existing table from parsing an InsertStatement.
	 * 
	 * If successful, inserts values into the specified table in the TABLE_MAP
	 * If unsuccessful, throws an exception and values are not inserted.
	 */
	
	protected static void insertValuesFromStatement(TInsertSqlStatement pStmt) throws InsertException{
		String tableName = null;
		if (pStmt.getTargetTable() != null) {
			tableName = pStmt.getTargetTable().toString();
		}

		if (pStmt.getColumnList() != null) {
			throw new InsertException("Unsupported form of insertion method. Run 'HELP INSERT' for more information.", tableName);
		}
		
		if (pStmt.getSubQuery() != null) {
			throw new InsertException("Unsupported nested SQL statements. Run 'HELP INSERT' for more information.", tableName);
		}

		if (pStmt.getValues() != null) {
			System.out.println("values:");
			for(int i=0;i<pStmt.getValues().size();i++){
				TMultiTarget mt = pStmt.getValues().getMultiTarget(i);
				for(int j=0;j<mt.getColumnList().size();j++){
					System.out.println("\t"+mt.getColumnList().getResultColumn(j).toString());
				}
			}
		}
	}

}
