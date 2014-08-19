package fi.internetix.edelphi.pages.panel.admin.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querylayout.QuerySectionDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querylayout.QuerySection;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.query.form.FormFieldType;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ReportOptionsPageController extends PanelPageController {

  public ReportOptionsPageController() {
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    
    // Data access objects

    PanelStampDAO panelStampDAO = new PanelStampDAO();
    QueryDAO queryDAO = new QueryDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryOptionFieldOptionDAO optionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    QuerySectionDAO querySectionDAO = new QuerySectionDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);

    // Input attributes
    
    Long queryId = pageRequestContext.getLong("queryId");
    Long stampId = pageRequestContext.getLong("stampId");
    if (stampId == null || stampId <= 0) {
      stampId = panel.getCurrentStamp().getId();
    }
    
    // Input attributes into objects
    
    Query query = queryDAO.findById(queryId);
    PanelStamp stamp = panelStampDAO.findById(stampId);
    
    // Form fields

    List<QueryPage> pages = queryPageDAO.listByQueryAndType(query, QueryPageType.FORM);
    List<FormOptionListFieldDescriptor> beans = new ArrayList<FormOptionListFieldDescriptor>();
    for (QueryPage queryPage : pages) {
      String fieldsSetting = QueryPageUtils.getSetting(queryPage, "form.fields");
      JSONArray fieldsJson = JSONArray.fromObject(fieldsSetting);
    
      JSONObject fieldJson = null;
      for (int i = 0, l = fieldsJson.size(); i < l; i++) {
        fieldJson = fieldsJson.getJSONObject(i);
        FormFieldType fieldType = FormFieldType.valueOf(fieldJson.getString("type"));
  
        String name = fieldJson.getString("name");
        String fieldName = "form." + name;
        
        FormOptionListFieldDescriptor mrBean;
        QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
        if (queryField == null) {
          throw new IllegalArgumentException("Field '" + fieldName + "' not found");
        }
        else {
          switch (fieldType) {
            case MEMO:
            case TEXT:
            break;
            case LIST:
              QueryOptionField queryOptionField = (QueryOptionField) queryField;
              List<QueryOptionFieldOption> options = optionFieldOptionDAO.listByQueryField(queryOptionField);
              String formFieldFilter = pageRequestContext.getString("ff:" + queryOptionField.getId());
              mrBean = new FormOptionListFieldDescriptor(fieldType.toString(), queryOptionField, options, formFieldFilter);
              beans.add(mrBean);
            break;
          }  
        }
      }
    }
    pageRequestContext.getRequest().setAttribute("queryFormFilterFields", beans);
    
    // User groups

    List<PanelUserGroup> panelUserGroups = panelUserGroupDAO.listByPanelAndStamp(panel, stamp);
    Collections.sort(panelUserGroups, new Comparator<PanelUserGroup>() {
      @Override
      public int compare(PanelUserGroup o1, PanelUserGroup o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    pageRequestContext.getRequest().setAttribute("userGroups", panelUserGroups);
    
    // Expertises and interests

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
      if (!expertiseGroupMap.containsKey(intressClass.getId())) {
        expertiseGroupMap.put(intressClass.getId(), new HashMap<Long, Long>());
      }
      for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
        PanelUserExpertiseGroup group = panelUserExpertiseGroupDAO.findByInterestAndExpertiseAndStamp(intressClass, expertiseClass, stamp);
        if (group != null) {
          Long userCount = panelExpertiseGroupUserDAO.getUserCountInGroup(group);
          expertiseGroupMap.get(intressClass.getId()).put(expertiseClass.getId(), group.getId());
          expertiseGroupUserCount.put(group.getId(), userCount);
        }
      }
    }
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterExpertises", expertiseClasses);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterInterests", intressClasses);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterGroupMap", expertiseGroupMap);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterGroupUserCount", expertiseGroupUserCount);

    // Panel stamps

    List<PanelStamp> stamps = panelStampDAO.listByPanel(panel);
    Collections.sort(stamps, new Comparator<PanelStamp>() {
      @Override
      public int compare(PanelStamp o1, PanelStamp o2) {
        return o1.getStampTime() == null ? 1 : o2.getStampTime() == null ? -1 : o1.getStampTime().compareTo(o2.getStampTime());
      }
    });
    pageRequestContext.getRequest().setAttribute("stamps", stamps);
    pageRequestContext.getRequest().setAttribute("stampId", stampId);
    
    // Query pages
    
    List<QueryPage> queryPages = new ArrayList<QueryPage>();
    List<QuerySection> querySections = querySectionDAO.listByQuery(query);
    Collections.sort(querySections, new Comparator<QuerySection>() {
      @Override
      public int compare(QuerySection o1, QuerySection o2) {
        return o1.getSectionNumber() - o2.getSectionNumber();
      }
    });
    for (QuerySection section : querySections) {
      if (section.getVisible()) {
        List<QueryPage> sectionPages = queryPageDAO.listByQuerySection(section);
        Collections.sort(sectionPages, new Comparator<QueryPage>() {
          @Override
          public int compare(QueryPage o1, QueryPage o2) {
            return o1.getPageNumber() - o2.getPageNumber();
          }
        });
        for (QueryPage sectionPage : sectionPages) {
          if (sectionPage.getVisible()) {
            queryPages.add(sectionPage);
          }
        }
      }
    }
    pageRequestContext.getRequest().setAttribute("queryPages", queryPages);
   
    pageRequestContext.setIncludeJSP("/jsp/blocks/panel/admin/report/reportoptions.jsp");
  }

  public class FormOptionListFieldDescriptor {

    private final String fieldType;
    private final QueryOptionField queryOptionField;
    private final List<QueryOptionFieldOption> options;

    public FormOptionListFieldDescriptor(String fieldType, QueryOptionField queryOptionField, List<QueryOptionFieldOption> options, String formFieldFilter) {
      this.fieldType = fieldType;
      this.queryOptionField = queryOptionField;
      this.options = options;
    }

    public String getFieldType() {
      return fieldType;
    }

    public QueryOptionField getQueryOptionField() {
      return queryOptionField;
    }

    public List<QueryOptionFieldOption> getOptions() {
      return options;
    }
  }

}
