package dwf.asynchronous;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import dwf.persistence.export.Importer;

public class AsyncImporterListener implements MessageListener{
	private Log log = LogFactory.getLog(AsyncImporterListener.class);
	
	final static int ENTITY_NAME_SIZE = 100; 

	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private ApplicationContext ctx; 
	
	@Override
	public void onMessage (Message msg) {
		log.info("Message arrived - starting file processing");
		
		byte[] messageBody = (byte[]) rabbitTemplate.getMessageConverter().fromMessage(msg);
		byte[] entityNameArray = Arrays.copyOf(messageBody, ENTITY_NAME_SIZE);
		
		//recupera o nome da entidade
		String entityName = StringUtils.toEncodedString(entityNameArray, Charset.forName("UTF-8")).trim();
		
		
		//Recupera o importador original, guardado dentro do ImporterWrapper (ver {@link dwf.data.autoconfigure.DwfDataAutoConfiguration.AsyncImporterConfiguration})
		Importer importer = ((ImporterWrapper) ctx.getBean(entityName + "Importer")).getWrappedImporter();
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(messageBody);
			int size = messageBody.length - ENTITY_NAME_SIZE;
			messageBody = null;
			is.skip(ENTITY_NAME_SIZE);
			importer.importFromExcel(is, size);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Wrapper que, ao invés de importar, cria e enfileira mensagem a ser processada pelo AsyncImporterListener. 
	 * Substitui o importador original no contexto - ver {@link dwf.data.autoconfigure.DwfDataAutoConfiguration.AsyncImporterConfiguration}
	 * @author cesar_000
	 *
	 */
	public static class ImporterWrapper implements Importer {
		private Log log = LogFactory.getLog(ImporterWrapper.class);
		
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
		public void importFromExcel(InputStream inputStream, int size)
				throws IOException {
			log.debug("Expected body length = " + (ENTITY_NAME_SIZE + (int) size));
			ByteArrayOutputStream os = new ByteArrayOutputStream(ENTITY_NAME_SIZE + (int) size);
			//escreve o nome da entidade nos primeiros 100 bytes
			os.write(StringUtils.rightPad(importer.getEntityName(), ENTITY_NAME_SIZE).getBytes("UTF-8"));
			IOUtils.copy(inputStream, os);
			byte[] messageBody = os.toByteArray();
			
			Assert.isTrue((ENTITY_NAME_SIZE + (int) size) == messageBody.length, "Erro na montagem da mensagem");	
			
			rabbitTemplate.convertAndSend(queueName, messageBody);
			log.info("Mensagem enviada ao RabbitMQ");
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
