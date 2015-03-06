package dwf.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;


@Scope(value="session")
public class AjaxHashKeyManager {


	public AjaxHashKeyManager() {
		map = new HashMap<Integer, AjaxHashKeyManager.EntityFilter>();
	}
	
	private Map<Integer, EntityFilter> map;
	
	public int generateHashKey(String entityName, String filter) {
		int hash = (entityName+filter).hashCode();
		map.put(hash, new EntityFilter(entityName, filter));
		return hash;
	}
	
	public EntityFilter getEntityFilter (int hash) {
		return map.getOrDefault(hash, null);
	}
	
	public class EntityFilter {
		private String entityName;
		private String filter;
		
		public EntityFilter(String entityName, String filter) {
			super();
			this.entityName = entityName;
			this.filter = filter;
		}
		public String getEntityName() {
			return entityName;
		}
		public void setEntityName(String entityName) {
			this.entityName = entityName;
		}
		public String getFilter() {
			return filter;
		}
		public void setFilter(String filter) {
			this.filter = filter;
		}
		
		
		
	}
	
}
