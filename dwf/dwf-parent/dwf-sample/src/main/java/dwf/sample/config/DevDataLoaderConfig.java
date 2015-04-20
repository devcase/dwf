package dwf.sample.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import dwf.user.dao.BaseUserDAO;
import dwf.user.domain.BaseUser;
import dwf.user.domain.BaseUserRole;


@Configuration
@Profile("dev")
public class DevDataLoaderConfig {
	@Autowired
	private BaseUserDAO baseUserDAO;
	
	@PostConstruct
	public void loadData() {
		BaseUser defaultUser = baseUserDAO.findOrSaveNew(new BaseUser("sample@devcase.com.br", null, null, BaseUserRole.BACKOFFICE_ADMIN));
	}
}
