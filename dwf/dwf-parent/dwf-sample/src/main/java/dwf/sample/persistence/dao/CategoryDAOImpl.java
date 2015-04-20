package dwf.sample.persistence.dao;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import dwf.sample.persistence.domain.Category;
import dwf.persistence.dao.BaseDAOImpl;

@Repository("categoryDAO")
@Transactional
public class CategoryDAOImpl extends BaseDAOImpl<Category> implements CategoryDAO{

	public CategoryDAOImpl() {
		super(Category.class);
	}

}