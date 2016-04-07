package dwf.persistence.domain;

import java.io.Serializable;

public abstract class BaseMongoEntity<ID extends Serializable> extends BaseEntity<ID> {
	
	private String searchstring;
	
	public String getSearchstring() {
		searchstring = super.getAutocompleteText();
		return searchstring;
	}

	public void setSearchstring(String searchstring) {
		// this.searchstring = searchstring;
	}
	
}
