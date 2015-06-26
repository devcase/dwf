package dwf.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.google.gson.Gson;

import dwf.persistence.dao.DAO;
import dwf.persistence.domain.BaseEntity;
import dwf.utils.ParsedMap;
import dwf.utils.SimpleParsedMap;
import dwf.web.AjaxHashKeyManager;

@RestController
@RequestMapping("/ajax")
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class AjaxController extends BaseController {
	@Autowired
	private AjaxHashKeyManager ajaxHashKeyManager;

	@RequestMapping(value="/tokenInput/{hashkey}", produces="text/plain;charset=UTF-8")
	public Callable<String> tokenInput (@PathVariable final int hashkey, final String q) {
		return new Callable<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public String call() throws Exception {
				String json = "";
				AjaxHashKeyManager.EntityFilter entityFilter = ajaxHashKeyManager.getEntityFilter(hashkey);
				if (entityFilter == null) return "";				
				DAO dao = (DAO) getApplicationContext().getBean(entityFilter.getEntityName() + "DAO");
				ParsedMap filter;
				if (StringUtils.isEmpty(entityFilter.getFilter())) {
					filter = new SimpleParsedMap();
				} else {
					filter = new SimpleParsedMap(entityFilter.getFilter().split(";|="));
				}
				filter.put("searchstring", BaseEntity.autocompleteForm(q));
				List<BaseEntity<Serializable>> list = dao.findByFilter(filter);
				List<HashMap<String, String>> returnList = new ArrayList<HashMap<String,String>>();
				for (BaseEntity obj : list) {
					HashMap<String, String> newobj = new HashMap<String, String>();
					newobj.put("id", String.valueOf(obj.getId()));
					newobj.put("name", obj.getDisplayText());
					returnList.add(newobj);
				}
				Gson gson = new Gson();
				json = gson.toJson(returnList);
				return json;
			}
		};
	}
	
}
