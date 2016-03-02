package dwf.sample.persistence.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import dwf.persistence.domain.BaseEntity;
import dwf.user.domain.BaseUser;

@Entity
public class Notebook extends BaseEntity<Long> {
	
	@Override
	protected String displayText() {
		return content;
	}
	private BaseUser baseUser;
	private String content;
	
	@ManyToOne(optional=false)
	public BaseUser getBaseUser() {
		return baseUser;
	}
	public void setBaseUser(BaseUser baseUser) {
		this.baseUser = baseUser;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
