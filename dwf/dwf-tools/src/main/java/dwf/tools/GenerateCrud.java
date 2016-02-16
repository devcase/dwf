package dwf.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.ClassTool;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import dwf.persistence.domain.BaseEntity;

public class GenerateCrud {

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String userDir = System.getProperty("user.dir");
		
		String entityPackage = "farmajato.persistence.domain";
		String daoPackage = "farmajato.persistence.dao";
		String controllerPackage = "farmajato.backoffice.controller";
		File daoSrcDir = new File(userDir + "/target/generated/dao/java");
		File controllerSrcDir  = new File(userDir + "/target/generated/controller/java");
		File viewSrcDir = new File(userDir + "/target/generated/main/webapp/WEB-INF/jsp/");
		boolean overwrite = true;
		File classesDirectory = new File(userDir + "/target/classes");
		
		ClassLoader cl = new URLClassLoader(new URL[] {classesDirectory.toURL()}, GenerateCrud.class.getClassLoader());
		Reflections reflections = new Reflections(ConfigurationBuilder.build(cl, entityPackage));
		Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);
		for (Class<?> entityClass : entities) {
			System.out.println(entityClass);
			if(BaseEntity.class.isAssignableFrom(entityClass)) {
				GenerateCrud.generateCrud(daoPackage, controllerPackage, daoSrcDir, controllerSrcDir, viewSrcDir, overwrite, entityClass);
			}
		}

		
		// String[] entityClassesName = {
		// "systemagiclabs.smservices.persistence.domain.Airline",
		// "systemagiclabs.smservices.persistence.domain.CarPark",
		// "systemagiclabs.smservices.persistence.domain.AirportTerminal" };
		// String[] entityClassesName = {
		// "systemagiclabs.smservices.persistence.domain.FlightPlan"};
		// String[] entityClassesName = {
		// "systemagiclabs.smservices.persistence.domain.flightplan.FlightPlanStep"};
		// String daopackage = "systemagiclabs.smservices.persistence.dao";
		// String controllerpackage =
		// "systemagiclabs.smservices.backoffice.controller";
//		String userDir = System.getProperty("user.dir");
//		String[] entityClassesName = { "farmajato.persistence.domain.ProductCategory" };
//		String daopackage = "farmajato.persistence.dao";
//		String controllerpackage = "farmajato.backoffice.controller";
//		String javaSrcDAOPath = userDir + "/target/generated/main/java";
//		String javaSrcControllerPath = userDir + "/target/generated/main/java";
//		String viewSrcPath = userDir + "/target/generated/main/webapp/WEB-INF/jsp/";
//		File srcDaoDir = new File(javaSrcDAOPath);
//		File srcControllerDir = new File(javaSrcControllerPath);
//		File viewSrcDir = new File(viewSrcPath);
//		boolean override = false;
//
//		for (String entityClassName : entityClassesName) {
//			Class<?> entityClass = Class.forName(entityClassName);
//			generateCrud(daopackage, controllerpackage, srcDaoDir, srcControllerDir, viewSrcDir, override, entityClass);
//		}

	}

	public static void generateCrud(String daopackage, String controllerpackage, File srcDaoDir, File srcControllerDir, File viewSrcDir, boolean overwrite,
			Class<?> entityClass) throws IOException {

		String entityClassName = entityClass.getName();
		Properties p = new Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		VelocityEngine ve = new VelocityEngine(p);

		VelocityContext context = new VelocityContext();
		context.put("daopackage", daopackage);
		context.put("controllerpackage", controllerpackage);
		context.put("entityClass", new ClassTool().inspect(entityClass));

		System.out.println("Processando entidade " + entityClassName);
		generateFile(ve, context, new File(srcDaoDir, daopackage.replaceAll("\\.", "/") + "/" + entityClass.getSimpleName() + "DAO.java"),
				"DAOTemplate.template", overwrite);
		generateFile(ve, context, new File(srcDaoDir, daopackage.replaceAll("\\.", "/") + "/" + entityClass.getSimpleName() + "DAOImpl.java"),
				"DAOImplTemplate.template", overwrite);
		generateFile(ve, context, new File(srcControllerDir, controllerpackage.replaceAll("\\.", "/") + "/" + entityClass.getSimpleName() + "Controller.java"),
				"ControllerTemplate.template", overwrite);
		generateFile(ve, context,
				new File(viewSrcDir, "/" + entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1) + "/edit.jsp"),
				"editTemplate.template", overwrite);
		generateFile(ve, context,
				new File(viewSrcDir, "/" + entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1) + "/list.jsp"),
				"listTemplate.template", overwrite);
		generateFile(ve, context,
				new File(viewSrcDir, "/" + entityClass.getSimpleName().substring(0, 1).toLowerCase() + entityClass.getSimpleName().substring(1) + "/view.jsp"),
				"viewTemplate.template", overwrite);
	}

	private static void generateFile(VelocityEngine ve, VelocityContext context, File generatedFile, String templateName, boolean overwrite)
			throws IOException {
		FileWriter fileWriter = null;
		try {
			if (!generatedFile.exists()) {
				System.out.println("Creating " + generatedFile.getAbsolutePath());
			} else {
				if (overwrite) {
					System.out.println("Overwriting " + generatedFile.getAbsolutePath());
					generatedFile.delete();
				} else {
					System.out.println("Skipping " + generatedFile.getAbsolutePath());
					return;
				}
			}
			Template template = ve.getTemplate(templateName);
			FileUtils.touch(generatedFile);
			fileWriter = new FileWriter(generatedFile);
			template.merge(context, fileWriter);
		} finally {
			if (fileWriter != null)
				fileWriter.close();
		}
		return;
	}
}
