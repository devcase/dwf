package dwf.geocode;

import java.util.regex.Pattern;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import dwf.persistence.embeddable.Address;

public class ViaCEPConsultaCEPServiceImpl implements ConsultaCEPService {
	
	private Pattern cepPattern = Pattern.compile("/[0-9]{8}");
	private RestTemplate restTemplate = new RestTemplate();

	@Override
	public Address[] consultaCEP(String cep) {
		if(cep == null) return null;
		cep = cep.replaceAll("-", "").trim();
		if(!cepPattern.matcher(cep).matches())  {
			return null;
		}
		
		Resultado resultado = restTemplate.getForObject("https://viacep.com.br/ws/" + cep + "/json/", Resultado.class);
		if(resultado == null) return null;
		if(Boolean.TRUE.equals(resultado.getErro())) {
			return null;
		}
		
		return new Address[] {resultado.converter()};
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Resultado {
		private String cep;
		private String logradouro;
		private String complemento;
		private String bairro;
		private String localidade;
		private String uf;
		private String ibge;
		private String gia;
		private Boolean erro;
		public String getCep() {
			return cep;
		}
		public void setCep(String cep) {
			this.cep = cep;
		}
		public String getLogradouro() {
			return logradouro;
		}
		public void setLogradouro(String logradouro) {
			this.logradouro = logradouro;
		}
		public String getComplemento() {
			return complemento;
		}
		public void setComplemento(String complemento) {
			this.complemento = complemento;
		}
		public String getBairro() {
			return bairro;
		}
		public void setBairro(String bairro) {
			this.bairro = bairro;
		}
		public String getLocalidade() {
			return localidade;
		}
		public void setLocalidade(String localidade) {
			this.localidade = localidade;
		}
		public String getUf() {
			return uf;
		}
		public void setUf(String uf) {
			this.uf = uf;
		}
		public String getIbge() {
			return ibge;
		}
		public void setIbge(String ibge) {
			this.ibge = ibge;
		}
		public String getGia() {
			return gia;
		}
		public void setGia(String gia) {
			this.gia = gia;
		}
		public Boolean getErro() {
			return erro;
		}
		public void setErro(Boolean erro) {
			this.erro = erro;
		}
		public Address converter() {
			if(Boolean.TRUE.equals(erro)) return null;
			
			Address a = new Address();
			a.setRoute(logradouro);
			a.setSublocality(bairro);
			a.setState(uf);
			a.setCity(localidade);
			a.setPostalCode(cep);
			return a;
		}
		
	}

}
