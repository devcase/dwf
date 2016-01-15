package test.dwf.sample;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import dwf.sample.persistence.dao.CategoryDAO;
import dwf.sample.persistence.domain.Category;
import dwf.user.dao.BaseUserDAO;
import dwf.user.domain.BaseUser;
import dwf.user.domain.BaseUserRole;

@Configuration
@Profile("test")
public class ITScenario1 {
	@Autowired
	private BaseUserDAO baseUserDAO;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private CategoryDAO categoryDAO;
	
	static final String TEST_USER_1_EMAIL =  "sample@devcase.com.br";
	static final String TEST_USER_1_PASSWORD =  "sample@devcase.com.br";
	static final String TEST_USER_2_EMAIL =  "user@devcase.com.br";
	static final String TEST_USER_2_PASSWORD =  "user@devcase.com.br";

	@PostConstruct
	public void loadData() {
		BaseUser adminUser = baseUserDAO.findOrSaveNew(new BaseUser(TEST_USER_1_EMAIL, passwordEncoder.encode(TEST_USER_1_PASSWORD), null, BaseUserRole.BACKOFFICE_ADMIN));
		BaseUser userUser = baseUserDAO.findOrSaveNew(new BaseUser(TEST_USER_2_EMAIL, passwordEncoder.encode(TEST_USER_2_PASSWORD), null, BaseUserRole.BACKOFFICE_USER));
		
		
	}

}
