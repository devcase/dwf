package dwf.multilang;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import dwf.persistence.dao.BaseDAOImpl;

@Repository
@Transactional
public class TranslationDAOImpl extends BaseDAOImpl<Translation> implements TranslationDAO {

	public TranslationDAOImpl() {
		super(Translation.class);
	}

}
