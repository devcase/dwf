package dwf.persistence.dao;

import org.bson.types.ObjectId;

import dwf.persistence.domain.BaseEntity;

public abstract  class StringIdMongoDAOImpl<D extends BaseEntity<String>> extends BaseMongoDAOImpl<D, String> implements MongoDAO<D, String> {

	public StringIdMongoDAOImpl(Class<D> clazz) {
		super(clazz);
	}
	

	public StringIdMongoDAOImpl(Class<D> clazz, Class<?> jsonview) {
		super(clazz, jsonview);
	}


	@Override
	protected String generateId() {
		ObjectId id = ObjectId.get();
		return id.toHexString();
	}
	
}
