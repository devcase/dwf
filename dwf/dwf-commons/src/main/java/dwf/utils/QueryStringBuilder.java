package dwf.utils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Example:
 * <jsp:useBean id="queryStringBuilder" class="dwf.utils.QueryStringBuilder">
 *    <jsp:setProperty name
 * </jsp:useBean>
 * 
 * 
 * 
 * @author hirata
 *
 */
public class QueryStringBuilder {
	private final Map<String, String[]> params;

	public QueryStringBuilder() {
		this.params = new HashMap<String, String[]>();
	}
	public QueryStringBuilder(int initialCapacity) {
		this.params = new HashMap<String, String[]>(initialCapacity);
	}

	public Map<String, String[]> getParams() {
		return params;
	}

	public QueryStringBuilder fromRequest(HttpServletRequest request) {
		QueryStringBuilder qb = new QueryStringBuilder(request.getParameterMap().size() + this.params.size());
		qb.params.putAll(this.params);
		qb.params.putAll(request.getParameterMap());
		return qb;
	}
	
	public QueryStringBuilder without(String... keys) {
		QueryStringBuilder qb = new QueryStringBuilder(request.getParameterMap().size() + this.params.size());
		qb.params.putAll(this.params);
		qb.params.putAll(request.getParameterMap());
		return qb;
	}
}
