package fi.internetix.edelphi.query.collage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;

import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.actions.DelfoiActionDAO;
import fi.internetix.edelphi.dao.actions.PanelUserRoleActionDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.dao.users.DelfoiUserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiAction;
import fi.internetix.edelphi.domainmodel.actions.PanelUserRoleAction;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiUser;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserRole;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.DelfoiUserRole;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserRole;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.query.AbstractQueryPageHandler;
import fi.internetix.edelphi.query.QueryExportContext;
import fi.internetix.edelphi.query.QueryOption;
import fi.internetix.edelphi.query.QueryOptionEditor;
import fi.internetix.edelphi.query.QueryOptionType;
import fi.internetix.edelphi.query.QueryPageHandler;
import fi.internetix.edelphi.query.QueryPageHandlerFactory;
import fi.internetix.edelphi.query.RequiredQueryFragment;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class Collage2DQueryPageHandler extends AbstractQueryPageHandler {

  public Collage2DQueryPageHandler() {
    super();
    options.add(new QueryOption(QueryOptionType.QUESTION, "collage2d.includedPages", "panelAdmin.block.query.collage2DIncludedPagesOptionLabel", QueryOptionEditor.HIDDEN, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "collage2d.pageSettings", "panelAdmin.block.query.collage2DPageSettingsOptionLabel", QueryOptionEditor.HIDDEN, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "collage2d.replySource", "panelAdmin.block.query.collage2DReplySourceOptionLabel", QueryOptionEditor.HIDDEN, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "collage2d.labelVisibility", "panelAdmin.block.query.collage2DLabelVisibilityOptionLabel", QueryOptionEditor.HIDDEN, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "collage2d.replyCountsVisible", "panelAdmin.block.query.collage2DReplyCountsVisibleOptionLabel", QueryOptionEditor.HIDDEN, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "collage2d.dotOffset", "panelAdmin.block.query.collage2DDotOffsetOptionLabel", QueryOptionEditor.FLOAT, true));
    options.add(new QueryOption(QueryOptionType.QUESTION, "collage2d.dotMultiplier", "panelAdmin.block.query.collage2DDotMultiplierOptionLabel", QueryOptionEditor.FLOAT, true));
   
    QueryPageHandler queryPageHandler = QueryPageHandlerFactory.getInstance().buildPageHandler(QueryPageType.THESIS_SCALE_2D);
		scale2dOptions = queryPageHandler.getDefinedOptions();
  }

  @Override
  public void renderPage(PageRequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {
  	QueryPageDAO queryPageDAO = new QueryPageDAO();
  	QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
  	QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
  	QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
    
    Locale locale = requestContext.getRequest().getLocale();
    
  	String labelX = null;
  	String labelY = null;
  	List<QueryOptionFieldOption> optionsX = null;
  	List<QueryOptionFieldOption> optionsY = null;
  	List<DataSetBean> dataSets = new ArrayList<DataSetBean>();
  	
  	boolean replyCountsVisible = "TRUE".equals(getStringOptionValue(queryPage, getDefinedOption("collage2d.replyCountsVisible")));
  	String replySourceValue = getStringOptionValue(queryPage, getDefinedOption("collage2d.replySource"));
  	ReplySource replySource = StringUtils.isNotBlank(replySourceValue) ? ReplySource.valueOf(replySourceValue) : null;
  	if (replySource == null) {
  		replySource = ReplySource.ALL;
  	}
  	
  	Double dotMultiplier = getDoubleOptionValue(queryPage, getDefinedOption("collage2d.dotMultiplier"));

  	Query query = queryPage.getQuerySection().getQuery();
  	PanelStamp panelStamp = RequestUtils.getActiveStamp(requestContext);
    List<QueryReply> includeReplies = null;
    Map<Long, Map<String, String>> pageOptions = getPageOptions(queryPage);

    switch (replySource) {
    	case ALL:
    		includeReplies = queryReplyDAO.listByQueryAndStamp(query, panelStamp);
    	break;
    	case MANAGER:
    		Delfoi delfoi = RequestUtils.getDelfoi(requestContext);
    		includeReplies = getManagerReplies(delfoi, query, panelStamp);
    	break;
    	case OWN:
    		includeReplies = queryReply != null ? Arrays.asList(queryReply) : new ArrayList<QueryReply>();
    	break;
    }

  	List<String> includedPages = QueryPageUtils.parseSerializedList(QueryPageUtils.getSetting(queryPage, "collage2d.includedPages"));
  	if (includedPages.size() == 0) {
  		throw new SmvcRuntimeException(EdelfoiStatusCode.COLLAGE2D_CONTAINS_NO_PAGES, Messages.getInstance().getText(locale, "exception.1035.collage2dPageContainsNoPages"));
  	} 
  	
  	for (String includedPage : includedPages) {
  		Long includedPageId = NumberUtils.createLong(includedPage);
  		if (includedPageId != null) {
  		  QueryPage includedQueryPage = queryPageDAO.findById(includedPageId);

	  		if (includedQueryPage.getPageType() == QueryPageType.THESIS_SCALE_2D) {
    		  if (includedQueryPage.getQuerySection().getQuery().getId().equals(query.getId())) {
    		  	
    		    String includedFieldNameX = getScale2dFieldName("x");
    		    String includedFieldNameY = getScale2dFieldName("y");
    		  	
    		    QueryOptionField includedQueryFieldX = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(includedQueryPage, includedFieldNameX);
    		    QueryOptionField includedQueryFieldY = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(includedQueryPage, includedFieldNameY);

    		  	if ((labelX == null) || (labelY == null)) {
    		  		// We use first found labels as x and y axis labels
    		  		
    		  		labelX = getStringOptionValue(includedQueryPage, getScale2dOption("scale2d.label.x"));
    		  		labelY = getStringOptionValue(includedQueryPage, getScale2dOption("scale2d.label.y"));
    		  	}
    		  	
    		  	if ((optionsX == null) || (optionsY == null)) {
    		  		// and first found options as x and y options
      		    optionsX = queryOptionFieldOptionDAO.listByQueryField(includedQueryFieldX);
      		    optionsY = queryOptionFieldOptionDAO.listByQueryField(includedQueryFieldY);
    		  	}
    		  	
    		  	String label = includedQueryPage.getTitle();
    		  	String color = "#aaaaaa";
    		  	Map<String, String> includedPageOptions = pageOptions.get(includedPageId);
    		  	if ((includedPageOptions != null) && includedPageOptions.containsKey("color")) {
    		  		color = includedPageOptions.get("color");
    		  	}
    		  	
    		  	DataSetBean dataSet = new DataSetBean(label, color);
    		  	
    		  	for (QueryReply includeReply : includeReplies) {
      	      QueryQuestionOptionAnswer answerX = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(includeReply, includedQueryFieldX);
      	      QueryQuestionOptionAnswer answerY = queryQuestionOptionAnswerDAO.findByQueryReplyAndQueryField(includeReply, includedQueryFieldY);

      	      if (answerX != null && answerY != null) {
      	      	int x = Integer.parseInt(answerX.getOption().getValue());
      	      	int y = Integer.parseInt(answerY.getOption().getValue());
      	      	
      	      	if (replyCountsVisible) {
      	          dataSet.addData(x, y, dotMultiplier);
      	      	} else {
      	      		dataSet.setData(x, y, dotMultiplier);
      	      	}
      	      }
    		  	}

    		  	dataSets.add(dataSet);
    		  	
    		  } else {
		    		throw new SmvcRuntimeException(EdelfoiStatusCode.COLLAGE2D_PAGE_FROM_FOREIGN_QUERY, Messages.getInstance().getText(locale, "exception.1032.collage2dPageFromForeignQuery"));
    		  }
	  		} else {
	    		throw new SmvcRuntimeException(EdelfoiStatusCode.COLLAGE2D_PAGE_TYPE_INVALID, Messages.getInstance().getText(locale, "exception.1033.collage2dPageTypeInvalid"));
	  		}
  		  
  		} else {
    		throw new SmvcRuntimeException(EdelfoiStatusCode.COLLAGE2D_PAGE_DATA_CORRUPTED, Messages.getInstance().getText(locale, "exception.1034.collage2dPageDataCorrupted"));
  		}
  	}
  	
  	if ((optionsX == null)||(optionsY == null)||(labelX == null)||(labelY == null)) {
  		throw new SmvcRuntimeException(EdelfoiStatusCode.COLLAGE2D_PAGE_DATA_CORRUPTED, Messages.getInstance().getText(locale, "exception.1034.collage2dPageDataCorrupted"));
  	} else {
  		addJsDataVariable(requestContext, "collage2d.dataSets", JSONArray.fromObject(dataSets).toString());
  		
  	  int i = 0;
      for (QueryOptionFieldOption option : optionsX) {
        addJsDataVariable(requestContext, "collage2d.options.x." + i + ".value", option.getValue());
        addJsDataVariable(requestContext, "collage2d.options.x." + i + ".text", option.getText());
        i++;
      }
     
      addJsDataVariable(requestContext, "collage2d.options.x.count", String.valueOf(optionsX.size()));
      addJsDataVariable(requestContext, "collage2d.options.x.label", labelX);
     
      i = 0;
      for (QueryOptionFieldOption option : optionsY) {
        addJsDataVariable(requestContext, "collage2d.options.y." + i + ".value", option.getValue());
        addJsDataVariable(requestContext, "collage2d.options.y." + i + ".text", option.getText());
        i++;
      }
     
      addJsDataVariable(requestContext, "collage2d.options.y.count", String.valueOf(optionsY.size()));
      addJsDataVariable(requestContext, "collage2d.options.y.label", labelY);
      
      addJsDataVariable(requestContext, "collage2d.labelVisibility", getStringOptionValue(queryPage, getDefinedOption("collage2d.labelVisibility")));
      addJsDataVariable(requestContext, "collage2d.dotOffset", getStringOptionValue(queryPage, getDefinedOption("collage2d.dotOffset")));
  
      RequiredQueryFragment requiredFragment = new RequiredQueryFragment("collage2d");
      addRequiredFragment(requestContext, requiredFragment);     
  	}
  }

  @Override
  public void saveAnswers(RequestContext requestContext, QueryPage queryPage, QueryReply queryReply) {

  }

  @Override
  public void updatePageOptions(Map<String, String> settings, QueryPage queryPage, User modifier, boolean hasAnswers) {
    super.updatePageOptions(settings, queryPage, modifier, hasAnswers);
    
    QueryPageUtils.setSetting(queryPage, "collage2d.includedPages", settings.get("collage2d.includedPages"), modifier);
    QueryPageUtils.setSetting(queryPage, "collage2d.pageSettings", settings.get("collage2d.pageSettings"), modifier);
    QueryPageUtils.setSetting(queryPage, "collage2d.replySource", settings.get("collage2d.replySource"), modifier);
    QueryPageUtils.setSetting(queryPage, "collage2d.labelVisibility", settings.get("collage2d.labelVisibility"), modifier);
    QueryPageUtils.setSetting(queryPage, "collage2d.replyCountsVisible", settings.get("collage2d.replyCountsVisible"), modifier);
    
    String dotOffset = settings.get("collage2d.dotOffset");
    if (StringUtils.isNotBlank(dotOffset)) {
    	dotOffset = dotOffset.replace(",", ".");
    }
    
    String dotMultiplier = settings.get("collage2d.dotMultiplier");
    if (StringUtils.isNotBlank(dotMultiplier)) {
    	dotMultiplier = dotMultiplier.replace(",", ".");
    }
    
    QueryPageUtils.setSetting(queryPage, "collage2d.dotOffset", dotOffset, modifier);
    QueryPageUtils.setSetting(queryPage, "collage2d.dotMultiplier", dotMultiplier, modifier);
  } 

  @Override
  public void exportData(QueryExportContext exportContext) {
    
  }
  
  @Override
  public List<QueryOption> getDefinedOptions() {
    List<QueryOption> options = new ArrayList<QueryOption>(super.getDefinedOptions());
    options.addAll(this.options);
    return options;
  }
  
  private List<User> getPanelManagers(Delfoi delfoi, PanelStamp panelStamp) {
  	List<User> result = new ArrayList<User>();
  	DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
  	PanelUserRoleActionDAO panelUserRoleActionDAO = new PanelUserRoleActionDAO();
  	PanelUserDAO panelUserDAO = new PanelUserDAO();
  	DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
  	
  	DelfoiAction manageAction = delfoiActionDAO.findByActionName(DelfoiActionName.MANAGE_PANEL.toString());
  	List<PanelUserRoleAction> panelUserRoleActions = panelUserRoleActionDAO.listByPanelAndDelfoiAction(panelStamp.getPanel(), manageAction);
  	
  	for (PanelUserRoleAction panelUserRoleAction : panelUserRoleActions) {
  		UserRole userRole = panelUserRoleAction.getUserRole();
  		if (userRole instanceof PanelUserRole) {
    		List<PanelUser> panelManagerUsers = panelUserDAO.listByPanelAndRoleAndStamp(panelStamp.getPanel(), (PanelUserRole) userRole, panelStamp);
    		for (PanelUser panelManagerUser : panelManagerUsers) {
    			addManager(result, panelManagerUser.getUser());
    		}
  		} else if (userRole instanceof DelfoiUserRole) {
  			List<DelfoiUser> managerUsers = delfoiUserDAO.listByDelfoiAndRoleAndArchived(delfoi, (DelfoiUserRole) userRole, Boolean.FALSE);
  			for (DelfoiUser managerUser : managerUsers) {
  				addManager(result, managerUser.getUser());
  			}
  		}
  	}

  	return result;
  }
  
  private void addManager(List<User> managers, User manager) {
  	for (User user : managers) {
  		if (user.getId().equals(manager.getId())) {
  			return;
  		}
  	}
  	
  	managers.add(manager);
  }
  
  private List<QueryReply> getManagerReplies(Delfoi delfoi, Query query, PanelStamp panelStamp) {
  	List<QueryReply> result = new ArrayList<QueryReply>();
  	
  	QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
  	List<User> panelManagers = getPanelManagers(delfoi, panelStamp);
  	for (User panelManager : panelManagers) {
  		QueryReply managerReply = queryReplyDAO.findByUserAndQueryAndStamp(panelManager, query, panelStamp);
  		if (managerReply != null) {
  			result.add(managerReply);
  		}
  	}
  	
  	return result;
  }
  
  private String getScale2dFieldName(String axis) {
    return "scale2d." + axis;
  }
  
  private QueryOption getScale2dOption(String name) {
  	for (QueryOption option : scale2dOptions) {
  		if (option.getName().equals(name)) {
  			return option;
  		}
  	}
  	
  	return null;
  }
  
  private Map<Long, Map<String, String>> getPageOptions(QueryPage queryPage) {
  	Map<Long, Map<String, String>> result = new HashMap<Long, Map<String,String>>();
  	
  	NavigableMap<String, String> map = getMapOptionValue(queryPage, getDefinedOption("collage2d.pageSettings"));
  	for (String pageId : map.keySet()) {
  		result.put(NumberUtils.createLong(pageId), QueryPageUtils.parseSerializedMap(map.get(pageId)));
  	}
  	
  	return result;
  }
  
  private List<QueryOption> options = new ArrayList<QueryOption>();
  private List<QueryOption> scale2dOptions = null;
  
  public class DataSetBean {
  	
  	public DataSetBean(String label, String color) {
			this.label = label;
			this.color = color;
		}

		public String getColor() {
			return color;
		}
  	
		public List<double[]> getData() {
			return data;
		}
  	
		public String getLabel() {
			return label;
		}
  
  	public void addData(double x, double y, double amount) {
  		int i = getDataIndex(x, y);
  		if (i == -1) {
  			data.add(new double[] {
  	  	  x, y, amount
  	    });
  		} else {
  			data.get(i)[2] += amount;
  		}
  	}
  	
  	public void setData(double x, double y, double size) {
  		int i = getDataIndex(x, y);
  		if (i == -1) {
  			data.add(new double[] {
  	  	  x, y, size
  	    });
  		} else {
  			data.get(i)[2] = size;
  		}
  	}
  	
  	private int getDataIndex(double x, double y) {
  		for (int i = 0, l = data.size(); i < l; i++) {
  			if ((data.get(i)[0] == x) && (data.get(i)[1] == y)) {
  				return i;
  			}
  		}
  		
  		return -1;
  	}
  	
  	private String label;
  	private String color;
  	private List<double[]> data = new ArrayList<double[]>();
  }
  
  private enum ReplySource {
  	ALL,
  	MANAGER,
  	OWN
  }
}
