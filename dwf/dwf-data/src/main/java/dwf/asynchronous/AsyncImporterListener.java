package dwf.asynchronous;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import dwf.persistence.export.Importer;

public class AsyncImporterListener implements MessageListener{
	
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private ApplicationContext ctx; 
	
	@Override
	public void onMessage (Message msg) {
				
		Map<String, String> mapa = (Map<String, String>) rabbitTemplate.getMessageConverter().fromMessage(msg);
		//Recupera o importador original, guardado dentro do ImporterWrapper (ver {@link dwf.data.autoconfigure.DwfDataAutoConfiguration.AsyncImporterConfiguration})
		Importer importer = ((ImporterWrapper) ctx.getBean(mapa.get("entityName") + "Importer")).getWrappedImporter();
		File tempFile=null;
		try {
			tempFile = File.createTempFile("temp", ".xlsx");
			FileOutputStream fos = new FileOutputStream(tempFile);
			fos.write(Base64.decodeBase64(mapa.get("fileString").getBytes()));
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
			
				
		try {
			importer.importFromExcel(new FileInputStream(tempFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tempFile.delete();
	}
	
	/**
	 * Wrapper que, ao invés de importar, cria e enfileira mensagem a ser processada pelo AsyncImporterListener. 
	 * Substitui o importador original no contexto - ver {@link dwf.data.autoconfigure.DwfDataAutoConfiguration.AsyncImporterConfiguration}
	 * @author cesar_000
	 *
	 */
	public static class ImporterWrapper implements Importer {
		private final RabbitTemplate rabbitTemplate;
		private final Importer importer;
		private final String queueName;
		
		public ImporterWrapper(RabbitTemplate rabbitTemplate,
				Importer importer, String queueName) {
			super();
			this.rabbitTemplate = rabbitTemplate;
			this.importer = importer;
			this.queueName = queueName;
		}

		@Override
		public void importFromExcel(InputStream inputStream)
				throws IOException {
			byte[] fileByteArray = IOUtils.toByteArray(inputStream);
			String fileByteArrayEncoded = new String(Base64.encodeBase64(fileByteArray));
			Map<String, String> mapa = new HashMap<String,String>();
			mapa.put("entityName", importer.getEntityName());
			mapa.put("fileString", fileByteArrayEncoded);
								
			rabbitTemplate.convertAndSend(queueName, mapa);
		}

		@Override
		public String getEntityName() {
			return importer.getEntityName();
		}
		
		public Importer getWrappedImporter() {
			return importer;
		}
	}

}
