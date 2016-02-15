package dwf.jasper.hack;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Files;

public class TestFileClasspathURL {
	@Test
	public void test() throws Exception {
		int firstByte = 80;
		
		//preparação
		File tempDir = Files.createTempDir();
		tempDir.deleteOnExit();
		File tempFile = File.createTempFile("entry", ".txt", tempDir);
		tempFile.deleteOnExit();
		String entryName = "/" + tempFile.getName();
		FileUtils.writeByteArrayToFile(tempFile, new byte[] {(byte) firstByte} );
		
		URL url = tempDir.toURI().toURL();
		
		FileClasspathJar fileClasspathJar = new FileClasspathJar(url);
		Assert.assertTrue(fileClasspathJar.entryExists(entryName));
		InputStream is = fileClasspathJar.getInputStream(entryName);
		try {
			Assert.assertEquals(firstByte, is.read());
		} finally {
			is.close();
		}
		
		fileClasspathJar.nextEntry();
		while(fileClasspathJar.getEntryName() != null && !entryName.equals(fileClasspathJar.getEntryName())) {
			fileClasspathJar.nextEntry();
		}
		Assert.assertNotNull("Não encontrou o arquivo " + entryName, fileClasspathJar.getEntryName());
		is = fileClasspathJar.getEntryInputStream();
		try {
			Assert.assertEquals(firstByte, is.read());
		} finally {
			is.close();
		}
		
		fileClasspathJar.close();

	}
}
