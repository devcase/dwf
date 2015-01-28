package dwf.persistence.annotations.constraints;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameConstraintValidator implements ConstraintValidator<Username, CharSequence>  {

	private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
	private Pattern pattern = Pattern.compile(USERNAME_PATTERN);
	
	@Override
	public void initialize(Username constraintAnnotation) {
	}

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();
	}

}
