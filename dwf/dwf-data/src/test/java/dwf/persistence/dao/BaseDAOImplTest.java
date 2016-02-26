package dwf.persistence.dao;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import javax.validation.Validator;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ListType;
import org.hibernate.type.StringType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import dwf.activitylog.service.ActivityLogService;
import dwf.persistence.dao.TestClasses.Entidade;


@RunWith(MockitoJUnitRunner.class)
public class BaseDAOImplTest {
	
	@Mock
	protected SessionFactory sessionFactory;
	@Mock
	protected ActivityLogService activityLogService;
	@Mock
	protected Validator beanValidator;
	@InjectMocks
	@Spy
	private TestClasses.EntidadeDAOImpl daoImpl;
	
	
	@Mock
	protected Session session;
	@Mock
	protected ClassMetadata entidadeClassMetadata;
	@Mock
	protected Query query;
	@Mock
	protected ListType listType;
	
	
	@Before
	public void configMock() {
		when(sessionFactory.getCurrentSession()).thenReturn(session);
		when(sessionFactory.getClassMetadata(Entidade.class)).thenReturn(entidadeClassMetadata);
		when(session.createQuery(anyString())).thenReturn(query);
		when(entidadeClassMetadata.getPropertyType("texto1")).thenReturn(StringType.INSTANCE);
		when(entidadeClassMetadata.getPropertyType("texto2")).thenReturn(StringType.INSTANCE);
		when(entidadeClassMetadata.getPropertyType("valor1")).thenReturn(IntegerType.INSTANCE);
		when(entidadeClassMetadata.getPropertyType("valor2")).thenReturn(IntegerType.INSTANCE);
		when(entidadeClassMetadata.getPropertyType("listaTextos1")).thenReturn(listType);
		
		
	}
	
	@Test
	public void testUpdateByAnnotation() {
		Entidade dExistente = new Entidade();
		dExistente.setId(123L);
		dExistente.setTexto1("valor antigo");
		dExistente.setTexto2("valor imutável");
		dExistente.setValor1(1);
		dExistente.setValor2(null);
		dExistente.setListaTextos1(new ArrayList<String>());
		dExistente.getListaTextos1().add("item1");
		dExistente.getListaTextos1().add("item2");
		dExistente = spy(dExistente);
		
		Entidade dNova = new Entidade();
		dNova.setId(123L);
		dNova.setTexto1("    valor novo    ");
		dNova.setTexto2("    valor imutável ");
		dNova.setValor2(4);
		dNova.setListaTextos1(new ArrayList<String>());
		dNova.getListaTextos1().add("item1");
		dNova.getListaTextos1().add("item3");
		
		when(daoImpl.findById(123L)).thenReturn(dExistente);
		
		daoImpl.updateByAnnotation(dNova);
		
		//chamou o método que grava log de modificação de entidade?
		verify(activityLogService).logEntityUpdate(eq(dNova), any());
		//fez trim e encontrou diferença em texto1?
		verify(dExistente, times(1)).setTexto1("valor novo");
		//detectou que não há diferenças entre texto2?
		verify(dExistente, never()).setTexto2(any());
		//levou em consideração a anotação UpdatableProperties? 
		verify(dExistente, never()).setValor1(any());
		//conseguiu atualizar números?
		verify(dExistente, times(1)).setValor2(4);
		//adicionou item3?
		Assert.assertTrue(dExistente.getListaTextos1().contains("item3"));
		//manteve item1?
		Assert.assertTrue(dExistente.getListaTextos1().contains("item1"));
		//removeu item2?
		Assert.assertTrue(!dExistente.getListaTextos1().contains("item2"));
	}
	
	@Test
	public void testUpdateByAnnotation2() {
		Entidade dExistente = new Entidade();
		dExistente.setId(123L);
		dExistente.setTexto1("valor antigo");
		dExistente.setTexto2("valor imutável");
		dExistente.setValor1(1);
		dExistente.setValor2(null);
		dExistente.setListaTextos1(new ArrayList<String>());
		dExistente.getListaTextos1().add("item1");
		dExistente.getListaTextos1().add("item2");
		dExistente = spy(dExistente);
		
		Entidade dNova = new Entidade();
		dNova.setId(123L);
		dNova.setTexto1("    valor novo    ");
		dNova.setTexto2("    valor imutável ");
		dNova.setValor1(0);
		dNova.setValor2(4);
		dNova.setListaTextos1(new ArrayList<String>());
		dNova.getListaTextos1().add("item1");
		dNova.getListaTextos1().add("item3");
		
		when(daoImpl.findById(123L)).thenReturn(dExistente);
		
		daoImpl.updateByAnnotation(dNova, Entidade.AtualizarValor1.class);
		
		//chamou o método que grava log de modificação de entidade?
		verify(activityLogService).logEntityUpdate(eq(dNova), anyObject(), any());
		//ignorou campos sem a anotação AtualizarValor1?
		verify(dExistente, never()).setTexto1(any());
		verify(dExistente, never()).setTexto2(any());
		verify(dExistente, never()).setValor2(any());
		//levou em consideração a anotação UpdatableProperties? 
		verify(dExistente, times(1)).setValor1(0);
		//não adicionou item3?
		Assert.assertTrue(!dExistente.getListaTextos1().contains("item3"));
		//manteve item1?
		Assert.assertTrue(dExistente.getListaTextos1().contains("item1"));
		//manteve item2?
		Assert.assertTrue(dExistente.getListaTextos1().contains("item2"));
	}
}
