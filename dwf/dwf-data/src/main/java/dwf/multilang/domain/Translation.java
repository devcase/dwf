package dwf.multilang.domain;

import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import dwf.serialization.View;

@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class Translation<D extends BaseMultilangEntity<?>> {
	private Long id;
	protected D parentEntity;
	private Map<String, String> text;
	private String language;
	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@JsonView({View.Rest.class})
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@NaturalId
	@ManyToOne
	@NotNull
	@JsonIgnore
	public D getParentEntity() {
		return parentEntity;
	}
	public void setParentEntity(D parentEntity) {
		this.parentEntity = parentEntity;
	}
	
	/**
	 * Map key is the property name
	 * @return
	 */
	@ElementCollection
	@MapKeyColumn(length=30)
	@Column(length=1000)
	@JsonView({View.Rest.class})
	public Map<String, String> getText() {
		return text;
	}
	
	public void setText(Map<String, String> text) {
		this.text = text;
	}
	@NaturalId
	@NotEmpty
	@Column(length=5)
	@JsonView({View.Rest.class})
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	
	
}
