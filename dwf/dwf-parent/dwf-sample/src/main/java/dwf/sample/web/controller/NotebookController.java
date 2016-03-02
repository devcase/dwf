package dwf.sample.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dwf.sample.persistence.domain.Notebook;
import dwf.utils.ParsedMap;
import dwf.utils.SingleValueParsedMap;
import dwf.web.controller.BaseCrudController;

@Controller
@RequestMapping("/notebook/")
public class NotebookController extends BaseCrudController<Notebook, Long> {

	public NotebookController() {
		super(Notebook.class);
	}

	@Override
	protected ParsedMap baseFilter() {
		return new SingleValueParsedMap("baseUser", getCurrentBaseUser());
	}

	@Override
	protected void prepareForSaveOrUpdate(Notebook entidade) {
		entidade.setBaseUser(getCurrentBaseUser());
	}
}
