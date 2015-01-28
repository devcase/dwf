package dwf.persistence.domain;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.enhanced.TableGenerator;

public class BaseEntityIdGenerator extends TableGenerator {

	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		if(object instanceof BaseEntity) {
			Serializable id = ((BaseEntity<? extends Serializable>) object).getId();
			if(id == null) {
				return super.generate(session, object);
			} else {
				return id;
			}
		} else {
			throw new HibernateException("Not a BaseEntity subclass");
		}
	}

}
