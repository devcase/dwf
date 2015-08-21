package dwf.web.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dwf.activitylog.service.ActivityLogService;
import dwf.multilang.TranslationManager;
import dwf.multilang.domain.BaseMultilangEntity;
import dwf.persistence.dao.DAO;
import dwf.persistence.domain.BaseEntity;
import dwf.persistence.export.Exporter;
import dwf.persistence.export.Importer;
import dwf.utils.ParsedMap;
import dwf.web.message.UserMessageType;

public class BaseCrudController<D extends BaseEntity<ID>, ID extends Serializable> extends BaseController {
	private static Log log = LogFactory.getLog(BaseCrudController.class); 
	
	public static final String OPERATION_EDIT = "edit";
	public static final String OPERATION_VIEW = "view";
	public static final String OPERATION_LIST = "list";
	public static final String OPERATION_CREATE = "create";
	public static final String OPERATION_DELETE = "delete";
	public static final String OPERATION_RESTORE = "restore";
	public static final String OPERATION_LOG = "log";
	public static final String OPERATION_UPLOAD = "upload";
	public static final String OPERATION_DOWNLOAD = "download";

	@Autowired
	private ActivityLogService activityLogService;
	@Autowired(required=false)
	private TranslationManager translationManager;
	
	private Importer<D> importer;
	private Exporter<D> exporter;
	private DAO<D> dao;

	protected final Class<D> clazz;
	protected final String entityName;

	public BaseCrudController(Class<D> clazz) {
		super();
		this.clazz = clazz;
		this.entityName = StringUtils.uncapitalize(clazz.getSimpleName());
	}
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void initDependencies() {
		this.dao = applicationContext.getBean(entityName + "DAO", DAO.class);
		//busca importador
		try{
			this.importer = applicationContext.getBean(entityName + "Importer", Importer.class);
		} catch (NoSuchBeanDefinitionException ignore) {
			log.debug("No importer found for " + entityName);
		}
		try{
			this.exporter = applicationContext.getBean(entityName + "Exporter", Exporter.class);
		} catch (NoSuchBeanDefinitionException ignore) {
			log.debug("No exporter found for " + entityName);
		}
		
		if(this.exporter == null && this.importer != null && this.importer instanceof Exporter) {
			this.exporter = (Exporter<D>) this.importer;
		}
	}

	/**
	 * Sets the entityName attribute to the model. Called before the
	 * RequestMapping because of the ModelAttribute annotation.
	 * 
	 */
	@Override
	public void setupController(RedirectAttributes redirectAttributes, Model model) {
		super.setupController(redirectAttributes, model);
		model.addAttribute("entityName", entityName);
	}

	/**
	 * Create a new instance - shows the edit form
	 * 
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create() {
		model.addAttribute(entityName, null);
		setupNavCrud(OPERATION_CREATE, null);
		return "/" + entityName + "/edit";
	}

	/**
	 * Edit a existing instance
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = { "/{id}", "/view/{id}" }, method = RequestMethod.GET)
	public String view(@PathVariable ID id) {
		D entity = getDAO().findById(id);
		if (entity == null) {
			addUserMessage("crud.view.notfound", UserMessageType.DANGER);
			return "redirect:/" + entityName + "/";
		} else {
			model.addAttribute(entityName, entity);
			model.addAttribute("entity", entity);
			setupNavCrud(OPERATION_VIEW, entity);
			return "/" + entityName + "/view";
		}
	}

	/**
	 * Edit a existing instance
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/edit/{id}")
	public String edit(@PathVariable ID id) {
		D entity = getDAO().findById(id);
		if (entity == null) {
			addUserMessage("crud.edit.notfound", UserMessageType.DANGER);
			return "redirect:/" + entityName + "/";
		} else {
			model.addAttribute(entityName, entity);
			setupNavCrud(OPERATION_EDIT, entity);
			return "/" + entityName + "/edit";
		}
	}

	/**
	 */
	@RequestMapping(value = { "/restore/{id}" }, method = RequestMethod.GET)
	public String restore(@PathVariable ID id) {
		D entity = getDAO().findById(id);
		if (entity == null) {
			addUserMessage("crud.edit.notfound", UserMessageType.DANGER);
			return "redirect:/" + entityName + "/";
		} else {
			model.addAttribute(entityName, entity);
			setupNavCrud(OPERATION_RESTORE, entity);
			return "/generic/restore";
		}
	}
	
	
	@RequestMapping(value = { "/upload" }, method = RequestMethod.GET)
	public String upload() {
		setupNavCrud(OPERATION_UPLOAD, null);
		
		return "/generic/upload";
	}


	@RequestMapping(value = { "/import" }, method = RequestMethod.POST)
	public String importFromExcel(final MultipartFile file) {
		try {
			importer.importFromExcel(file.getInputStream());
		} catch (IOException e) {
			//TODO tratamento de erro!!!
			e.printStackTrace();
		}
		return String.format("redirect:/%s/list", entityName);
	}

	
	/**
	 */
	@RequestMapping(value = { "/restore/{id}" }, method = RequestMethod.POST)
	public String restore(@RequestParam(value = "comments", required = true) String comments, @PathVariable ID id) {
		D entity = getDAO().findById(id);
		if (entity == null) {
			addUserMessage("crud.edit.notfound", UserMessageType.DANGER);
			return "redirect:/" + entityName + "/";
		} else {
			getDAO().restore(entity, comments);
			return "redirect:/" + entityName + "/" + entity.getId();
		}
	}

	/**
	 */
	@RequestMapping(value = { "/delete/{id}" }, method = RequestMethod.GET)
	public String delete(@PathVariable ID id) {
		D entity = getDAO().findById(id);
		if (entity == null) {
			addUserMessage("crud.edit.notfound", UserMessageType.DANGER);
			return "redirect:/" + entityName + "/";
		} else {
			model.addAttribute(entityName, entity);
			setupNavCrud(OPERATION_DELETE, entity);
			return "/generic/delete";
		}
	}

	/**
	 */
	@RequestMapping(value = { "/delete/{id}" }, method = RequestMethod.POST)
	public String delete(@RequestParam(value = "comments", required = true) String comments, @PathVariable ID id) {
		D entity = getDAO().findById(id);
		if (entity == null) {
			addUserMessage("crud.edit.notfound", UserMessageType.DANGER);
			return "redirect:/" + entityName + "/";
		} else {
			getDAO().delete(entity, comments);
			return "redirect:/" + entityName + "/" + entity.getId();
		}
	}

	/**
	 * List existing instances
	 * 
	 * @param filter
	 *            {@lin ParsedMapArgumentResolver}
	 * @return
	 */
	@RequestMapping(value = { "/list", "/" }, method = RequestMethod.GET)
	public Callable<String> list(final ParsedMap filter, @RequestParam(defaultValue = "0") final int pageNumber, @RequestParam(defaultValue = "10") final int fetchSize) {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				int count = getDAO().countByFilter(filter);
				int pages = fetchSize > 0 ? (count / fetchSize) + (count % fetchSize != 0 ? 1 : 0) : 0;
				int p = Math.min(pages - 1, pageNumber);
				List<?> list = getDAO().findByFilter(filter, p * fetchSize, fetchSize);
				model.addAttribute("list", list);
				model.addAttribute("count", count);
				model.addAttribute("pageNumber", p);
				model.addAttribute("pageCount", pages);
				model.addAttribute("fetchSize", fetchSize);
				model.addAttribute("filter", filter);
				setupNavCrud(OPERATION_LIST, null);
				return "/" + entityName + "/list";
			}
			
		};
	}

	@RequestMapping(value = { "/log/{id}" }, method = RequestMethod.GET)
	public String log(@PathVariable ID id) {
		D entity = getDAO().findById(id);
		if (entity == null) {
			addUserMessage("crud.edit.notfound", UserMessageType.DANGER);
			return "redirect:/" + entityName + "/";
		} else {
			model.addAttribute("logList", activityLogService.viewLog(entity));
			model.addAttribute(entityName, entity);
			setupNavCrud(OPERATION_LOG, entity);
			return "/generic/log";
		}
	}
	
	@RequestMapping(value = { "/updateUpload/{id}" }, method = RequestMethod.POST)
	public Callable<String> updateUpload(@PathVariable final ID id, final String propertyName, final MultipartFile file) {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				if(file == null) {
					return "/" + entityName + "/view";
				}
				
				try {
					getDAO().updateUpload(id, file.getInputStream(), file.getContentType(), file.getOriginalFilename(), propertyName);
					return "redirect:/" + entityName + "/" + id;
				} catch (ValidationException ex) {
					addValidationExceptionMessage(ex);
					return "redirect:/" + entityName + "/" + id;
				}
				
			}
		};
	}


	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Callable<String> save(final D form, final BindingResult bindingResult) {
		return new Callable<String>() {
			public String call() throws Exception {
				try {
					if (form.getId() == null) {
						getDAO().saveNew(form);
						addUserMessage("crud.save.new.success", UserMessageType.SUCCESS);
					} else {
						getDAO().updateByAnnotation(form);
						addUserMessage("crud.save.update.success", UserMessageType.SUCCESS);
					}
					return "redirect:/" + entityName + "/" + form.getId();
				} catch (ValidationException ex) {
					addValidationExceptionMessage(ex);
					model.addAttribute(entityName, form);
					if (form.getId() == null)
						setupNavCrud(OPERATION_CREATE, null);
					else
						setupNavCrud(OPERATION_EDIT, form);
					return "/" + entityName + "/edit";
				}
			}
		};
	}

	@RequestMapping(value="/download")
	public String download(final ParsedMap filter) {
		model.addAttribute("filter", filter);
		setupNavCrud(OPERATION_DOWNLOAD, null);
		return "/generic/download";
	}

	
	@RequestMapping(value="/export")
	public void export(final ParsedMap filter, @RequestParam(defaultValue = "0") final int pageNumber, @RequestParam(defaultValue = "100") final int fetchSize, final HttpServletResponse response) {
		if(exporter == null) {
			log.warn("Invalid exporting attempt for entity " + entityName);
		}
		try {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			String contentDisposition = String
					.format("attachment; filename=export_%s.xlsx", entityName);
			response.setHeader("Content-disposition", contentDisposition);
			exporter.exportAsExcel(response.getOutputStream(), filter);
		} catch (IOException e) {
			log.info("Error exporting entities", e);
		}
	}
	
	@RequestMapping(value="/translate/{id}/{property}")
	public String translate(@PathVariable Long id, @RequestParam List<String> language, @PathVariable String property, @RequestParam List<String> text) {
		try {
			BaseMultilangEntity<?> d = (BaseMultilangEntity<?>) clazz.newInstance();
			d.setId(id);
			
			for (int i = 0; i < text.size(); i++) {
				String t = text.get(i);
				String l = language.get(i);
				translationManager.setTranslation(d, property, l, t);
			}
			
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException();
		}
		return "redirect:/" + entityName + "/" + id;
	}
	
	/**
	 * TODO Documentation
	 * @param form
	 * @param groups
	 * @return
	 */
	protected String saveByGroup(final D form, final Class<?>... groups) {
		try {
			if (form.getId() == null) {
				getDAO().saveNew(form);
				addUserMessage("crud.save.new.success", UserMessageType.SUCCESS);
			} else {
				getDAO().updateByAnnotation(form, groups);
				addUserMessage("crud.save.update.success", UserMessageType.SUCCESS);
			}
			return "redirect:/" + entityName + "/" + form.getId();
		} catch (ValidationException ex) {
			addValidationExceptionMessage(ex);
			model.addAttribute(entityName, form);
			if (form.getId() == null)
				setupNavCrud(OPERATION_CREATE, null);
			else
				setupNavCrud(OPERATION_EDIT, form);
			return "/" + entityName + "/view";
		}
	}

	public DAO<D> getDAO() {
		return dao;
	}
	
	public Importer<D> getImporter() {
		return importer;
	}
	
	public Exporter<D> getExporter() {
		return exporter;
	}



	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * 
	 * @param operation
	 */
	protected NavCrud setupNavCrud(String operation, D entity) {
		NavCrud navCrud = new NavCrud();
		navCrud.setActiveOperation(operation);
		navCrud.setItems(new ArrayList<NavCrudItem>());
		navCrud.setEntity(entity);
		model.addAttribute("operation", operation);
		if (entity != null && entity.getId() != null) {
			if(OPERATION_VIEW.equals(operation)) {
				navCrud.getItems().add(new NavCrudItem(null, OPERATION_LIST, NavCrudItem.ICON_LIST, false, true));
			}
			navCrud.getItems().add(new NavCrudItem(entity, OPERATION_VIEW, NavCrudItem.ICON_VIEW, false, !OPERATION_VIEW.equals(operation)));
			navCrud.getItems().add(new NavCrudItem(entity, OPERATION_EDIT, NavCrudItem.ICON_EDIT));
			navCrud.getItems().add(new NavCrudItem(entity, OPERATION_DELETE, NavCrudItem.ICON_DELETE, !entity.isEnabled()));
			navCrud.getItems().add(new NavCrudItem(entity, OPERATION_RESTORE, NavCrudItem.ICON_RESTORE, entity.isEnabled()));
			navCrud.getItems().add(new NavCrudItem(entity, OPERATION_LOG, NavCrudItem.ICON_LOG));
		} else {
			navCrud.getItems().add(new NavCrudItem(null, OPERATION_LIST, NavCrudItem.ICON_LIST, false, OPERATION_CREATE.equals(operation)));
			navCrud.getItems().add(new NavCrudItem(null, OPERATION_CREATE, NavCrudItem.ICON_CREATE));
			if(importer != null) {
				navCrud.getItems().add(new NavCrudItem(null, OPERATION_UPLOAD, NavCrudItem.ICON_UPLOAD));
			}
			if(exporter != null) {
				navCrud.getItems().add(new NavCrudItem(null, OPERATION_DOWNLOAD, NavCrudItem.ICON_DOWNLOAD));
			}

		}
		model.addAttribute("navCrud", navCrud);
		return navCrud;
	}

	/**
	 * 
	 * @author Hirata
	 * 
	 */
	public class NavCrud {
		private String activeOperation;
		private List<NavCrudItem> items;
		private Object entity;

		public String getActiveOperation() {
			return activeOperation;
		}

		public void setActiveOperation(String activeOperation) {
			this.activeOperation = activeOperation;
		}

		public List<NavCrudItem> getItems() {
			return items;
		}

		public void setItems(List<NavCrudItem> items) {
			this.items = items;
		}

		
		/**
		 * @return the entity
		 */
		public Object getEntity() {
			return entity;
		}

		/**
		 * @param entity the entity to set
		 */
		public void setEntity(Object entity) {
			this.entity = entity;
		}

		public NavCrud insertBefore(String operation, NavCrudItem newItem) {
			int idx = -1;
			for (NavCrudItem item : items) {
				idx++;
				if (operation.equals(item.getOperation()))
					break;
			}
			items.add(idx, newItem);
			return this;
		}
		
		public NavCrud changeActiveOperation(String activeOperation) {
			setActiveOperation(activeOperation);
			return this;
		}
	}

	/**
	 * 
	 * @author Hirata
	 * 
	 */
	public class NavCrudItem {
		public static final String ICON_VIEW = "zoom-in";
		public static final String ICON_EDIT = "pencil";
		public static final String ICON_DELETE = "remove";
		public static final String ICON_LOG = "time";
		public static final String ICON_LIST = "list";
		public static final String ICON_CREATE = "plus";
		public static final String ICON_RESTORE = "open";
		public static final String ICON_UPLOAD = "cloud-upload";
		public static final String ICON_DOWNLOAD = "cloud-download";

		private String operation;
		private D entity;
		private String icon;
		private String label;
		private String badge;
		private boolean hidden;
		/**
		 * Se este item deve ser posicionado como a função "Voltar" do menu
		 */
		private boolean backButton;

		public NavCrudItem(D entity, String operation) {
			super();
			this.operation = operation;
			this.entity = entity;
		}

		public NavCrudItem(D entity, String operation, String icon) {
			super();
			this.operation = operation;
			this.icon = icon;
			this.entity = entity;
		}

		public NavCrudItem(D entity, String operation, String icon, boolean hidden) {
			super();
			this.operation = operation;
			this.icon = icon;
			this.entity = entity;
			this.hidden = hidden;
		}

		public NavCrudItem(D entity, String operation, String icon, boolean hidden, boolean backButton) {
			super();
			this.operation = operation;
			this.icon = icon;
			this.entity = entity;
			this.hidden = hidden;
			this.backButton = backButton;
		}

		public NavCrudItem(D entity, String operation, String icon, String label) {
			super();
			this.operation = operation;
			this.icon = icon;
			this.entity = entity;
			this.label = label;
		}

		public String getOperation() {
			return operation;
		}

		public void setOperation(String operation) {
			this.operation = operation;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getBadge() {
			return badge;
		}

		public void setBadge(String badge) {
			this.badge = badge;
		}

		public boolean isHidden() {
			return hidden;
		}

		public void setHidden(boolean hidden) {
			this.hidden = hidden;
		}

		public String getLabelKey() {
			if (this.label == null) {
				return "action." + this.getOperation();
			} else {
				return label;
			}
		}

		public D getEntity() {
			return entity;
		}

		public void setEntity(D entity) {
			this.entity = entity;
		}

		/**
		 * @return the backButton
		 */
		public boolean isBackButton() {
			return backButton;
		}
	}


}
