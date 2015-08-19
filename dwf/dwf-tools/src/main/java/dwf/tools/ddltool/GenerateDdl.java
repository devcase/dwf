package dwf.tools.ddltool;

import java.io.PrintStream;

import javax.sql.DataSource;

import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import dwf.persistence.utils.DdlTool;

@EnableAutoConfiguration
public class GenerateDdl {
	@Value("${dwf.data.databaseSchema:public}")
	private String databaseSchema; 
	
	public String getDatabaseSchema() {
		return databaseSchema;
	}

	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}

	@Autowired
	private DataSource dataSource;
	@Autowired
	private DdlTool ddlTool;
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(GenerateDdl.class);
		app.setWebEnvironment(false);
		ConfigurableApplicationContext applicationContext = app.run();
		
		GenerateDdl bean = applicationContext.getBean(GenerateDdl.class);
		bean.printUpdateScript(System.out);
		
	}
	
	public void printUpdateScript(PrintStream printStream) {
		printStream.println(dataSource);
		printStream.println(databaseSchema);
		try {
			for (SchemaUpdateScript updateScript : ddlTool.generateSchemaUpdateScriptList()) {
				printStream.println(updateScript.getScript());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	
}
