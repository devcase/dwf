package dwf.persistence.annotations.constraints;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.time.DateUtils;

public class FromNowValidator implements ConstraintValidator<FromNow, Object> {
	
	FromNow annotation;
	@Override
	public void initialize(FromNow constraintAnnotation) {
		annotation = constraintAnnotation;
	}

	@Override
	public boolean isValid(Object v, ConstraintValidatorContext context) {
		Date value;
		if(v instanceof Date) {
			value = (Date) v;
		} else if(v instanceof Calendar) {
			value = ((Calendar) v).getTime();
		} else {
			return true; //??
		}
		if(value == null) return true;
		boolean result = true;
		if(result && annotation.minYearsPast() != FromNow.NOT_SET) {
			result = result && DateUtils.addYears(new Date(), annotation.minYearsPast() * -1).after(value);
		} 
		if(result && annotation.minDaysFuture() != FromNow.NOT_SET) {
			result = result && DateUtils.addDays(new Date(), annotation.minDaysFuture()).before(value); 
		}
		return result;
	}

}
