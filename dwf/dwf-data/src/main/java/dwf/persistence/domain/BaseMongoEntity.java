package dwf.persistence.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;

import dwf.serialization.View;

public abstract class BaseMongoEntity<ID extends Serializable> extends BaseEntity<ID> {
	
	private String searchstring;
	
	
	@JsonView(View.Mongo.class)
	public String getSearchstring() {
		searchstring = super.getAutocompleteText();
		return searchstring;
	}

	public void setSearchstring(String searchstring) {
		// this.searchstring = searchstring;
	}
	
}
