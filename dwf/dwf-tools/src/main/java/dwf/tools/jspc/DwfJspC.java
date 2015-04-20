package dwf.tools.jspc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.jasper.JspCompilationContext;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class DwfJspC extends org.apache.jasper.JspC {
	List<String[]> generatedJsps = new ArrayList<String[]>();
	public String targetPackage;

	public static void main(String[] args)  {
		try {
//			File dir = new File(args[0]);
//			FileUtils.deleteDirectory(dir);
			DwfJspC jspc = new DwfJspC();
			jspc.setUriroot(args[2]);
			jspc.setOutputDir(args[0]);
			jspc.targetPackage = args[1];
			jspc.setSmapSuppressed(true);
			jspc.execute();
			jspc.generate();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void generateWebMapping(String file, JspCompilationContext clctxt) throws IOException {
        String className = clctxt.getServletClassName();
        String packageName = clctxt.getServletPackageName();
        String thisServletName;
        if  ("".equals(packageName)) {
            thisServletName = className;
        } else {
            thisServletName = packageName + '.' + className;
        }
        generatedJsps.add(new String[] {thisServletName , file.replace('\\', '/'), file.replace('\\', '_').replace('.', '_').replace('-', '_')});
	}
	
	public void generate() throws IOException {
		Properties p = new Properties();
	    p.setProperty("resource.loader", "class");
	    p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		VelocityEngine ve = new VelocityEngine(p);

		VelocityContext context = new VelocityContext();
		context.put("generatedJsps", generatedJsps );
		context.put("targetPackage", targetPackage );
		
		File targDir = new File(scratchDir, targetPackage.replace('.', '/'));
		targDir.mkdir();
		File outputFile = new File(targDir, "GeneratedJspConfiguration.java");
		generateFile(ve, context, outputFile, "GeneratedJspConfigurationTemplate.template", false);

	}
	
	private static FileWriter generateFile(VelocityEngine ve, VelocityContext context, File generatedFile, String templateName, boolean replaces)
			throws IOException {
		FileWriter fileWriter = null;
		try {
			if(!generatedFile.exists()) {
				System.out.println("Creating " + generatedFile.getAbsolutePath());
			} else {
				if(replaces) {
					System.out.println("Overriding " + generatedFile.getAbsolutePath());
					generatedFile.delete();
				} else {
					System.out.println("Skipping " + generatedFile.getAbsolutePath());
					return null;
				}
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
