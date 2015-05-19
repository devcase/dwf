package dwf.persistence.annotations.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordConstraintValidator implements ConstraintValidator<Password, CharSequence>  {
	Password p;
	
	@Override
	public void initialize(Password constraintAnnotation) {
		p = constraintAnnotation;
	}

	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		int lower=0; int upper=0; int digits=0;
		for(int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if(c >= 'a' && c<='z') lower++;
			else if(c >= 'A' && c<='Z') upper++;
			else if(c >= '0' && c<='9') digits++;
		}
		if(p.minLowerCase() > lower || p.minUpperCase() > upper || p.minDigits() > digits) return false;
		return true;
	}

}
