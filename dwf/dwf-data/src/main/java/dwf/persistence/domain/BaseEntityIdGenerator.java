package dwf.persistence.domain;

import java.io.Serializable;
import java.util.Properties;

import org.bson.types.ObjectId;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

public class BaseEntityIdGenerator implements IdentifierGenerator, Configurable {
	private Type type;
	
	public static Long convertFromObjectId(ObjectId id) {
		
		long value = (((long) id.getTimestamp()) << 9*4 & 0xfff000000000L) +
				(((long) id.getMachineIdentifier() << 7*4) & 0xff0000000L) +
				(((int) id.getProcessIdentifier() << 4*4) & 0xfff0000L) +
				(((int) id.getCounter()) & 0xffff);
		return value;

	}
	
	public Serializable generateNewId() {
		ObjectId id = ObjectId.get();
		if(type.equals(LongType.INSTANCE)) {
			return convertFromObjectId(id);
		} else if(type.equals(StringType.INSTANCE)) {
			return id.toHexString();
		} else {
			//inválido!
			return id.toString();
		}
	}
	

	@Override
	public void configure(Type type, Properties params, Dialect d) throws MappingException {
		this.type = type;
	}

	@Override
	public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
		if(object instanceof BaseEntity) {
			Serializable id = ((BaseEntity<? extends Serializable>) object).getId();
			if(id == null) {
				return generateNewId();
			} else {
				return id;
			}
		} else {
			throw new HibernateException("Not a BaseEntity subclass");
		}
	}

}
