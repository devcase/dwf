package dwf.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Creates an object that returns a timestamp for {@link #toString()} and adds it to the request scope
 * as an attribute named "timestamp"
 * @author Hirata
 *
 */
public class TimestampFilter  extends OncePerRequestFilter {

	static Object timestamp = new Object() {
		@Override
		public String toString() {
			return String.valueOf(System.currentTimeMillis());
		}
		
	};
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		request.setAttribute("timestamp", timestamp);
		filterChain.doFilter(request, response);
	}

}
