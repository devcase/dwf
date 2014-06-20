package dwf.persistence.utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.stereotype.Component;

import dwf.config.DwfConfig;

@Component
public class DdlTool {
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private LocalSessionFactoryBean sessionFactory;
	@Autowired
	private DwfConfig dwfConfig;
	

	public String generateSchema() throws SQLException, IOException, ClassNotFoundException {
		Configuration cfg = sessionFactory.getConfiguration();
		Connection conn = dataSource.getConnection();
		StringBuilder sql = new StringBuilder();
		String originalDefaultSchema = cfg.getProperty(Environment.DEFAULT_SCHEMA);
		try {
			DatabaseMetadata database = new DatabaseMetadata(conn, new Oracle10gDialect(), cfg);
			cfg.getProperties().put( Environment.DEFAULT_SCHEMA , dwfConfig.getDatabaseSchema());
			
			String[] dropSchemaScript = cfg.generateDropSchemaScript(new Oracle10gDialect());
			sql.append("/* DROP SCHEMA */\n");
	    	for (String script : dropSchemaScript) {
    			sql.append(script + ";\n");
			}
	    	
			String[] schemaCreationScript = cfg.generateSchemaCreationScript(new Oracle10gDialect());
			sql.append("\n/* SCHEMA CREATION */\n");
	    	for (String script : schemaCreationScript) {
    			sql.append(script + ";\n");
			}
	    	
			List<SchemaUpdateScript> schemaUpdateScript = cfg.generateSchemaUpdateScriptList(new Oracle10gDialect(), database);
			sql.append("\n/* SCHEMA UPDATE */\n");
	    	for (SchemaUpdateScript script : schemaUpdateScript) {
    			sql.append(script.getScript() + ";\n");
			}
		} finally {
			//restore previous default schema
			if(originalDefaultSchema == null) {
				cfg.getProperties().remove(Environment.DEFAULT_SCHEMA);
			} else {
				cfg.getProperties().put(Environment.DEFAULT_SCHEMA, originalDefaultSchema);
			}
			conn.close();
		}
		
		return sql.toString();
	}
}
