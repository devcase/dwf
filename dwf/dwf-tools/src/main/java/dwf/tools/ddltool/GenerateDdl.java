package dwf.tools.ddltool;

import java.io.PrintStream;

import javax.sql.DataSource;

import org.hibernate.tool.hbm2ddl.SchemaUpdateScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import dwf.config.DwfDataConfig;
import dwf.persistence.utils.DdlTool;

@EnableAutoConfiguration
public class GenerateDdl {
	
	@Autowired
	private DwfDataConfig dwfConfig;
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
		printStream.println(dwfConfig.getDatabaseSchema());
		try {
			for (SchemaUpdateScript updateScript : ddlTool.generateSchemaUpdateScriptList()) {
				printStream.println(updateScript.getScript());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	
}
