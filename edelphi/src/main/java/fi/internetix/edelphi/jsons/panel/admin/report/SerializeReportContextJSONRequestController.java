package fi.internetix.edelphi.jsons.panel.admin.report;

import java.util.Enumeration;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.panels.PanelUserGroupDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.panels.PanelUserGroup;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReplyFilterType;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.internetix.edelphi.query.form.FormFieldType;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class SerializeReportContextJSONRequestController extends JSONController {
  
  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    
    // Data access objects
    
    QueryDAO queryDAO = new QueryDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();
    
    // Query and panel
    
    Long queryId = jsonRequestContext.getLong("queryId");
    Query query = queryDAO.findById(queryId);
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    
    // Filters: stamp
    
    Long stampId = jsonRequestContext.getLong("stampId");
    PanelStamp panelStamp = stampId == null ? panel.getCurrentStamp() : panelStampDAO.findById(stampId);

    // Report context
    
    ReportContext reportContext = new ReportContext(jsonRequestContext.getRequest().getLocale().toString(), panelStamp.getId());
    
    // Filters: expertises and interests

    String queryExpertiseFilter = jsonRequestContext.getString("queryExpertiseFilter");
    if (!StringUtils.isEmpty(queryExpertiseFilter)) {
      reportContext.addFilter(QueryReplyFilterType.EXPERTISE.toString(), queryExpertiseFilter);
    }
    
    // Filters: form fields

    Enumeration<?> names = jsonRequestContext.getRequest().getParameterNames();
    while (names.hasMoreElements()) {
      String name = (String) names.nextElement();
      if (name.startsWith(CHART_PARAMETER_PREFIX)) {
        String value = jsonRequestContext.getRequest().getParameter(name);
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
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryPageDAO pageDAO = new QueryPageDAO();
    List<QueryPage> pages = pageDAO.listByQueryAndType(query, QueryPageType.FORM);
    for (QueryPage queryPage : pages) {
      String fieldsSetting = QueryPageUtils.getSetting(queryPage, "form.fields");
      JSONArray fieldsJson = JSONArray.fromObject(fieldsSetting);
      JSONObject fieldJson = null;
      for (int i = 0, l = fieldsJson.size(); i < l; i++) {
        fieldJson = fieldsJson.getJSONObject(i);
        FormFieldType fieldType = FormFieldType.valueOf(fieldJson.getString("type"));
        String name = fieldJson.getString("name");
        String fieldName = "form." + name;
        QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, fieldName);
        if (queryField == null) {
          throw new IllegalArgumentException("Field '" + fieldName + "' not found");
        } else {
          switch (fieldType) {
            case MEMO:
            case TEXT:
            break;
            case LIST:
              QueryOptionField queryOptionField = (QueryOptionField) queryField;
              String formFieldFilter = jsonRequestContext.getString("ff:" + queryOptionField.getId());
              if (!StringUtils.isEmpty(formFieldFilter))
                reportContext.addFilter(QueryReplyFilterType.FORMFIELD.toString(), queryOptionField.getId() + "=" + formFieldFilter);
            break;
          }  
        }
      }
    }
    
    // Filters: user groups

    List<PanelUserGroup> panelUserGroups = panelUserGroupDAO.listByPanelAndStamp(panel, panelStamp);
    String[] userGroupsFilter = jsonRequestContext.getStrings("userGroups");
    if (userGroupsFilter != null && userGroupsFilter.length < panelUserGroups.size()) {
      reportContext.addFilter(QueryReplyFilterType.USER_GROUPS.toString(), userGroupsFilter != null ? StringUtils.join(userGroupsFilter, ",") : null);
    }
    
    // Settings: 2D as 1D
    
    if (Boolean.valueOf(jsonRequestContext.getString(SHOW_2D_AS_1D_PARAM))) {
      reportContext.addParameter(SHOW_2D_AS_1D_PARAM, "true");
    }
    
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

    jsonRequestContext.addResponseParameter("serializedReportContext", serializedReportContext);
  }
  
  private static final String CHART_PARAMETER_PREFIX = "chart_";
  private static final String CHART_FILTER_PARAMETER_PREFIX = "filter:";

  private static final String SHOW_2D_AS_1D_PARAM = "show2dAs1d";

}
