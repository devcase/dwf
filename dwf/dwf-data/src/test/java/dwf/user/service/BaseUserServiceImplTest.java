package dwf.user.service;

import static helper.BaseUserTestHelper.newBaseUser;
import static helper.ChangePasswordBeanTestHelper.invalidChangePasswordBean;
import static helper.ChangePasswordBeanTestHelper.validChangePasswordBean;
import static helper.ResetPasswordBeanTestHelper.invalidResetPasswordBean;
import static helper.ResetPasswordBeanTestHelper.validResetPasswordBean;
import static helper.VerificationTokenTestHelper.validResetPasswordToken;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.validation.ValidationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import dwf.persistence.validation.ValidationGroups;
import dwf.user.dao.BaseUserDAO;
import dwf.user.domain.BaseUser;
import dwf.user.domain.TokenType;
import dwf.user.domain.VerificationToken;
import dwf.user.utils.LoggedUser;

@RunWith(MockitoJUnitRunner.class)
public class BaseUserServiceImplTest {

	@Mock
	private BaseUserDAO daoMock;
	
	@Mock
	private VerificationTokenService verificationTokenServiceDAOMock;
	
	@Mock
	private PasswordEncoder passwordEncoderMock;
	
	@Mock
	private LoggedUser loggedUserMock;
	
	private BaseUserService service;
	
	@Before
	public void before() {
		this.service = new BaseUserServiceImpl(daoMock, verificationTokenServiceDAOMock, 
				passwordEncoderMock, loggedUserMock);
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
		final BaseUser user = newBaseUser("user@devcase.com.br");
		
		when(loggedUserMock.getEmail()).thenReturn("user@devcase.com.br");
		when(daoMock.findByEmail("user@devcase.com.br")).thenReturn(user);
		when(passwordEncoderMock.matches(anyString(), anyString())).thenReturn(false);
		
		service.changePassword(validChangePasswordBean());
	}
	
	@Test
	public void testValidChangepasswordBean() {
		final BaseUser user = newBaseUser("user@devcase.com.br");
		
		when(loggedUserMock.getEmail()).thenReturn("user@devcase.com.br");
		when(daoMock.findByEmail("user@devcase.com.br")).thenReturn(user);
		when(passwordEncoderMock.matches(anyString(), anyString())).thenReturn(true);
		
		service.changePassword(validChangePasswordBean());
		
		verify(passwordEncoderMock).encode(anyString());
		verify(daoMock).updateByAnnotation(user, ValidationGroups.ChangePassword.class);
	}
	
	@Test
	public void findByEmail() {
		service.findByEmail("user@devcase.com.br");
		
		verify(daoMock).findByEmail("user@devcase.com.br");
	}
	
	@Test(expected = ValidationException.class)
	public void throwsValidationExceptionWhenResetBeanIsInvalid() {
		service.resetPasswordChange("abcdef1234", invalidResetPasswordBean());
	}
	
	@Test(expected = ValidationException.class)
	public void throwsValidationExceptionWhenTokenIsNull() {
		service.resetPasswordChange("abcdef1234", validResetPasswordBean());
	}
	
	@Test
	public void testValidResetPasswordChange() {
		final BaseUser userSpy = spy(newBaseUser("user@devcase.com.br"));
		final VerificationToken tokenSpy = spy(validResetPasswordToken());
		tokenSpy.setUser(userSpy);
		
		when(verificationTokenServiceDAOMock.findByToken("abcdef1234")).thenReturn(tokenSpy);
		
		service.resetPasswordChange("abcdef1234", validResetPasswordBean());
		
		verify(passwordEncoderMock).encode(anyString());
		verify(userSpy).setHashedpass(anyString());
		verify(daoMock).updateByAnnotation(userSpy, ValidationGroups.ChangePassword.class);
		verify(verificationTokenServiceDAOMock).verifyToken(tokenSpy);
	}
	
	@Test(expected = ValidationException.class)
	public void throwsValidationExceptionWhenResetPasswordRequestUserIsNull() {
		when(daoMock.findByEmail("teste@email.com")).thenReturn(null);
		service.resetPasswordRequest("teste@email.com");
	}
	
	@Test
	public void testValidResetPasswordRequest() {
		when(daoMock.findByEmail("user@devcase.com.br")).thenReturn(newBaseUser("user@devcase.com.br"));
		
		service.resetPasswordRequest("user@devcase.com.br");
		
		verify(verificationTokenServiceDAOMock).generateAndSendToken("user@devcase.com.br", TokenType.RESET_PASSWORD);
	}
}
