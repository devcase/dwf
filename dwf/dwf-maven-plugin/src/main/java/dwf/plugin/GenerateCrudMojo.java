package dwf.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.shared.utils.io.DirectoryScanner;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import dwf.persistence.domain.BaseEntity;
import dwf.tools.GenerateCrud;

@Mojo(name="generate-crud", requiresDependencyResolution=ResolutionScope.COMPILE_PLUS_RUNTIME, requiresDirectInvocation=true)
public class GenerateCrudMojo extends AbstractMojo {
	
	@Parameter(property = "dwf.generate.crud.entitypackage", required=true)
	private String entityPackage;
	@Parameter(property = "dwf.generate.crud.daopackage", required=true)
	private String daoPackage;
	@Parameter(property = "dwf.generate.crud.controllerpackage", required=true)
	private String controllerPackage;
	@Parameter(property = "dwf.generate.crud.daosrcdir", required=true)
	private File daoSrcDir;
	@Parameter(property = "dwf.generate.crud.controllersrcdir", required=true)
	private File controllerSrcDir;
	@Parameter(property = "dwf.generate.crud.viewsrcdir", required=true)
	private File viewSrcDir;
	@Parameter(property = "dwf.generate.crud.override", defaultValue="false")
	private boolean override = false;
	@Parameter(defaultValue="${project.build.outputDirectory}")
	private File classesDirectory;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		DirectoryScanner dirScanner = new DirectoryScanner();
		try {
			ClassLoader cl = new URLClassLoader(new URL[] {classesDirectory.toURL()}, getClass().getClassLoader());
			Reflections reflections = new Reflections(ConfigurationBuilder.build(cl, entityPackage));
			Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);
			for (Class<?> entityClass : entities) {
				if(BaseEntity.class.isAssignableFrom(entityClass)) {
					GenerateCrud.generateCrud(daoPackage, controllerPackage, daoSrcDir, controllerSrcDir, viewSrcDir, override, entityClass);
				}
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

}
