package dwf.persistence.utils;

import org.hibernate.AssertionFailure;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Value;



public class DwfNamingStrategy implements NamingStrategy {
	@Value("${dwf.data.tablePrefix:}")
	private String tablePrefix;
	
	public String getTablePrefix() {
		return tablePrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	public DwfNamingStrategy() {
		super();
	}
	
	/**
	 * Return the unqualified class name, mixed case converted to
	 * underscores
	 */
	public String classToTableName(String className) {
		return tablePrefix + normalizeName( StringHelper.unqualify(className) );
	}
	/**
	 * Return the full property path with underscore seperators, mixed
	 * case converted to underscores
	 */
	public String propertyToColumnName(String propertyName) {
		if(propertyName.contains(".collection&&element")) propertyName = propertyName.replace(".collection&&element", "");
		return columnName( propertyName );
	}
	/**
	 * Convert mixed case to underscores
	 */
	public String tableName(String tableName) {
		return normalizeName(tableName);
	}
	/**
	 * Convert mixed case to underscores
	 */
	public String columnName(String columnName) {
		if("user".equalsIgnoreCase(columnName)) { //user Ã© palavra reservada no postgres
			return "user_";
		}
		if("order".equalsIgnoreCase(columnName)) { //order Ã© palavra reservada no postgres
			return "order_";
		}
		return normalizeName(columnName);
	}

	protected static String normalizeName(String name) {
		StringBuilder buf = new StringBuilder( name );
		for (int i=0; i < buf.length(); i++) {
			char currentChar = buf.charAt(i);
			if(currentChar == '_') { 
			} else if(currentChar == '.') {
				buf.setCharAt(i, '_');
			} else if (Character.isAlphabetic( currentChar )) {
				buf.setCharAt(i, Character.toLowerCase(currentChar));
			} else if(!Character.isLetterOrDigit(currentChar)) {
				buf.deleteCharAt(i);
			}
		}
		return buf.toString();
	}

	public String collectionTableName(
			String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable,
			String propertyName
	) {
		String ret =new StringBuffer(tablePrefix)
				.append(ownerEntityTable != null ? ownerEntityTable : StringHelper.unqualify(ownerEntity))
				.append("_")
				.append(propertyName != null ? StringHelper.unqualify( propertyName ) : associatedEntityTable).toString();
		return normalizeName(ret);

	}

	/**
	 * Return the argument
	 */
	public String joinKeyColumnName(String joinedColumn, String joinedTable) {
		return columnName( joinedColumn );
	}

	/**
	 * Return the property name or propertyTableName
	 */
	public String foreignKeyColumnName(
			String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName
	) {
		String prefix = propertyName != null ? propertyName : StringHelper.unqualify(propertyEntityName);
		return columnName( prefix + "_" + referencedColumnName ); //+ "_" + referencedColumnName not used for backward compatibility
	}

	/**
	 * Return the column name or the unqualified property name
	 */
	public String logicalColumnName(String columnName, String propertyName) {
		String ret =StringHelper.isNotEmpty( columnName ) ? columnName : propertyName;
		if(ret.contains(".collection&&element")) ret = ret.replace(".collection&&element", "");
		return columnName(ret);
	}

	/**
	 * Returns either the table name if explicit or
	 * if there is an associated table, the concatenation of owner entity table and associated table
	 * otherwise the concatenation of owner entity table and the unqualified property name
	 */
	public String logicalCollectionTableName(String tableName,
											 String ownerEntityTable, String associatedEntityTable, String propertyName
	) {
		if ( tableName != null ) {
			return tableName;
		}
		else {
			String ret =new StringBuffer(ownerEntityTable)
					.append(
							associatedEntityTable != null ?
							associatedEntityTable :
							StringHelper.unqualify( propertyName )
						).toString();
			return normalizeName(ret);
		}
	}
	/**
	 * Return the column name if explicit or the concatenation of the property name and the referenced column
	 */
	public String logicalCollectionColumnName(String columnName, String propertyName, String referencedColumn) {
		return columnName(StringHelper.isNotEmpty( columnName ) ?
				columnName :
				StringHelper.unqualify( propertyName ) + "_" + referencedColumn);
	}
}
