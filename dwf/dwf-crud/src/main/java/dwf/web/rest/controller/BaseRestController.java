package dwf.web.rest.controller;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonView;

import dwf.persistence.dao.DAO;
import dwf.persistence.domain.BaseEntity;
import dwf.serialization.View;
import dwf.utils.ParsedMap;

public class BaseRestController<T extends BaseEntity<?>> {

	private final DAO<T> dao;
	
	public BaseRestController(DAO<T> dao) {
		this.dao = dao;
	}
	
	public DAO<T> getDao() {
		return dao;
	}
	
	@RequestMapping(method = {RequestMethod.POST, RequestMethod.GET})
	@JsonView(View.RestList.class)
	public Callable<ResponseEntity<List<T>>> list(ParsedMap filter,
			@RequestParam(defaultValue = "0") final int pageNumber,
			@RequestParam(defaultValue = "10") final int fetchSize) {
		return new Callable<ResponseEntity<List<T>>>() {
			@Override
			public ResponseEntity<List<T>> call() throws Exception {
				int count = getDao().countByFilter(filter);
				int pages = fetchSize > 0 ? (count / fetchSize) + (count % fetchSize != 0 ? 1 : 0) : 0;
				int p = Math.min(pages - 1, pageNumber);
				return new ResponseEntity<List<T>>(getDao().findByFilter(
						filter, p * fetchSize, fetchSize), HttpStatus.OK);
			}
		};
	}
	
	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	@JsonView(View.RestDetails.class)
	public Callable<ResponseEntity<T>> view(@PathVariable Long id) {
		return new Callable<ResponseEntity<T>>() {
			@Override
			public ResponseEntity<T> call() throws Exception {
				return new ResponseEntity<T>(getDao().findById(id),
						HttpStatus.OK);
			}
		};
	}
}
