package dwf.user.service;

import static helper.BaseUserTestHelper.newBaseUser;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RunWith(MockitoJUnitRunner.class)
public class UserDetailsServiceImplTest {

	@Mock
	private BaseUserService baseUserServiceMock;
	
	private UserDetailsService service;
	
	@Before
	public void before() {
		this.service = new UserDetailsServiceImpl(baseUserServiceMock);
	}
	
	@Test(expected = UsernameNotFoundException.class)
	public void throwsUsernameNotFoundExceptionWhenUserNotFound() {
		when(baseUserServiceMock.findByEmail(anyString())).thenReturn(null);
		service.loadUserByUsername("invalid@devcase.com.br");
	}
	
	@Test
	public void returnLoggedUserWhenUserIsFound() {
		when(baseUserServiceMock.findByEmail(anyString())).thenReturn(newBaseUser("found@devcase.com.br"));
		
		final UserDetails user = service.loadUserByUsername("found@devcase.com.br");
		assertThat(user.getUsername(), equalTo("found@devcase.com.br"));
	}
	
}
