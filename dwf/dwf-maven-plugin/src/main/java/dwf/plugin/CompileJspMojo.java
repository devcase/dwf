package dwf.plugin;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import dwf.tools.jspc.DwfJspC;

/**
 * Transforms jsp into java servlets, creates a class that maps each servlet for its
 * original path and adds the sources to the maven project
 * @author Hirata
 *
 */
@Mojo(name="compile-jsp")
public class CompileJspMojo extends AbstractMojo {
	@Parameter(name="uriRoot", required=true)
	private String uriRoot;
	@Parameter(name="outputDir", required=true)
	private String outputDir;
	@Parameter(name="targetPackage", required=true)
	private String targetPackage;
	@Parameter(defaultValue="${project}")
    private MavenProject project;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			DwfJspC jspc = new DwfJspC();
			jspc.setUriroot(this.uriRoot);
			jspc.setOutputDir(this.outputDir);
			jspc.targetPackage = this.targetPackage;
			
            this.getLog().info( "Compiling jsps." );      
			jspc.execute();
            this.getLog().info( "Generating GeneratedJspConfiguration.java");      
			jspc.generate();
			
			this.project.addCompileSourceRoot( outputDir );
            this.getLog().info( "Source directory: " + outputDir + " added." );      
		} catch (IOException e) {
			throw new MojoExecutionException("Error!", e);
		}
	}

}
