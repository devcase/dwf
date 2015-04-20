package dwf.plugin;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import dwf.tools.jspc.DwfJspC;

@Mojo(name="compile-jsp")
public class CompileJspMojo extends AbstractMojo {
	@Parameter(name="uriRoot", required=true)
	private String uriRoot;
	@Parameter(name="outputDir", required=true)
	private String outputDir;
	@Parameter(name="targetPackage", required=true)
	private String targetPackage;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			DwfJspC jspc = new DwfJspC();
			jspc.setUriroot(this.uriRoot);
			jspc.setOutputDir(this.outputDir);
			jspc.targetPackage = this.targetPackage;
			jspc.execute();
			jspc.generate();
		} catch (IOException e) {
			throw new MojoExecutionException("Error!", e);
		}
	}

}
