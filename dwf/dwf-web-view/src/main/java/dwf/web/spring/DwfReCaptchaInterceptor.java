package dwf.web.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import dwf.web.ReCaptcha;

public class DwfReCaptchaInterceptor implements HandlerInterceptor {
	private String captchaPrivateKey;

	public DwfReCaptchaInterceptor(String privateKey) {
		this.captchaPrivateKey = privateKey;
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod targetMethod = (HandlerMethod) handler;
			if (targetMethod.getMethodAnnotation(ReCaptcha.class) != null) {
				ReCaptcha reCaptchaAnnotation = targetMethod.getMethodAnnotation(ReCaptcha.class);
				String reCaptchaResponse = request.getParameter("g-recaptcha-response");
		        @SuppressWarnings("deprecation")
				String privateKey = StringUtils.isNotBlank(reCaptchaAnnotation.privateKey())? reCaptchaAnnotation.privateKey():this.captchaPrivateKey;
		        return reCaptchaValidation(privateKey, reCaptchaResponse);
			}
		}
		
		return true;
	}
	
	private boolean reCaptchaValidation(String privateKey, String reCaptchaResponse) throws Exception {
		StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("https://www.google.com/recaptcha/api/siteverify?secret=");
        strBuilder.append(privateKey);
        strBuilder.append("&response=");
        strBuilder.append(reCaptchaResponse);
        String captchaUrl = strBuilder.toString();
        
		HttpClient client = new DefaultHttpClient();
		HttpGet captchaRequest = new HttpGet(captchaUrl);
 		HttpResponse captchaResponse = client.execute(captchaRequest);
 		
 		String jsonStr = EntityUtils.toString(captchaResponse.getEntity());
 		
 		Gson recaptchaGson = new Gson();
 		JsonObject recaptchaObj = recaptchaGson.fromJson(jsonStr, JsonObject.class);
 		if (recaptchaObj.get("success").getAsBoolean()) {
 			return true;
 		} else {
 			return false;
 		}
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub

	}

}
