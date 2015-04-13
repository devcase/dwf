package dwf.persistence.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.stereotype.Component;

import dwf.config.DwfDataConfig;

@Component
public class DdlTool {
	private static Log log = LogFactory.getLog(DdlTool.class);

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private LocalSessionFactoryBean sessionFactoryBean;
	@Autowired
	private DwfDataConfig dwfConfig;


	public String[] generateSchemaCreationScript()  throws SQLException, IOException, ClassNotFoundException {
		Connection conn = dataSource.getConnection();
		try {
			Dialect dialect = new StandardDialectResolver().resolveDialect(new DatabaseMetaDataDialectResolutionInfoAdapter(conn.getMetaData()));
			return generateSchemaCreationScript(dialect);
		} finally {
			conn.close();
		}
	}
	
	public String[] generateSchemaCreationScript(Dialect dialect)  throws SQLException, IOException, ClassNotFoundException {
		Configuration cfg = sessionFactoryBean.getConfiguration();
		String originalDefaultSchema = cfg.getProperty(Environment.DEFAULT_SCHEMA);
		try {
			cfg.getProperties().put( Environment.DEFAULT_SCHEMA , dwfConfig.getDatabaseSchema());
			return cfg.generateSchemaCreationScript(dialect);
		} finally {
			//restore previous default schema
			if(originalDefaultSchema == null) {
				cfg.getProperties().remove(Environment.DEFAULT_SCHEMA);
			} else {
				cfg.getProperties().put(Environment.DEFAULT_SCHEMA, originalDefaultSchema);
			}
		}
	}
	
	public String[] generateDropSchemaScript()  throws SQLException, IOException, ClassNotFoundException {
		Configuration cfg = sessionFactoryBean.getConfiguration();
		Connection conn = dataSource.getConnection();
		String originalDefaultSchema = cfg.getProperty(Environment.DEFAULT_SCHEMA);
		try {
			Dialect dialect = new StandardDialectResolver().resolveDialect(new DatabaseMetaDataDialectResolutionInfoAdapter(conn.getMetaData()));
			DatabaseMetadata database = new DatabaseMetadata(conn, dialect, cfg);
			cfg.getProperties().put( Environment.DEFAULT_SCHEMA , dwfConfig.getDatabaseSchema());
			
			return cfg.generateDropSchemaScript(dialect);
		} finally {
			//restore previous default schema
			if(originalDefaultSchema == null) {
				cfg.getProperties().remove(Environment.DEFAULT_SCHEMA);
			} else {
				cfg.getProperties().put(Environment.DEFAULT_SCHEMA, originalDefaultSchema);
			}
			conn.close();
		}
	}
	
	public List<SchemaUpdateScript> generateSchemaUpdateScriptList()  throws SQLException, IOException, ClassNotFoundException {
		Configuration cfg = sessionFactoryBean.getConfiguration();
		Connection conn = dataSource.getConnection();
		String originalDefaultSchema = cfg.getProperty(Environment.DEFAULT_SCHEMA);
		try {
			Dialect dialect = new StandardDialectResolver().resolveDialect(new DatabaseMetaDataDialectResolutionInfoAdapter(conn.getMetaData()));
			DatabaseMetadata database = new DatabaseMetadata(conn, dialect, cfg);
			cfg.getProperties().put( Environment.DEFAULT_SCHEMA , dwfConfig.getDatabaseSchema());
			
			return cfg.generateSchemaUpdateScriptList(dialect, database);
		} finally {
			//restore previous default schema
			if(originalDefaultSchema == null) {
				cfg.getProperties().remove(Environment.DEFAULT_SCHEMA);
			} else {
				cfg.getProperties().put(Environment.DEFAULT_SCHEMA, originalDefaultSchema);
			}
			conn.close();
		}
	}
	
	public String generateSchema() throws SQLException, IOException, ClassNotFoundException {
		StringBuilder sql = new StringBuilder();
		sql.append("/* DROP SCHEMA */\n");
		String[] dropSchemaScript = generateDropSchemaScript();
    	for (String script : dropSchemaScript) {
			sql.append(script + ";\n");
		}
    	
		String[] schemaCreationScript = generateSchemaCreationScript();
		sql.append("\n/* SCHEMA CREATION */\n");
    	for (String script : schemaCreationScript) {
			sql.append(script + ";\n");
		}
    	
		List<SchemaUpdateScript> schemaUpdateScript = generateSchemaUpdateScriptList();
		sql.append("\n/* SCHEMA UPDATE */\n");
    	for (SchemaUpdateScript script : schemaUpdateScript) {
			sql.append(script.getScript() + ";\n");
		}
    	
    	return sql.toString();

	}
}
