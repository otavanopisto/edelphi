package fi.internetix.edelphi.pages.panel.admin.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querylayout.QuerySectionDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.panels.PanelUserGroup;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querylayout.QuerySection;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReplyFilterType;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPage;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageProvider;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ViewQueryReportPageController extends PanelPageController {

  public ViewQueryReportPageController() {
    setAccessAction(DelfoiActionName.MANAGE_PANEL_REPORTS, DelfoiActionScope.PANEL);
  }
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    
    // Data access objects
    
    QueryDAO queryDAO = new QueryDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();
    
    // Query and panel
    
    Long queryId = pageRequestContext.getLong("queryId");
    Query query = queryDAO.findById(queryId);
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    
    // Filters: stamp
    
    Long stampId = pageRequestContext.getLong("stampId");
    PanelStamp panelStamp = stampId == null ? panel.getCurrentStamp() : panelStampDAO.findById(stampId);

    // Report context
    
    ReportContext reportContext = new ReportContext(pageRequestContext.getRequest().getLocale().toString(), panelStamp.getId());
    
    // Filters: expertises and interests

    String queryExpertiseFilter = pageRequestContext.getString("queryExpertiseFilter");
    if (!StringUtils.isEmpty(queryExpertiseFilter)) {
      reportContext.addFilter(QueryReplyFilterType.EXPERTISE.toString(), queryExpertiseFilter);
    }
    
    // Filters: form fields

//    Enumeration<?> names = requestContext.getRequest().getParameterNames();
//    while (names.hasMoreElements()) {
//      String name = (String) names.nextElement();
//      if (name.startsWith(CHART_PARAMETER_PREFIX)) {
//        String value = requestContext.getRequest().getParameter(name);
//        name = name.substring(CHART_PARAMETER_PREFIX.length());
//        if (!name.startsWith(CHART_FILTER_PARAMETER_PREFIX)) {
//          reportContext.addParameter(name, value);
//        }
//        else {
//          name = name.substring(CHART_FILTER_PARAMETER_PREFIX.length());
//          reportContext.addFilter(name, value);
//        }
//      }
//    }
//    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
//    QueryPageDAO pageDAO = new QueryPageDAO();
//    QueryOptionFieldOptionDAO optionFieldOptionDAO = new QueryOptionFieldOptionDAO();
//    List<QueryPage> pages = pageDAO.listByQueryAndType(query, QueryPageType.FORM);
//    List<FormFieldFilterDescriptor> beans = new ArrayList<FormFieldFilterDescriptor>();
//    for (QueryPage queryPage : pages) {
//      String fieldsSetting = QueryPageUtils.getSetting(queryPage, "form.fields");
//      JSONArray fieldsJson = JSONArray.fromObject(fieldsSetting);
//      JSONObject fieldJson = null;
//      for (int i = 0, l = fieldsJson.size(); i < l; i++) {
//        fieldJson = fieldsJson.getJSONObject(i);
//        FormFieldType fieldType = FormFieldType.valueOf(fieldJson.getString("type"));
//        String name = fieldJson.getString("name");
//        String fieldName = "form." + name;
//        FormFieldFilterDescriptor mrBean;
//        QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
//        if (queryField == null) {
//          throw new IllegalArgumentException("Field '" + fieldName + "' not found");
//        } else {
//          switch (fieldType) {
//            case MEMO:
//            case TEXT:
//            break;
//            case LIST:
//              QueryOptionField queryOptionField = (QueryOptionField) queryField;
//              List<QueryOptionFieldOption> options = optionFieldOptionDAO.listByQueryField(queryOptionField);
//              String formFieldFilter = pageRequestContext.getString("ff:" + queryOptionField.getId());
//              if (!StringUtils.isEmpty(formFieldFilter))
//                reportContext.addFilter(QueryReplyFilterType.FORMFIELD.toString(), queryOptionField.getId() + "=" + formFieldFilter);
//              mrBean = new FormOptionListFieldDescriptor(fieldType.toString(), queryOptionField, options, formFieldFilter);
//              beans.add(mrBean);
//            break;
//          }  
//        }
//      }
//    }
//    pageRequestContext.getRequest().setAttribute("queryFormFilterFields", beans);
    
    // Filters: user groups

    List<PanelUserGroup> panelUserGroups = panelUserGroupDAO.listByPanelAndStamp(panel, panelStamp);
    String[] userGroupsFilter = pageRequestContext.getStrings("userGroups");
    if (userGroupsFilter != null && userGroupsFilter.length < panelUserGroups.size()) {
      reportContext.addFilter(QueryReplyFilterType.USER_GROUPS.toString(), userGroupsFilter != null ? StringUtils.join(userGroupsFilter, ",") : null);
    }
    
    // Settings: 2D as 1D
    
    if (Boolean.valueOf(pageRequestContext.getString(SHOW_2D_AS_1D_PARAM))) {
      reportContext.addParameter(SHOW_2D_AS_1D_PARAM, "true");
    }
    

    List<QueryReportPage> queryReportPages = getReportPages(pageRequestContext, reportContext, query);
    
    // Serialize the report context so that it can be re-used when exporting reports
    
    String serializedReportContext = null;
    try {
      ObjectMapper om = new ObjectMapper();
      serializedReportContext = Base64.encodeBase64URLSafeString(om.writeValueAsBytes(reportContext)); 
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    // Render the report 

    pageRequestContext.getRequest().setAttribute("reportContext", reportContext);
    pageRequestContext.getRequest().setAttribute("serializedReportContext", serializedReportContext);
    pageRequestContext.getRequest().setAttribute("queryReportPages", queryReportPages);
    pageRequestContext.setIncludeJSP("/jsp/blocks/panel/admin/report/queryreport.jsp");
  }

  private List<QueryReportPage> getReportPages(PageRequestContext pageRequestContext, ReportContext reportContext, Query query) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QuerySectionDAO querySectionDAO = new QuerySectionDAO();
    List<QueryReportPage> reportPages = new ArrayList<QueryReportPage>();
    
    List<QuerySection> querySections = querySectionDAO.listByQuery(query);
    Collections.sort(querySections, new Comparator<QuerySection>() {
      @Override
      public int compare(QuerySection o1, QuerySection o2) {
        return o1.getSectionNumber() - o2.getSectionNumber();
      }
    });
    for (QuerySection section : querySections) {
      if (section.getVisible()) {
        List<QueryPage> queryPages = queryPageDAO.listByQuerySection(section);
        Collections.sort(queryPages, new Comparator<QueryPage>() {
          @Override
          public int compare(QueryPage o1, QueryPage o2) {
            return o1.getPageNumber() - o2.getPageNumber();
          }
        });
        for (QueryPage queryPage : queryPages) {
          if (queryPage.getVisible()) {
            QueryPageType queryPageType = queryPage.getPageType();
            QueryReportPageController queryReportPageController = QueryReportPageProvider.getController(queryPageType);
            if (queryReportPageController != null) {
              // Convert old, deprecated QueryReportPageData instances to new, shiny QueryReportPage instances
              QueryReportPage reportPage = queryReportPageController.generateReportPage(pageRequestContext, reportContext, queryPage);
              reportPages.add(reportPage);
//              List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, reportContext);
//              QueryUtils.appendQueryPageReplys(pageRequestContext, queryPage.getId(), queryReplies);
            }
            
          }
        }
      }
    }
    return reportPages;
  }

  private static final String SHOW_2D_AS_1D_PARAM = "show2dAs1d";

}
