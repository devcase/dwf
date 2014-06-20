package dwf.activitylog.service;

import java.util.List;

import dwf.activitylog.domain.ActivityLog;
import dwf.activitylog.domain.UpdatedProperty;
import dwf.persistence.domain.BaseEntity;


public interface ActivityLogService {
	void log(BaseEntity<?> entity, String operationId);
	void log(BaseEntity<?> entity, String operationId, String comments);
	void logEntityUpdate(BaseEntity<?> entity, Object newInstance, List<UpdatedProperty> updatedProperties,  Class<?>... groups);
	List<ActivityLog> viewLog(BaseEntity<?> entity);
	
}
