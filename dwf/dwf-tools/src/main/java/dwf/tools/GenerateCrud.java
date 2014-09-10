package dwf.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.ClassTool;

public class GenerateCrud {

	public static void main(String[] args) throws Exception {
		//String[] entityClassesName = { "systemagiclabs.smservices.persistence.domain.Airline", "systemagiclabs.smservices.persistence.domain.CarPark", "systemagiclabs.smservices.persistence.domain.AirportTerminal" };
		String[] entityClassesName = { "systemagiclabs.smservices.persistence.domain.FlightPlan"};
		String daopackage = "systemagiclabs.smservices.persistence.dao";
		String controllerpackage = "systemagiclabs.smservices.backoffice.controller";
		String javaSrcPath = "/target/generated/main/java";
		String viewSrcPath = "/target/generated/main/webapp/WEB-INF/jsp/";


		for (String entityClassName : entityClassesName) {

			Class<?> entityClass = Class.forName(entityClassName);
			
			
			Properties p = new Properties();
		    p.setProperty("resource.loader", "class");
		    p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
			VelocityEngine ve = new VelocityEngine(p);

			VelocityContext context = new VelocityContext();
			context.put("daopackage", daopackage );
			context.put("controllerpackage", controllerpackage);
			context.put("entityClass", new ClassTool().inspect(entityClass));
			
			File srcDir = new File(System.getProperty("user.dir") + javaSrcPath);
			File viewSrcDir = new File(System.getProperty("user.dir") + viewSrcPath);
			
			System.out.println("Processando entidade " + entityClassName);
			generateFile(ve, context, new File(srcDir, daopackage.replaceAll("\\.", "/") + "/" + entityClass.getSimpleName() + "DAO.java"), "DAOTemplate.template");
			generateFile(ve, context, new File(srcDir, daopackage.replaceAll("\\.", "/") + "/" + entityClass.getSimpleName() + "DAOImpl.java"), "DAOImplTemplate.template");
			generateFile(ve, context, new File(srcDir, controllerpackage.replaceAll("\\.", "/") + "/" + entityClass.getSimpleName() + "Controller.java"), "ControllerTemplate.template");
			generateFile(ve, context, new File(viewSrcDir, "/" + entityClass.getSimpleName().substring(0, 1).toLowerCase() +  entityClass.getSimpleName().substring(1) + "/edit.jsp"), "editTemplate.template");
			generateFile(ve, context, new File(viewSrcDir, "/" + entityClass.getSimpleName().substring(0, 1).toLowerCase() +  entityClass.getSimpleName().substring(1) + "/list.jsp"), "listTemplate.template");
			generateFile(ve, context, new File(viewSrcDir, "/" + entityClass.getSimpleName().substring(0, 1).toLowerCase() +  entityClass.getSimpleName().substring(1) + "/view.jsp"), "viewTemplate.template");
		}
	}

	private static FileWriter generateFile(VelocityEngine ve, VelocityContext context, File generatedFile, String templateName)
			throws IOException {
		FileWriter fileWriter = null;
		try {
			if(!generatedFile.exists()) {
				System.out.println("Creating " + generatedFile.getAbsolutePath());
			} else {
				System.out.println("Overriding " + generatedFile.getAbsolutePath());
				generatedFile.delete();
			}
			Template template = ve.getTemplate(templateName);
			FileUtils.touch(generatedFile);
			fileWriter = new FileWriter(generatedFile);
			template.merge(context, fileWriter);
		} finally {
			if(fileWriter != null )fileWriter.close();
		}
		return fileWriter;
	}
}
