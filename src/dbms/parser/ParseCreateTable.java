package dbms.parser;

import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TConstraint;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;

import java.util.ArrayList;

import javax.script.ScriptException;

import dbms.table.Table;
import dbms.table.TableColumn;
import dbms.table.TableColumn.DataType;
import dbms.table.TableManager;
import dbms.table.constraints.CheckConstraint;
import dbms.table.constraints.CheckConstraintList;
import dbms.table.constraints.ForeignKeyConstraint;
import dbms.table.constraints.PrimaryKeyConstraint;
import dbms.table.exceptions.AttributeException;
import dbms.table.exceptions.CreateTableException;


public class ParseCreateTable {
	private static String tableName = null;

	/* 
	 * Creates a new table from parsing a CreateTableStatement.
	 * 
	 * If successful, adds the new table to the TABLE_MAP
	 * If unsuccessful, throws an exception and table is not created.
	 */
	protected static Table createTableFromStatement(TCreateTableSqlStatement pStmt) throws CreateTableException, AttributeException {
		tableName = pStmt.getTargetTable().toString();
		if (tableName == null) {
			throw new CreateTableException("Table cannot have null name.");
		}

		// Parse Columns
		ArrayList<TableColumn> tableColumnList = new ArrayList<TableColumn>();
		TColumnDefinition column;
		String columnName;
		DataType columnDataType;
		String attributeTypeString = null;
		String columnCheckConstraint = null;
		PrimaryKeyConstraint primaryKey = new PrimaryKeyConstraint();
		ArrayList<ForeignKeyConstraint> foreignKeyList = new ArrayList<ForeignKeyConstraint>();
		for(int i=0; i<pStmt.getColumnList().size(); i++){
			column = pStmt.getColumnList().getColumn(i);

			// Get column name
			columnName = column.getColumnName().toString();

			// Get the column data type
			if (column.getDatatype() != null) {
				attributeTypeString = column.getDatatype().toString();
				columnDataType = TableColumn.getAttributeType(attributeTypeString);
				if (columnDataType == DataType.UNKNOWN) {
					throw new AttributeException("Invalid attribute datatype '"+attributeTypeString+"'.");
				}
			} else {
				throw new CreateTableException("Column '"+columnName+"' cannot have null datatype.", tableName);
			}

			// Get in-line column 'check' constraints
			if (column.getConstraints() != null) {
				for(int j=0; j<column.getConstraints().size(); j++) {
					columnCheckConstraint = getCheckConstraint(column.getConstraints().getConstraint(j), tableName, columnName, columnDataType);
				}
			}	

			// Add the new column to the column list
			// Make sure attribute name is unique
			for (TableColumn tbleColumn : tableColumnList) {
				if (tableName.equals(tbleColumn.getTableName()) && tbleColumn.getColumnName().equals(columnName)) {
					throw new CreateTableException("Table attribute names must be unique. Attribute '"+columnName+"' is already defined in this table.", tableName);
				}
			}
			try {
				tableColumnList.add(new TableColumn(tableName, columnName, attributeTypeString, columnCheckConstraint));
			} catch (AttributeException e) {
				if (tableName != null) {
					throw new CreateTableException("\n\t|\n\t\\-->\t"+e.getMessage(), tableName);
				} else {
					throw new CreateTableException("\n\t|\n\t\\-->\t"+e.getMessage());
				}
			}
		}		

		// Get table key constraints
		if(pStmt.getTableConstraints().size() > 0) {
			for(int i=0; i<pStmt.getTableConstraints().size(); i++) {
				getTableKeyConstraints(pStmt.getTableConstraints().getConstraint(i), primaryKey, foreignKeyList, tableColumnList);
			}
		}

		Table table = new Table(tableName, tableColumnList);
		if (!primaryKey.getPrimaryColumnList().isEmpty()) {
			table.setPrimaryKeyConstraint(primaryKey);
		} else {
			throw new CreateTableException("No primary key specified for this table.", tableName);
		}
		table.setForeignKeyConstraintList(foreignKeyList);
		TableManager.addTable(table.getTableName(), table);
		System.out.println("Table created successfully");
		return table;
	}


	protected static String getCheckConstraint(TConstraint constraint, String tableName, String columnName, DataType columnDataType) throws CreateTableException {
		switch(constraint.getConstraint_type()) {
		case check:
			// TODO: Return constraint object
			CheckConstraintList checkConstraintList = ParseCheckConstraint.parseList(constraint, tableName, columnName, columnDataType);
			// TODO: Remove this later -- just here for testing
			/*
			System.out.print("FULL EXPRESSION:");
			for (CheckConstraint checkConstraint : checkConstraintList.getCheckConstraintList()) {
				System.out.print(" "+checkConstraint.getParentColumnName()+" "+checkConstraint.getOperatorString()+" "+checkConstraint.getConstantString()+" "+checkConstraintList.getLogicalOperator());
			}
			try {
				System.out.println(" >>> "+columnDataType.toString()+" --  "+CheckConstraint.passesCheckConstraints("Michael", columnDataType, checkConstraintList));
			} catch (ScriptException e) {
				System.out.println(e);
			}
			System.out.println();
			*/
			return constraint.getCheckCondition().toString();
		case primary_key:
			throw new CreateTableException("Primary key must be specified after all attributes are listed.", tableName);
		case foreign_key:
		case reference:
			throw new CreateTableException("Foreign keys must be specified after all attributes are listed.", tableName);
		default:
			throw new CreateTableException("Only 'CHECK' domain constraints are supported in-line. Cannot specify '"+constraint.toString()+"' constraint here.", tableName);
		}
	}


	protected static void getTableKeyConstraints(TConstraint constraint, PrimaryKeyConstraint primaryKey, ArrayList<ForeignKeyConstraint> foreignKeyList, ArrayList<TableColumn> columnList) throws CreateTableException {

		switch(constraint.getConstraint_type()){
		case primary_key:
			if (constraint.getColumnList() != null) {
				if (!primaryKey.getPrimaryColumnList().isEmpty()) {
					throw new CreateTableException("Only one primary key or composite primary key is allowed per table.", tableName);
				}
				for(int k=0; k<constraint.getColumnList().size(); k++) {
					TableColumn column = TableManager.getTableColumnByName(columnList, constraint.getColumnList().getObjectName(k).toString());
					if (column != null) {
						primaryKey.addPrimaryColumn(column);
					} else {
						throw new CreateTableException("Specifying primary key on an invalid attribute '"+constraint.getColumnList().getObjectName(k).toString()+"'.", tableName);
					}
				}
			}
			break;
		case foreign_key:
		case reference:
			if (!TableManager.tableExists(constraint.getReferencedObject().toString())) {
				throw new CreateTableException("Foreign key references table that does not exist.", tableName);
			}

			// Create foreign key from referenced table name
			ForeignKeyConstraint foreignKey = new ForeignKeyConstraint(TableManager.getTable(constraint.getReferencedObject().toString()));

			// Add the table columns to the foreign key that will reference other attributes
			if (constraint.getColumnList() != null) {
				for(int k=0; k<constraint.getColumnList().size(); k++) {
					// Make sure the attribute the foreign key is specified on is in the table being created
					TableColumn column = TableManager.getTableColumnByName(columnList, constraint.getColumnList().getObjectName(k).toString());
					if (column != null) {
						if (foreignKey.getColumn() != null) {
							throw new CreateTableException("Composite foreign keys are not supported.", tableName);
						}
						foreignKey.setColumn(column);
					} else {
						throw new CreateTableException("Specifying foreign key on an invalid attribute '"+constraint.getColumnList().getObjectName(k).toString()+"'.", tableName);
					}					
				}
			}

			// Add the referenced columns to the foreign key
			if (constraint.getReferencedColumnList() != null){
				for(int k=0; k<constraint.getReferencedColumnList().size(); k++){					
					// Make sure the attribute the foreign key references is in the table being created
					TableColumn referencedColumn = TableManager.getTableColumnByName(constraint.getReferencedObject().toString(), constraint.getReferencedColumnList().getObjectName(k).toString());
					if (referencedColumn != null) {
						TObjectName constraintObject = constraint.getColumnList().getObjectName(k);
						if (constraintObject == null) {
							throw new CreateTableException("Composite foreign keys are not supported.", tableName);
						}
						TableColumn column = TableManager.getTableColumnByName(columnList, constraintObject.toString());	
						// Redundant sanity check, this is already handled above, but just wanted to make sure
						if (column == null) {
							throw new CreateTableException("Specifying foreign key on an invalid attribute '"+constraint.getColumnList().getObjectName(k).toString()+"'.", tableName);
						}
						// Make sure the referenced column is the primary key of the referenced table (must be ONLY primary key of the table i.e. not part of composite key)
						Table referencedTable = TableManager.getTable(constraint.getReferencedObject().toString());
						if (!referencedTable.getPrimaryKeyConstraint().getPrimaryColumnList().contains(referencedColumn)) {
							throw new CreateTableException("Foreign key on attribute '"+column.getColumnName()+"' references attribute '"+referencedColumn.getColumnName()+"' which is not a primary key.", tableName);
						} else if (referencedTable.getPrimaryKeyConstraint().getPrimaryColumnList().size() > 1) {
							throw new CreateTableException("Foreign key on attribute '"+column.getColumnName()+"' references attribute '"+referencedColumn.getColumnName()+"' which is part of a composite primary key.", tableName);
						}
						
						// Make sure column and referenced column both have the same type
						if (!(referencedColumn.getAttributeDataType() == column.getAttributeDataType())) {
							throw new CreateTableException("Foreign key on attribute '"+column.getColumnName()+"' references attribute '"+referencedColumn.getColumnName()+"' which has a different datatype.", tableName);
						}
						if (foreignKey.getReferencedColumn() != null) {
							throw new CreateTableException("Composite foreign keys are not supported.", tableName);
						}
						foreignKey.setReferencedColumn(referencedColumn);
					} else {
						throw new CreateTableException("Foreign key references an invalid attribute '"+constraint.getReferencedColumnList().getObjectName(k).toString()+"'.", tableName);
					}					
				}
			}

			// Add the foreignKey to the foreignKeyList
			foreignKeyList.add(foreignKey);
			break;
		default:
			throw new CreateTableException("Expected primary or foreign key constraint. Cannot specify '"+constraint.toString()+"' constraint here.", tableName);
		}
	}

}
