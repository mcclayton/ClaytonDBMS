package dbms.parser;

import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;

import java.util.ArrayList;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableManager;
import dbms.table.constraints.ForeignKeyConstraint;
import dbms.table.exceptions.DropTableException;


public class ParseDropTable {

	/* 
	 * Drops an existing table from parsing a DropTableStatement.
	 * 
	 * If successful, removes the table from the TABLE_MAP
	 * If unsuccessful, throws an exception and table is not removed.
	 */
	protected static void dropTableFromStatement(TDropTableSqlStatement pStmt) throws DropTableException {
		String tableName = pStmt.getTableName().toString();
		if (tableName == null) {
			throw new DropTableException("Table cannot have null name.");
		}
		
		// Make sure the table being dropped exists.
		Table tableBeingDropped = TableManager.getTable(tableName);
		if (tableBeingDropped == null){
			throw new DropTableException("Table does not exist.", tableName);
		}
		
		// Check if any other table has a foreign constraint specified on an attribute of this table
		ArrayList<TableColumn> tableBeingDroppedColumns = tableBeingDropped.getTableColumns();
		for (Table table : TableManager.getTableMap().values()) {
			for (ForeignKeyConstraint foreignKey : table.getForeignKeyConstraintList()) {
				if (TableManager.getTableColumnByName(tableBeingDroppedColumns, foreignKey.getReferencedColumn().getColumnName()) != null) {
					throw new DropTableException("Table is referenced by a foreign key belonging to table '"+table.getTableName()+"'.", tableName);
				}
			}
		}
		
		// Drop the table
		TableManager.removeTable(tableName);
		System.out.println("Table dropped successfully.");
	}

}
