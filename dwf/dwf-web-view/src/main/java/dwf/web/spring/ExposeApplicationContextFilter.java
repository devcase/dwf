package dwf.web.spring;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.filter.OncePerRequestFilter;

public class ExposeApplicationContextFilter extends OncePerRequestFilter {
	
	@Autowired
	private ApplicationContext wac;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		request.setAttribute("applicationContext", wac);
		request.setAttribute("environment", wac.getEnvironment());
		filterChain.doFilter(request, response);
	}
	
}
