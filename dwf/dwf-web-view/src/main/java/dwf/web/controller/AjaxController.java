package dwf.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import dwf.persistence.dao.DAO;
import dwf.persistence.domain.BaseEntity;
import dwf.utils.ParsedMap;
import dwf.utils.SimpleParsedMap;

@RestController
@RequestMapping("/ajax")
public class AjaxController extends BaseController {
	
	@RequestMapping("/tokenInput/{targetEntityName}")
	public Callable<String> tokenInput (@PathVariable final String targetEntityName, final String q) {
		return new Callable<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public String call() throws Exception {
				String json = "";
				DAO dao = (DAO) getApplicationContext().getBean(targetEntityName + "DAO");
				ParsedMap filter = new SimpleParsedMap();
				filter.put("searchstring", q);
				List<BaseEntity<Serializable>> list = dao.findByFilter(filter);
				List<HashMap<String, String>> returnList = new ArrayList<HashMap<String,String>>();
				for (BaseEntity obj : list) {
					HashMap<String, String> newobj = new HashMap<String, String>();
					newobj.put("id", String.valueOf(obj.getId()));
					newobj.put("name", obj.getAutocompleteText());
					returnList.add(newobj);
				}
				Gson gson = new Gson();
				json = gson.toJson(returnList);
				return json;
			}
		};
	}
	
}
