package dwf.persistence.dao;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import dwf.persistence.annotations.UpdatableProperty;
import dwf.persistence.domain.BaseEntity;

/**
 * 
 * @author Hirata
 *
 */
public interface TestClasses {

	
	public static class Entidade extends BaseEntity<Long> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7996919787303553610L;
		public static interface AtualizarValor1{}
		
		private String texto1;
		private String texto2;
		private Integer valor1;
		private Integer valor2;
		private List<String> listaTextos1;
		@Override
		protected String displayText() {
			return texto1;
		}
		@NotEmpty
		public String getTexto1() {
			return texto1;
		}
		public void setTexto1(String texto1) {
			this.texto1 = texto1;
		}
		public String getTexto2() {
			return texto2;
		}
		public void setTexto2(String texto2) {
			this.texto2 = texto2;
		}
		@NotEmpty(groups=AtualizarValor1.class)
		@UpdatableProperty(groups=AtualizarValor1.class)
		public Integer getValor1() {
			return valor1;
		}
		public void setValor1(Integer valor1) {
			this.valor1 = valor1;
		}
		public Integer getValor2() {
			return valor2;
		}
		public void setValor2(Integer valor2) {
			this.valor2 = valor2;
		}
		public List<String> getListaTextos1() {
			return listaTextos1;
		}
		public void setListaTextos1(List<String> listaTextos1) {
			this.listaTextos1 = listaTextos1;
		}
	}
	
	public static class EntidadeDAOImpl extends BaseDAOImpl<Entidade> {
		public EntidadeDAOImpl() {
			super(Entidade.class);
		}
	}
}
