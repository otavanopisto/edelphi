package fi.internetix.edelphi.pages.panel.admin.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querylayout.QuerySectionDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querylayout.QuerySection;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageProvider;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportUtils;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.edelphi.utils.ReportChartFormat;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.SystemUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.controllers.PageRequestContext;

/**
 * Full query report for export purposes. 
 */
public class FullQueryReportPageController extends PanelPageController {

  public FullQueryReportPageController() {
  }
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Locale locale = pageRequestContext.getRequest().getLocale();

    String internalAuthorization = AuthUtils.getInternalAuthorization(pageRequestContext.getRequest());
    if (StringUtils.isBlank(internalAuthorization)) {
      throw new AccessDeniedException(locale);
    }
    if (!internalAuthorization.equals(SystemUtils.getSettingValue("system.internalAuthorizationHash"))) {
      throw new AccessDeniedException(locale);
    }
    
    Long queryId = pageRequestContext.getLong("queryId");
    ReportChartFormat chartFormat = ReportChartFormat.valueOf(pageRequestContext.getString("chartFormat"));
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);

    QueryDAO queryDAO = new QueryDAO();
    QuerySectionDAO querySectionDAO = new QuerySectionDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    
    Query query = queryDAO.findById(queryId); 
    List<QuerySection> querySections = querySectionDAO.listByQuery(query);
    Collections.sort(querySections, new Comparator<QuerySection>() {
      @Override
      public int compare(QuerySection o1, QuerySection o2) {
        return o1.getSectionNumber() - o2.getSectionNumber();
      }
    });
    
    List<QueryReportPageData> pageDatas = new ArrayList<QueryReportPageData>();
    
    ReportContext reportContext = null;
    String serializedContext = pageRequestContext.getString("serializedContext");
    try {
      ObjectMapper om = new ObjectMapper();
      byte[] serializedData = Base64.decodeBase64(serializedContext);
      String stringifiedData = new String(serializedData, "UTF-8");
      reportContext = om.readValue(stringifiedData, ReportContext.class); 
    }
    catch (Exception e) {
    }
    
    // Generate the report pages
    
    for (QuerySection section : querySections) {
      if (section.getVisible()) {
        List<QueryPage> queryPages = queryPageDAO.listByQuerySection(section);
        Collections.sort(queryPages, new Comparator<QueryPage>() {
          @Override
          public int compare(QueryPage o1, QueryPage o2) {
            return o1.getPageNumber() - o2.getPageNumber();
          }
        });
        // TODO: Will Sections need something in reports??
        for (QueryPage queryPage : queryPages) {
          if (queryPage.getVisible()) {
            QueryPageType queryPageType = queryPage.getPageType();
            QueryReportPageController queryReportPageController = QueryReportPageProvider.getController(queryPageType);
            if (queryReportPageController != null) {
              QueryReportPageData pageData = queryReportPageController.loadPageData(pageRequestContext, reportContext, queryPage);
              pageDatas.add(pageData);

              // Query reply ids are needed for proper filtering of comments
              
              List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, reportContext);
              QueryUtils.appendQueryPageReplys(pageRequestContext, queryPage.getId(), queryReplies);
            }
          }
        }
      }
    }
    
    // TODO Needed here?
    ActionUtils.includeRoleAccessList(pageRequestContext);

    // TODO Are all of these needed?
    pageRequestContext.getRequest().setAttribute("panelId", panel.getId());
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("queryId", queryId);
    pageRequestContext.getRequest().setAttribute("chartFormat", chartFormat);
    pageRequestContext.getRequest().setAttribute("reportPageDatas", pageDatas);
    pageRequestContext.getRequest().setAttribute("reportContext", reportContext);

    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/report/showreport.jsp");
  }
}
