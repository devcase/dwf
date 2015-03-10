package dwf.user.service;

import static helper.BaseUserTestHelper.newBaseUser;
import static helper.VerificationTokenTestHelper.expiredVerificationToken;
import static helper.VerificationTokenTestHelper.validEmailConfirmationToken;
import static helper.VerificationTokenTestHelper.validResetPasswordToken;
import static helper.VerificationTokenTestHelper.verifiedVerificationToken;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.validation.ValidationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import dwf.user.dao.BaseUserDAO;
import dwf.user.dao.VerificationTokenDAO;
import dwf.user.domain.BaseUser;
import dwf.user.domain.TokenType;
import dwf.user.domain.VerificationToken;

@RunWith(MockitoJUnitRunner.class)
public class VerificationTokenServiceImplTest {

	@Mock
	private BaseUserDAO baseUserDAOMock;
	
	@Mock
	private VerificationTokenDAO verificationTokenDAOMock;
	
	@Mock
	private MailSender mailSenderMock;
	
	private VerificationTokenService service;
	
	@Before
	public void before() {
		this.service = new VerificationTokenServiceImpl(verificationTokenDAOMock, baseUserDAOMock, mailSenderMock);
	}
	
	@Test
	public void generateAndSendEmailConfirmationTokenSuccess() {
		when(baseUserDAOMock.findByEmail("user@devcase.com.br")).thenReturn(newBaseUser("user@devcase.com.br"));
		
		service.generateAndSendToken("user@devcase.com.br", TokenType.EMAIL_CONFIRMATION);
		
		InOrder inOrder = inOrder(verificationTokenDAOMock, mailSenderMock); 
		inOrder.verify(verificationTokenDAOMock).saveNew(any(VerificationToken.class));
		inOrder.verify(mailSenderMock).send(any(SimpleMailMessage.class));
	}
	
	@Test
	public void generateAndSendResetPasswordTokenSuccess() {
		when(baseUserDAOMock.findByEmail("user@devcase.com.br")).thenReturn(newBaseUser("user@devcase.com.br"));
		
		service.generateAndSendToken("user@devcase.com.br", TokenType.RESET_PASSWORD);
		
		InOrder inOrder = inOrder(verificationTokenDAOMock, mailSenderMock); 
		inOrder.verify(verificationTokenDAOMock).saveNew(any(VerificationToken.class));
		inOrder.verify(mailSenderMock).send(any(SimpleMailMessage.class));
	}
	
	@Test(expected = ValidationException.class)
	public void confirTokenThrowsValidationExceptionIfTokenDoesNotExist() {
		when(verificationTokenDAOMock.findFirstByFilter("token", "abcdef1234")).thenReturn(null);
		service.confirmToken("abcdef1234");
	}
	
	@Test(expected = ValidationException.class)
	public void confirmTokenThrowsValidationExceptionIfTokenHasExpired() {
		when(verificationTokenDAOMock.findFirstByFilter("token", "abcdef1234")).thenReturn(expiredVerificationToken());
		service.confirmToken("abcdef1234");
	}
	
	@Test(expected = ValidationException.class)
	public void confirmTokenThrowsValidationExceptionIfTokenIsVerified() {
		when(verificationTokenDAOMock.findFirstByFilter("token", "abcdef1234")).thenReturn(verifiedVerificationToken());
		service.confirmToken("abcdef1234");
	}
	
	@Test
	public void confirmEmailConfirmationTokenSuccess() {
		final BaseUser userSpy = spy(newBaseUser("user@devcase.com.br"));
		final VerificationToken tokenSpy = spy(validEmailConfirmationToken());
		tokenSpy.setUser(userSpy);
		
		when(verificationTokenDAOMock.findByToken("abcdef1234")).thenReturn(tokenSpy);
		service.confirmToken("abcdef1234");
		
		verify(tokenSpy).setVerified(true);
		verify(verificationTokenDAOMock).updateByAnnotation(tokenSpy);

		verify(userSpy).setVerified(true);
		verify(baseUserDAOMock).updateByAnnotation(userSpy);
	}
	
	@Test
	public void confirmResetPasswordTokenSuccess() {
		final BaseUser userSpy = spy(newBaseUser("user@devcase.com.br"));
		final VerificationToken tokenSpy = spy(validResetPasswordToken());
		tokenSpy.setUser(userSpy);
		
		when(verificationTokenDAOMock.findByToken("abcdef1234")).thenReturn(tokenSpy);
		service.confirmToken("abcdef1234");
		
		verify(tokenSpy).setVerified(true);
		verify(verificationTokenDAOMock).updateByAnnotation(tokenSpy);

		verify(userSpy, never()).setVerified(true);
		verify(baseUserDAOMock, never()).updateByAnnotation(userSpy);
	}
	
	@Test
	public void generateTokenSuccessfully() {
		service.generateToken("user@devcase.com.br", TokenType.CHANGE_PASSWORD);
		
		verify(baseUserDAOMock).findByEmail("user@devcase.com.br");
		verify(verificationTokenDAOMock).saveNew(any(VerificationToken.class));
	}
	
	@Test
	public void testFindByToken() {
		service.findByToken("abcdef1234");
		verify(verificationTokenDAOMock).findByToken("abcdef1234");
	}
}
