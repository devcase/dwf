package dwf.persistence.embeddable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class PriceNotEmptyValidator implements ConstraintValidator<PriceNotEmpty, Price> {

	@Override
	public void initialize(PriceNotEmpty constraintAnnotation) {
	}

	@Override
	public boolean isValid(Price value, ConstraintValidatorContext context) {
		return value.getValue() != null && StringUtils.isNotBlank(value.getCurrencyCode());
	}
}
