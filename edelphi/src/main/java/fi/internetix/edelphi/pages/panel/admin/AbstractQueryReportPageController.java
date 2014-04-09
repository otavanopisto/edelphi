package fi.internetix.edelphi.pages.panel.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReplyFilterType;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.internetix.edelphi.query.form.FormFieldType;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.ReportChartFormat;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public abstract class AbstractQueryReportPageController extends PanelPageController {
  
  private final String jspFile;

  public AbstractQueryReportPageController(String jspFile) {
    this.jspFile = jspFile;
  }

  protected abstract List<QueryReportPageData> getPageDatas(PageRequestContext pageRequestContext, ReportContext reportContext, Query query);
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();
    
    Long queryId = pageRequestContext.getLong("queryId");
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    PanelStamp activeStamp = RequestUtils.getActiveStamp(pageRequestContext);

    String queryExpertiseFilter = pageRequestContext.getString("queryExpertiseFilter");
    
    QueryDAO queryDAO = new QueryDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    
    List<Query> queries = queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
    Collections.sort(queries, new Comparator<Query>() {
      @Override
      public int compare(Query o1, Query o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    Map<Long, List<QueryPage>> queryPages = new HashMap<Long, List<QueryPage>>();
    Map<Long, Long> queryReplyCounts = new HashMap<Long, Long>();
    for (Query query : queries) {
      List<QueryPage> queryPageList = queryPageDAO.listByQuery(query);
      Collections.sort(queryPageList, new Comparator<QueryPage>() {
        @Override
        public int compare(QueryPage o1, QueryPage o2) {
          return o1.getPageNumber() - o2.getPageNumber();
        }
      });
      queryPages.put(query.getId(), queryPageList);
      queryReplyCounts.put(query.getId(), queryReplyDAO.countByQueryAndStamp(query, activeStamp));     
    }

    List<PanelUserGroup> panelUserGroups = panelUserGroupDAO.listByPanelAndStamp(panel, RequestUtils.getActiveStamp(pageRequestContext));
    
    Query query = queryDAO.findById(queryId);
    
    ReportContext reportContext = new ReportContext(pageRequestContext.getRequest().getLocale().toString(), activeStamp.getId());
    
    // Report options
    
    if (Boolean.valueOf(pageRequestContext.getString(SHOW_2D_AS_1D_PARAM))) {
      reportContext.addParameter(SHOW_2D_AS_1D_PARAM, "true");
    }
    
    this.populateRequestParameters(pageRequestContext, reportContext);
    if (!StringUtils.isEmpty(queryExpertiseFilter)) {
      reportContext.addFilter(QueryReplyFilterType.EXPERTISE.toString(), queryExpertiseFilter);
    }

    Set<Long> checkedUserGroups = null;
    List<UserGroupFilterBean> userGroups = new ArrayList<UserGroupFilterBean>();
  	String[] userGroupsFilter = pageRequestContext.getStrings("userGroups");
  	if (userGroupsFilter != null && userGroupsFilter.length < panelUserGroups.size()) {
  	  reportContext.addFilter(QueryReplyFilterType.USER_GROUPS.toString(), userGroupsFilter != null ? StringUtils.join(userGroupsFilter, ",") : null);
		  checkedUserGroups = new HashSet<Long>(); 
      for (String userGroup : userGroupsFilter) {
  		 checkedUserGroups.add(NumberUtils.createLong(userGroup));
  		}
  	}
		for (PanelUserGroup panelUserGroup : panelUserGroups) {
    	userGroups.add(new UserGroupFilterBean(panelUserGroup.getId(), panelUserGroup.getName(), checkedUserGroups != null ? checkedUserGroups.contains(panelUserGroup.getId()) : true));
    }
    Collections.sort(userGroups, new Comparator<UserGroupFilterBean>() {
      @Override
      public int compare(UserGroupFilterBean o1, UserGroupFilterBean o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });

    handleFormFilter(pageRequestContext, query, reportContext);
    
    List<QueryReportPageData> pageDatas = getPageDatas(pageRequestContext, reportContext, query);
    
    List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(panel);
    Collections.sort(expertiseClasses, new Comparator<PanelUserExpertiseClass>() {
      @Override
      public int compare(PanelUserExpertiseClass o1, PanelUserExpertiseClass o2) {
        return o1.getId().compareTo(o2.getId());
      }
    });
    List<PanelUserIntressClass> intressClasses = panelUserIntressClassDAO.listByPanel(panel);
    Collections.sort(intressClasses, new Comparator<PanelUserIntressClass>() {
      @Override
      public int compare(PanelUserIntressClass o1, PanelUserIntressClass o2) {
        return o1.getId().compareTo(o2.getId());
      }
    });
    Map<Long, Map<Long, Long>> expertiseGroupMap = new HashMap<Long, Map<Long, Long>>();    
    Map<Long, Long> expertiseGroupUserCount = new HashMap<Long, Long>();
    
    for (PanelUserIntressClass intressClass : intressClasses) {
      if (!expertiseGroupMap.containsKey(intressClass.getId()))
        expertiseGroupMap.put(intressClass.getId(), new HashMap<Long, Long>());
      
      for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
        PanelUserExpertiseGroup group = panelUserExpertiseGroupDAO.findByInterestAndExpertiseAndStamp(intressClass, expertiseClass, activeStamp);
        if (group != null) {
          Long userCount = panelExpertiseGroupUserDAO.getUserCountInGroup(group);
          expertiseGroupMap.get(intressClass.getId()).put(expertiseClass.getId(), group.getId());
          expertiseGroupUserCount.put(group.getId(), userCount);
        }
      }
    }

    List<PanelStamp> stamps = panelStampDAO.listByPanel(panel);
    Collections.sort(stamps, new Comparator<PanelStamp>() {
      @Override
      public int compare(PanelStamp o1, PanelStamp o2) {
        return o1.getStampTime() == null ? 1 : o2.getStampTime() == null ? -1 : o1.getStampTime().compareTo(o2.getStampTime());
      }
    });
    PanelStamp latestStamp = panel.getCurrentStamp();

    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    // Serialize the report context so that it can be re-used when exporting reports
    
    String serializedContext = null;
    try {
      ObjectMapper om = new ObjectMapper();
      serializedContext = Base64.encodeBase64URLSafeString(om.writeValueAsBytes(reportContext)); 
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    pageRequestContext.getRequest().setAttribute("queries", queries);
    pageRequestContext.getRequest().setAttribute("queryPages", queryPages);
    pageRequestContext.getRequest().setAttribute("queryReplyCounts", queryReplyCounts);
    pageRequestContext.getRequest().setAttribute("panelId", panel.getId());
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("queryId", queryId);
    pageRequestContext.getRequest().setAttribute("chartFormat", ReportChartFormat.PNG);
    pageRequestContext.getRequest().setAttribute("reportPageDatas", pageDatas);
    pageRequestContext.getRequest().setAttribute("reportContext", reportContext);
    pageRequestContext.getRequest().setAttribute("serializedContext", serializedContext);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterExpertises", expertiseClasses);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterInterests", intressClasses);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterGroupMap", expertiseGroupMap);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterGroupUserCount", expertiseGroupUserCount);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilter", queryExpertiseFilter);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterSelected", getSelectedExpertiseMap(queryExpertiseFilter));
    pageRequestContext.getRequest().setAttribute("stamps", stamps);
    pageRequestContext.getRequest().setAttribute("latestStamp", latestStamp);
    pageRequestContext.getRequest().setAttribute("activeStamp", activeStamp);
    pageRequestContext.getRequest().setAttribute("userGroups", userGroups);

    pageRequestContext.setIncludeJSP(jspFile);
  }
  
  private void populateRequestParameters(RequestContext requestContext, ReportContext reportContext) {
    Enumeration<?> names = requestContext.getRequest().getParameterNames();
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      if (name.startsWith(CHART_PARAMETER_PREFIX)) {
        String value = requestContext.getRequest().getParameter(name);
        name = name.substring(CHART_PARAMETER_PREFIX.length());
        if (!name.startsWith(CHART_FILTER_PARAMETER_PREFIX)) {
          reportContext.addParameter(name, value);
        }
        else {
          name = name.substring(CHART_FILTER_PARAMETER_PREFIX.length());
          reportContext.addFilter(name, value);
        }
      }
    }
  }

  private Map<Long, Boolean> getSelectedExpertiseMap(String queryExpertiseFilter) {
    Map<Long, Boolean> queryExpertiseFilterSelected = new HashMap<Long, Boolean>();
    
    if ((queryExpertiseFilter != null) && (!"".equals(queryExpertiseFilter))) {
      try {
        StringTokenizer tkz = new StringTokenizer(queryExpertiseFilter, ",");
        while (tkz.hasMoreElements())
          queryExpertiseFilterSelected.put(Long.valueOf(tkz.nextToken()), Boolean.TRUE);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    
    return queryExpertiseFilterSelected;
  }
 
  private void handleFormFilter(PageRequestContext pageRequestContext, Query query, ReportContext reportContext) {
    /**
     * Load fields on page
     */
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
//    QueryQuestionTextAnswerDAO queryQuestionTextAnswerDAO = new QueryQuestionTextAnswerDAO();
    QueryPageDAO pageDAO = new QueryPageDAO();
    QueryOptionFieldOptionDAO optionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    
    
    List<QueryPage> pages = pageDAO.listByQueryAndType(query, QueryPageType.FORM);
    
    List<FormFieldFilterDescriptor> beans = new ArrayList<FormFieldFilterDescriptor>();
    for (QueryPage queryPage : pages) {
      String fieldsSetting = QueryPageUtils.getSetting(queryPage, "form.fields");
      JSONArray fieldsJson = JSONArray.fromObject(fieldsSetting);
    
      JSONObject fieldJson = null;
      for (int i = 0, l = fieldsJson.size(); i < l; i++) {
        fieldJson = fieldsJson.getJSONObject(i);
        FormFieldType fieldType = FormFieldType.valueOf(fieldJson.getString("type"));
  
        String name = fieldJson.getString("name");
        String fieldName = "form." + name;
        
        FormFieldFilterDescriptor mrBean;
        QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
        if (queryField == null) {
          throw new IllegalArgumentException("Field '" + fieldName + "' not found");
        } else {
          switch (fieldType) {
            case MEMO:
            case TEXT:
  //            List<QueryQuestionTextAnswer> textAnswers = queryQuestionTextAnswerDAO.listByQueryField(queryField);
  //            
  //            mrBean = new TextFieldAnswerBean(fieldType.toString(), textAnswers);
  //            beans.add(mrBean);
            break;
            case LIST:
              QueryOptionField queryOptionField = (QueryOptionField) queryField;
              List<QueryOptionFieldOption> options = optionFieldOptionDAO.listByQueryField(queryOptionField);
              
              String formFieldFilter = pageRequestContext.getString("ff:" + queryOptionField.getId());
              if (!StringUtils.isEmpty(formFieldFilter))
                reportContext.addFilter(QueryReplyFilterType.FORMFIELD.toString(), queryOptionField.getId() + "=" + formFieldFilter);
              
              mrBean = new FormOptionListFieldDescriptor(fieldType.toString(), queryOptionField, options, formFieldFilter);
              beans.add(mrBean);
            break;
          }  
        }
      }
    }

    pageRequestContext.getRequest().setAttribute("queryFormFilterFields", beans);
  }
  
  public class UserGroupFilterBean {
  	
  	public UserGroupFilterBean(Long id, String name, Boolean checked) {
			this.id = id;
			this.name = name;
			this.checked = checked;
		}
  	
  	public Long getId() {
			return id;
		}
  	
  	public String getName() {
			return name;
		}
  	
  	public Boolean getChecked() {
			return checked;
		}

  	private Long id;
  	private String name;
  	private Boolean checked;
  }
  
  public abstract class FormFieldFilterDescriptor {
    private final String fieldType;

    public FormFieldFilterDescriptor(String fieldType) {
      this.fieldType = fieldType;
    }

    public String getFieldType() {
      return fieldType;
    }
  }
  
  public class FormOptionListFieldDescriptor extends FormFieldFilterDescriptor {

    private final QueryOptionField queryOptionField;
    private final List<QueryOptionFieldOption> options;
    private final Map<Long, Boolean> selectedOptions;

    public FormOptionListFieldDescriptor(String fieldType, QueryOptionField queryOptionField, List<QueryOptionFieldOption> options, String formFieldFilter) {
      super(fieldType);
      this.queryOptionField = queryOptionField;
      this.options = options;
      this.selectedOptions = new HashMap<Long, Boolean>();
      populateSelected(formFieldFilter);
    }

    private void populateSelected(String formFieldFilter) {
      List<String> selectedValues = new ArrayList<String>();

      if (!StringUtils.isEmpty(formFieldFilter)) {
        StringTokenizer tokx = new StringTokenizer(formFieldFilter, ",");
        while (tokx.hasMoreElements()) {
          selectedValues.add(tokx.nextToken());
        }
      }
      
      for (QueryOptionFieldOption option : options) {
        selectedOptions.put(option.getId(), selectedValues.indexOf(option.getValue()) >= 0);
      }
    }
    
    public QueryOptionField getQueryOptionField() {
      return queryOptionField;
    }

    public List<QueryOptionFieldOption> getOptions() {
      return options;
    }

    public Map<Long, Boolean> getSelectedOptions() {
      return selectedOptions;
    }
  }

  private static final String CHART_PARAMETER_PREFIX = "chart_";
  private static final String CHART_FILTER_PARAMETER_PREFIX = "filter:";
  
  private static final String SHOW_2D_AS_1D_PARAM = "show2dAs1d";


}
