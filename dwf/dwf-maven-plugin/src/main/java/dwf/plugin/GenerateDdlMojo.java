package dwf.plugin;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * 
 * @author Hirata
 *
 */
@Mojo(name="generate-crud", requiresDependencyResolution=ResolutionScope.COMPILE_PLUS_RUNTIME, requiresDirectInvocation=true)
public class GenerateDdlMojo {

}
