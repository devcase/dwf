package dwf.persistence.dao;

import java.util.List;
import java.util.Map;

import dwf.persistence.domain.BaseEntity;

public interface MongoDAO<D extends BaseEntity<String>> extends DAO<D> {
	public D findFirstByMap(Map<String, Object> mongoMap);
	public List<D> findByMap(Map<String, Object> mongoMap);
	public <T> List<T> aggregateByListOfMap(List<Map<String, Object>> mongoAggregatePipeline, Class<T> resultClass);
}
