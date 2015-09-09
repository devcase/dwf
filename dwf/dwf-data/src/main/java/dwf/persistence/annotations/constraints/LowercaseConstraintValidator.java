package dwf.persistence.annotations.constraints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LowercaseConstraintValidator implements ConstraintValidator<Lowercase, CharSequence>  {

	private Pattern pattern = Pattern.compile(Lowercase.LOWERCASE_REGEXP);
	
	@Override
	public void initialize(Lowercase constraintAnnotation) {
	}

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		if(value == null) return true;
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

}
