package dwf.web.sitemesh;

import java.io.IOException;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;

public class FastAndDumbHtmlParser implements PageParser{

	@Override
	public Page parse(final char[] page) throws IOException {
		Page p = new DumbHtmlPage(page);
		return p;
	}
}
