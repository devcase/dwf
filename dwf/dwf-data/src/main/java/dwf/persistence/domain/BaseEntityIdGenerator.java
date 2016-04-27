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
		
		//Usando apenas 12 bytes+3 bytes - para evitar números com mais de
		//15 dígitos, que é o máximo do excel....
		long value = (((long) id.getTimestamp()) << 5*4 & 0x3fffffff00000L) +
				(((long) id.getMachineIdentifier() << 4*4) & 0xf0000L) +
				(((int) id.getProcessIdentifier() << 3*4) & 0xf000L) +
				(((int) id.getCounter()) & 0xfffff);
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
		return generateNewId();
	}

}
