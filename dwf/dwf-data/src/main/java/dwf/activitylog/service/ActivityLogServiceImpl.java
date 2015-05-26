package dwf.activitylog.service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dwf.activitylog.domain.ActivityLog;
import dwf.activitylog.domain.UpdatedProperty;
import dwf.persistence.domain.BaseEntity;
import dwf.user.DwfUserUtils;

/**
 * @author Hirata
 *
 */
@Service
public class ActivityLogServiceImpl implements ActivityLogService {
	@Autowired
	protected SessionFactory sessionFactory;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	@Transactional
	public void log(BaseEntity<?> entity, String operationId) {
		getSession().save(createActivityLog(entity, operationId, null, null));
	}

	@Override
	@Transactional
	public void log(BaseEntity<?> entity, String operationId, String comments) {
		getSession().save(createActivityLog(entity, operationId, comments, null));
	}

	@Override
	@Transactional
	public void logEntityUpdate(BaseEntity<?> entity, List<UpdatedProperty> updatedProperties, Class<?>... groups) {
		getSession().save(createActivityLog(entity, OPERATION_UPDATE, null, updatedProperties)); //TODO - colocar as modificações aqui
	}
	

	@Override
	public void logEntityPropertyUpdate(BaseEntity<?> entity, UpdatedProperty updatedProperty) {
		getSession().save(createActivityLog(entity, OPERATION_UPDATE, null, Collections.singletonList(updatedProperty)));
	}

	/**
	 * Factory method
	 * @param entity
	 * @param operationId
	 * @return
	 */
	private ActivityLog createActivityLog(BaseEntity<?> entity, String operationId, String comments, List<UpdatedProperty> updatedProperties) {
		String entityName = StringUtils.uncapitalize(entity.getClass().getSimpleName());
		String entityId = String.valueOf(entity.getId());

		ActivityLog newLog = new ActivityLog();
		newLog.setOperation(operationId);
		newLog.setEntityId(entityId);
		newLog.setEntityName(StringUtils.abbreviate(entityName, 200));
		newLog.setComments(comments);
		newLog.setTimestamp(new Date());
		newLog.setEntityDescription(StringUtils.abbreviate(entity.toString(), 200));
		newLog.setUpdatedProperties(updatedProperties);
		newLog.setUser(DwfUserUtils.getCurrentUserId());
		
		if(entity.getParent() != null) {
			newLog.setParentEntityId(String.valueOf(entity.getParent().getId()));
			newLog.setParentEntityName(StringUtils.abbreviate(StringUtils.uncapitalize(entity.getParent().getClass().getSimpleName()),200));
			newLog.setParentEntityDescription(StringUtils.abbreviate(entity.getParent().toString(), 200));
		}
		
		return newLog;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ActivityLog> viewLog(BaseEntity<?> entity) {
		String entityName = StringUtils.uncapitalize(entity.getClass().getSimpleName());
		String entityId = String.valueOf(entity.getId());
		return getSession().createCriteria(ActivityLog.class).add(
				Restrictions.or(
						Restrictions.and(Restrictions.eq("entityName", entityName), Restrictions.eq("entityId", entityId)),
						Restrictions.and(Restrictions.eq("parentEntityName", entityName), Restrictions.eq("parentEntityId", entityId))
				)).addOrder(Order.desc("timestamp")).list();
	}

}
