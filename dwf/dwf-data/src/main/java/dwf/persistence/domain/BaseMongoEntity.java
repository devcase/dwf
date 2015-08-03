package dwf.persistence.domain;


public abstract class BaseMongoEntity extends BaseEntity<String> {
	
	private String searchstring;
	
	public String getSearchstring() {
		searchstring = super.getAutocompleteText();
		return searchstring;
	}

	public void setSearchstring(String searchstring) {
		// this.searchstring = searchstring;
	}
	
}
