package dwf.persistence.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestBaseEntityImpl {
	
	@Test
	public void testRemoveAccents() {
		BaseEntity entity = Mockito.mock(BaseEntity.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(entity.autocompleteText()).thenReturn("áàãâéèêñíìîóòõôúùû");
		Assert.assertTrue(entity.getAutocompleteText().equals("aaaaeeeniiioooouuu"));
	}
}
