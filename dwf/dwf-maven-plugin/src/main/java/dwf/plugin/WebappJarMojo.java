package dwf.plugin;

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.jar.JarArchiver;

/**
 * Adiciona o conteúdo de webapp na pasta /public do jar - sem incluir o conteúdo de WEB-INF.
 * @author hirata
 *
 */
@Mojo( name = "webapp-jar", defaultPhase = LifecyclePhase.PACKAGE, threadSafe = true, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class WebappJarMojo extends AbstractMojo {

    /**
     * The JAR archiver needed for archiving the classes directory into a JAR file under WEB-INF/lib.
     */
    @Component( role = Archiver.class, hint = "jar" )
    private JarArchiver jarArchiver;
    /**
     * The directory for the generated WAR.
     */
    @Parameter( defaultValue = "${project.build.directory}", required = true )
    private String outputDirectory;

    /**
     * The name of the generated WAR.
     */
    @Parameter( defaultValue = "${project.build.finalName}", property = "war.warName", required = true )
    private String warName;

    /**
     * Classifier to add to the generated WAR. If given, the artifact will be an attachment instead. The classifier will
     * not be applied to the JAR file of the project - only to the WAR file.
     */
    @Parameter( defaultValue ="webapp")
    private String classifier="webapp";

    /**
     * Single directory for extra files to include in the WAR. This is where you place your JSP files.
     */
    @Parameter( defaultValue = "${basedir}/src/main/webapp", required = true )
    private String webappDirectory;

    /**
     * Single directory for extra files to include in the WAR. This is where you place your JSP files.
     */
    @Parameter( defaultValue = "webapp", required = true )
    private String webappDirectoryName = "webapp";
    
    /**
     */
    @Component
    private MavenProjectHelper projectHelper;
    
    /**
     * @since 2.1-alpha-2
     */
    @Parameter( defaultValue = "${session}", readonly = true, required = true )
    private MavenSession session;

    /**
     * The Maven project.
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    /**
     * The archive configuration to use. See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven
     * Archiver Reference</a>.
     */
    @Parameter
    private MavenArchiveConfiguration archiveConfiguration = new MavenArchiveConfiguration();
    
    
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		Resource r = new Resource();
		r.setDirectory(webappDirectory);
		r.setTargetPath("public/");
		r.addExclude("**/*.jsp");
		r.addExclude("/WEB-INF/**");
		project.addResource(r);
	}
    
    

}
