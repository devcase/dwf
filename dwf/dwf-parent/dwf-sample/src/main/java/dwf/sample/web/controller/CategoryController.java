package dwf.sample.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dwf.sample.persistence.domain.Category;
import dwf.web.controller.BaseCrudController;

@Controller
@RequestMapping("/category/")
public class CategoryController extends BaseCrudController<Category, Long> {

	public CategoryController() {
		super(Category.class);
	}

}
