package dwf.security;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import dwf.user.domain.User;


@Transactional
public class DwfUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	protected SessionFactory sessionFactory;
	
	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Session session = sessionFactory.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<? extends User> users = session.createCriteria(User.class).add(Restrictions.eq("username", username)).add(Restrictions.eq("enabled", true)).list();
		if(users.size() == 0) {
			throw new UsernameNotFoundException("Login não encontrado");
		}
		User user = users.get(0);
		Hibernate.initialize(user.getRoles());
		return user;
	}
	
}
