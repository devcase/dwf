package dwf.activitylog.service;

import java.util.List;

import dwf.activitylog.domain.ActivityLog;
import dwf.activitylog.domain.UpdatedProperty;
import dwf.persistence.domain.BaseEntity;


public interface ActivityLogService {
	static String OPERATION_UPDATE = "update";
	static String OPERATION_SUBMIT = "submit";
	static String OPERATION_CREATE = "create";
	static String OPERATION_RESTORE = "restore";
	static String OPERATION_DELETE = "delete";
	static String OPERATION_CASCADE_DELETE = "cascadedelete";
	static String OPERATION_CANCEL = "cancel";
	static String OPERATION_CONFIRM = "confirm";
	static String OPERATION_EVALUATE = "evaluate";
	static String OPERATION_IMPORT = "import";
	static String OPERATION_ERROR = "error";
	
	void log(BaseEntity<?> entity, String operationId);
	void log(BaseEntity<?> entity, String operationId, String comments);
	void logEntityUpdate(BaseEntity<?> entity, List<UpdatedProperty> updatedProperties,  Class<?>... groups);
	void logEntityPropertyUpdate(BaseEntity<?> entity, UpdatedProperty updatedProperty);
	List<ActivityLog> viewLog(BaseEntity<?> entity);
	
}
