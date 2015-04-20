package dwf.sample.persistence.domain;

import javax.persistence.Entity;
import javax.validation.constraints.Size;

import dwf.multilang.domain.BaseMultilangEntity;

@Entity
public class Category extends BaseMultilangEntity<CategoryTranslation> {
	private String description;

	@Size(max=3000)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
