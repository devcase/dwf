package dwf.web.mail;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Builds a mail message dispatching to a JSP page. Enables the use of taglibs and i18n.
 * @author Hirata
 *
 */
@Component
public class JspBasedMailBuilder {
	@Autowired
	private JavaMailSender javaMailSender;

	
	public MimeMessage buildMimeMessage(String from, String[] to, String subject, String templatePath, Map<String, ?> model) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		
		ByteArrayOutputStream mailBodyOs = new ByteArrayOutputStream();
		RequestDispatcher rd = request.getRequestDispatcher(templatePath);
		try {
			MyResponse resp = new MyResponse(mailBodyOs);
			
			if(model != null && !model.isEmpty()) {
				for (Map.Entry<String, ?> modelelement : model.entrySet()) {
					request.setAttribute(modelelement.getKey(), modelelement.getValue());
				}
			}
			
			rd.include(request, resp);
			resp.flushBuffer();
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setFrom(from);
			mimeMessageHelper.setSubject(subject);
			
			String messageText= mailBodyOs.toString("UTF-8");
			mimeMessage.setText(messageText, "UTF-8", "html");
			
			return mimeMessage;
			
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void sendMail(String from, String[] to, String subject, String templatePath, Map<String, ?> model) {
		MimeMessage message = buildMimeMessage(from, to, subject, templatePath, model);
		javaMailSender.send(message);
	}
	
	class MyResponse implements HttpServletResponse {
		
		private final OutputStream outputStream;
		
		private MyOutputStream servletOutputStream;
		private PrintWriter out;

		private String characterEncoding;
		private String contentType;
		private long contentLength;
		private int bufferSize = 1;
		private Locale locale;
		private int status;
		
		public MyResponse(OutputStream outputStream) {
			super();
			this.outputStream = outputStream;
		}

		private void initStreams() {
			if(servletOutputStream == null) {
				servletOutputStream = new MyOutputStream();
				servletOutputStream.outputStream = outputStream;
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(servletOutputStream), bufferSize);
				out = new PrintWriter(bw);
			}
		}
		
		@Override
		public String getCharacterEncoding() {
			return characterEncoding;
		}

		@Override
		public String getContentType() {
			return contentType;
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			initStreams();
			return servletOutputStream;
		}

		@Override
		public PrintWriter getWriter() throws IOException {
			initStreams();
			System.out.println("getWriter");
			return out;
		}

		@Override
		public void setCharacterEncoding(String charset) {
			characterEncoding = charset;
		}

		@Override
		public void setContentLength(int len) {
			contentLength = len;
		}

		@Override
		public void setContentLengthLong(long length) {
			contentLength = length;
		}

		@Override
		public void setContentType(String type) {
			contentType = type;
		}

		@Override
		public void setBufferSize(int size) {
			bufferSize = size;
		}

		@Override
		public int getBufferSize() {
			return bufferSize;
		}

		@Override
		public void flushBuffer() throws IOException {
			initStreams();
			out.flush();
		}

		@Override
		public void resetBuffer() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean isCommitted() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setLocale(Locale loc) {
			this.locale = loc;
		}

		@Override
		public Locale getLocale() {
			return this.locale;
		}

		@Override
		public void addCookie(Cookie cookie) {
			// NOOP
		}

		@Override
		public boolean containsHeader(String name) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String encodeURL(String url) {
			// TODO Auto-generated method stub
			return url;
		}

		@Override
		public String encodeRedirectURL(String url) {
			// TODO Auto-generated method stub
			return url;
		}

		@Override
		public String encodeUrl(String url) {
			return encodeURL(url);
		}

		@Override
		public String encodeRedirectUrl(String url) {
			return encodeRedirectURL(url);
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			setStatus(sc, msg);
		}

		@Override
		public void sendError(int sc) throws IOException {
			sendError(sc, null);
		}

		@Override
		public void sendRedirect(String location) throws IOException {
			// TODO Auto-generated method stub
		}

		@Override
		public void setDateHeader(String name, long date) {
			// TODO Auto-generated method stub
		}

		@Override
		public void addDateHeader(String name, long date) {
			// TODO Auto-generated method stub
		}

		@Override
		public void setHeader(String name, String value) {
			// TODO Auto-generated method stub
		}

		@Override
		public void addHeader(String name, String value) {
			// TODO Auto-generated method stub
		}

		@Override
		public void setIntHeader(String name, int value) {
			// TODO Auto-generated method stub
		}

		@Override
		public void addIntHeader(String name, int value) {
			// TODO Auto-generated method stub
		}

		@Override
		public void setStatus(int sc) {
			this.status = sc;
		}

		@Override
		public void setStatus(int sc, String sm) {
			this.status = sc;
		}

		@Override
		public int getStatus() {
			return status;
		}

		@Override
		public String getHeader(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<String> getHeaders(String name) {
			// TODO Auto-generated method stub
			return Collections.emptyList();
		}

		@Override
		public Collection<String> getHeaderNames() {
			// TODO Auto-generated method stub
			return Collections.emptyList();
		}
	}
	
	class MyOutputStream extends ServletOutputStream {
		private OutputStream outputStream;
		

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setWriteListener(WriteListener listener) {
			//NOOP
		}

		@Override
		public void write(int b) throws IOException {
			outputStream.write(b);
		}
		
	}
}
