package dwf.test;

import javax.sql.DataSource;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import dwf.config.DwfConfig;
import dwf.persistence.utils.DdlTool;

@RunWith(DwfJUnit4ClassRunner.class)
@ContextConfiguration(loader=DwfTestContextLoader.class)
@WebAppConfiguration()
public class TestBase implements ApplicationContextAware {

	protected ApplicationContext applicationContext;
	
	@Autowired
	protected DataSource dataSource;
	@Autowired
	private DdlTool ddlTool;
	@Autowired
	private DwfConfig dwfConfig;
	
	@Before
	public void setupTestDatabase() throws Exception {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		String[] commands = new String[] {"drop schema " + dwfConfig.getDatabaseSchema() + " restrict", "create schema " + dwfConfig.getDatabaseSchema() };
		commands = ArrayUtils.addAll(commands, ddlTool.generateDropSchemaScript());
		commands = ArrayUtils.addAll(commands, ddlTool.generateSchemaCreationScript());
		for (String command : commands) {
			try {
				System.out.println(command);
				template.execute(command);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
