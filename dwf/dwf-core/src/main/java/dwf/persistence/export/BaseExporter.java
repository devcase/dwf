package dwf.persistence.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import dwf.persistence.dao.DAO;
import dwf.persistence.domain.BaseEntity;
import dwf.utils.ParsedMap;

public abstract class BaseExporter<D extends BaseEntity<?>> implements Exporter<D>, ApplicationContextAware {
	private ApplicationContext applicationContext;
	protected final Class<D> clazz;
	protected final String entityFullName;
	protected final String entityName;
	
	public BaseExporter(Class<D> clazz)  {
		super();
		this.clazz = clazz;
		this.entityFullName = clazz.getName();
		this.entityName = StringUtils.uncapitalize(clazz.getSimpleName());
		
	}

	@Override
	public void exportAsExcel(OutputStream outputStream, ParsedMap filter) throws IOException {
		DAO<D> dao = (DAO<D>) applicationContext.getBean(entityName + "DAO", DAO.class);
		
		List results = dao.findByFilter(filter);
		ExcelBuilder builder = new ExcelBuilder(entityName, 0);
		buildHeader(builder);
		for (Object asobj : results) {
			D domain = (D) asobj;
			buildLine(builder, domain);
		}
		builder.write(outputStream);
	}
	
	protected abstract void buildHeader(ExcelBuilder builder);
	
	protected abstract void buildLine(ExcelBuilder builder, D domain);

	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	


}
