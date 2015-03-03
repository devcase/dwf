package dwf.user.service;

import static helper.BaseUserTestHelper.newBaseUser;
import static helper.ChangePasswordBeanTestHelper.invalidChangePasswordBean;
import static helper.ChangePasswordBeanTestHelper.validChangePasswordBean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.validation.ValidationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import dwf.user.dao.BaseUserDAO;
import dwf.user.domain.BaseUser;
import dwf.user.utils.LoggedUser;

@RunWith(MockitoJUnitRunner.class)
public class BaseUserServiceImplTest {

	@Mock
	private BaseUserDAO daoMock;
	
	@Mock
	private PasswordEncoder passwordEncoderMock;
	
	@Mock
	private LoggedUser loggedUser;
	
	private BaseUserService service;
	
	@Before
	public void before() {
		this.service = new BaseUserServiceImpl(daoMock, passwordEncoderMock, loggedUser);
	}
	
	@Test(expected = ValidationException.class)
	public void throwsValidationExceptionWhenChangePasswordBeanIsInvalid() {
		service.changePassword(invalidChangePasswordBean());
	}
	
	@Test(expected = ValidationException.class)
	public void throwsValidationExceptionWhenUsernameIsNull() {
		service.changePassword(validChangePasswordBean());
	}
	
	@Test(expected = ValidationException.class)
	public void throwsValidationExceptionWhenDatabasePasswordIsDifferent() {
		final BaseUser user = newBaseUser("travenup");
		
		when(loggedUser.getUsername()).thenReturn("travenup");
		when(daoMock.findFirstByFilter("username", "travenup")).thenReturn(user);
		when(passwordEncoderMock.matches(anyString(), anyString())).thenReturn(false);
		
		service.changePassword(validChangePasswordBean());
	}
	
	@Test
	public void testValidChangepasswordBean() {
		final BaseUser user = newBaseUser("travenup");
		
		when(loggedUser.getUsername()).thenReturn("travenup");
		when(daoMock.findFirstByFilter("username", "travenup")).thenReturn(user);
		when(passwordEncoderMock.matches(anyString(), anyString())).thenReturn(true);
		
		service.changePassword(validChangePasswordBean());
		
		verify(passwordEncoderMock).encode(anyString());
		verify(daoMock).updateByAnnotation(user);
	}
	
	@Test
	public void findByUsernameCallsFindFirstByFilter() {
		service.findByUsername("travenup");
		
		verify(daoMock).findFirstByFilter("username", "travenup");
	}
}
