package dwf.persistence.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import dwf.persistence.domain.BaseEntity;
import dwf.utils.ParsedMap;

public interface MongoDAO<D extends BaseEntity<ID>, ID extends Serializable> extends DAO<D> {
	public D findFirstByMap(Map<String, Object> mongoMap);
	public List<D> findByMap(Map<String, Object> mongoMap);
	public <T> List<T> aggregateByListOfMap(List<Map<String, Object>> mongoAggregatePipeline, Class<T> resultClass);
	public D saveOrReplace(D entity);
	public void deleteByFilter(ParsedMap filter);
}
