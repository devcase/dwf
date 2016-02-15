package dwf.jasper.hack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.scan.Jar;

public class FileClasspathJar implements Jar {
	private URL url;
	private File file;
	private Iterator<File> entryIterator;
	private File currentEntry;

	public FileClasspathJar(URL url) {
		super();
		this.url= url;
		this.file = new File(url.getFile());
		try {
			reset();
		} catch (Exception ignore) {}
	}

	@Override
	public URL getJarFileURL() {
		return url;
	}

	@Override
	public boolean entryExists(String name) throws IOException {
		return new File(file, name).exists();
	}

	@Override
	public InputStream getInputStream(String name) throws IOException {
		return new FileInputStream(new File(file, name));
	}

	@Override
	public long getLastModified(String name) throws IOException {
		return new File(file, name).lastModified();
	}

	@Override
	public void close() {
		entryIterator = null;

	}

	@Override
	public void nextEntry() {
		if(entryIterator.hasNext()) {
			currentEntry = entryIterator.next();
		} else {
			currentEntry = null;
		}
	}

	@Override
	public String getEntryName() {
		if(currentEntry != null)
			return currentEntry.getAbsolutePath().substring(file.getAbsolutePath().length()).replace('\\', '/');
		else
			return null;
	}

	@Override
	public InputStream getEntryInputStream() throws IOException {
		if(currentEntry != null)
			return getInputStream(getEntryName());
		else
			return null;
	}

	@Override
	public String getURL(String entry) {
		try {
			return new File(file, entry).toURI().toURL().toString();
		} catch (MalformedURLException e) {
			return new File(file, entry).getAbsolutePath();
		}
	}

	@Override
	public void reset() throws IOException {
		entryIterator = FileUtils.iterateFiles(this.file, null, true);

	}

}
