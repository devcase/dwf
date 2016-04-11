package dwf.persistence.dao;

import org.bson.types.ObjectId;

import dwf.persistence.domain.BaseEntity;

public abstract  class LongIdMongoDAOImpl<D extends BaseEntity<Long>> extends BaseMongoDAOImpl<D, Long> implements MongoDAO<D, Long> {

	public LongIdMongoDAOImpl(Class<D> clazz) {
		super(clazz);
	}

	@Override
	protected Long generateId() {
		ObjectId id = ObjectId.get();
		long value = (((long) id.getTimestamp()) << 48) +
				(((long) id.getMachineIdentifier() << 32) & 0xffff00000000L) +
				(((int) id.getProcessIdentifier() << 16) & 0xffff0000L) +
				(((int) id.getCounter()) & 0xffff);
		return value;
	}
	
}
