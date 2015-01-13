package dwf.persistence.validation;

import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import dwf.persistence.annotations.UniqueValue;
import dwf.persistence.domain.BaseEntity;

/**
 * UniqueValue constraint 
 * @author Hirata
 *
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UniqueValueValidator extends UniqueValue.Validator  {
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired
	protected SessionFactory sessionFactory;

	private String fieldName;
	private String messageTemplate;

	@Override
	public void initialize(UniqueValue constraintAnnotation) {
		this.fieldName = constraintAnnotation.field();
		this.messageTemplate = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(BaseEntity<?> entity, ConstraintValidatorContext constraintContext) {
		try {
			Object propertyValue = BeanUtils.getProperty(entity, fieldName);
			if(propertyValue == null) {
				return true;
			}
			Session session = sessionFactory.getCurrentSession();
			//monta busca de elemento no banco com mesmo nome
			Criteria crit = session.createCriteria(entity.getClass()).add(Restrictions.eq(fieldName, propertyValue));
			if(entity.getId() != null) {
				//e diferente id
				crit.add(Restrictions.ne("id", entity.getId()));
			}
			Number count = (Number) crit.setProjection(Projections.rowCount()).uniqueResult();
			
			boolean isValid = count.intValue() == 0; 

			//Creates a violation
			if(!isValid) {
	            constraintContext.disableDefaultConstraintViolation();
	            constraintContext.buildConstraintViolationWithTemplate( messageTemplate  )
	            	.addPropertyNode(fieldName).addConstraintViolation();
	        }
	        return isValid;
			
		}
		catch (final Exception ex)
        {
            // ignore
			log.fatal("Could not check UniqueValue constraint. Ignoring.", ex);
			return true;
        }
	}

}
