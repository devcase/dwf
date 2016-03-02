package dwf.sample.persistence.dao;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import dwf.persistence.dao.BaseDAOImpl;
import dwf.sample.persistence.domain.Notebook;

@Repository("notebookDAO")
@Transactional
public class NotebookDAOImpl extends BaseDAOImpl<Notebook> implements NotebookDAO{

	public NotebookDAOImpl() {
		super(Notebook.class);
	}

}