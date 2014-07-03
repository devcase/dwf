package dwf.web.sitemesh;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.JstlView;

import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.PageParserSelector;
import com.opensymphony.module.sitemesh.RequestConstants;
import com.opensymphony.module.sitemesh.filter.PageResponseWrapper;

/**
 * Wraps a JstlView and apply a decoration based on Sitemesh
 * 
 * @author Hirata
 * 
 */
public class SitemeshView extends JstlView {

	@Override
	protected RequestDispatcher getRequestDispatcher(final HttpServletRequest request, final String path) {
		// return request.getRequestDispatcher(path);

		final RequestDispatcher defaultDispatcher = request.getRequestDispatcher(path);
		return new RequestDispatcher() {
			@Override
			public void include(ServletRequest exposedRequest, ServletResponse response) throws ServletException, IOException {

				PageResponseWrapper pageResponseWrapper = wrapResponse(response);
				defaultDispatcher.include(exposedRequest, pageResponseWrapper);
				decorateAndDispatch((DumbHtmlPage) pageResponseWrapper.getPage(), exposedRequest, response);
			}

			@Override
			public void forward(ServletRequest exposedRequest, ServletResponse response) throws ServletException, IOException {
				// defaultDispatcher.forward(request, z);
				PageResponseWrapper pageResponseWrapper = wrapResponse(response);
				defaultDispatcher.forward(exposedRequest, pageResponseWrapper);
				decorateAndDispatch((DumbHtmlPage) pageResponseWrapper.getPage(), exposedRequest, response);
			}

			private PageResponseWrapper wrapResponse(ServletResponse response) {
				return new PageResponseWrapper((HttpServletResponse) response, new PageParserSelector() {
					@Override
					public boolean shouldParsePage(String contentType) {
						return true;
					}

					@Override
					public PageParser getPageParser(String contentType) {
						return new FastAndDumbHtmlParser();
					}
				});
			}

			private void decorateAndDispatch(DumbHtmlPage page, ServletRequest exposedRequest, ServletResponse response) throws ServletException, IOException {

				if (page != null) {
					// guarda previous decorated page - multiple includes
					DumbHtmlPage originalPage = (DumbHtmlPage) request.getAttribute("dwf_decoratedPage");

					request.setAttribute(RequestConstants.PAGE, page);
					request.setAttribute("dwf_decoratedPage", page);
					String decorator = exposedRequest.getParameter("decorator");
					if(decorator == null) {
						page.getProperty("meta.decorator");
					}
					if(decorator == null) {
						decorator = (String) exposedRequest.getAttribute("decorator");
					}
					if(decorator == null || decorator.equals("none")) {
						page.writePage(response.getWriter());
						return;
					} else if(decorator.equals("table")) {
						page.writeTable(response.getWriter());
						return;
					}
					RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/decorators/" + decorator + ".jsp");
					dispatcher.include(request, response);
					request.removeAttribute("dwf_decoratedPage");
					request.removeAttribute(RequestConstants.PAGE);

					// restaura antigo valor de dwf_decoratedPage
					request.setAttribute("dwf_decoratedPage", originalPage);
				}

			}
		};
	}

}
