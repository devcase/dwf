package dwf.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro que disponibiliza o atributo appPath no escopo de request. Para
 * facilitar o uso de EL (ex: ${appPath}/resources/css/style.css)
 * @author Hirata
 *
 */
public class AppPathFilter extends OncePerRequestFilter  {
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String contextPath = httpRequest.getContextPath();
		request.setAttribute("appPath", contextPath);
		filterChain.doFilter(request, response);
	}

}
