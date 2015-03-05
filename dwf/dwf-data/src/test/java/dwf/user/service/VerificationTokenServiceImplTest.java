package dwf.user.service;

import static helper.BaseUserTestHelper.newBaseUser;
import static helper.VerificationTokenTestHelper.expiredVerificationToken;
import static helper.VerificationTokenTestHelper.validVerificationToken;
import static helper.VerificationTokenTestHelper.verifiedVerificationToken;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
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
	public void sendEmailConfirmationSuccess() {
		when(baseUserDAOMock.findFirstByFilter("username", "travenup")).thenReturn(newBaseUser("travenup"));
		
		service.sendEmailConfirmation("travenup");
		
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
	public void confirmTokenSuccess() {
		final BaseUser userSpy = spy(newBaseUser("travenup"));
		final VerificationToken tokenSpy = spy(validVerificationToken());
		tokenSpy.setUser(userSpy);
		
		when(verificationTokenDAOMock.findFirstByFilter("token", "abcdef1234")).thenReturn(tokenSpy);
		service.confirmToken("abcdef1234");
		
		verify(tokenSpy).setVerified(true);
		verify(verificationTokenDAOMock).updateByAnnotation(tokenSpy);

		verify(userSpy).setVerified(true);
		verify(baseUserDAOMock).updateByAnnotation(userSpy);
	}
}
