package dwf.test;

import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class DwfJUnit4ClassRunner extends SpringJUnit4ClassRunner {

	public DwfJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}
}
