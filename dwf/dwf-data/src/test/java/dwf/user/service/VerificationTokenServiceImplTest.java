package dwf.user.service;

import static helper.BaseUserTestHelper.newBaseUser;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

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
}
