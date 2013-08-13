package fi.internetix.edelphi.pages.panel.admin.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReplyFilter;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportChartContext;
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

public class PageQueryReportPageController extends PanelPageController {
  
  public PageQueryReportPageController() {
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    Locale locale = pageRequestContext.getRequest().getLocale();
    
    String internalAuthorization = AuthUtils.getInternalAuthorization(pageRequestContext.getRequest());
    if (StringUtils.isBlank(internalAuthorization)) {
      throw new AccessDeniedException(locale);
    }
    
    if (!internalAuthorization.equals(SystemUtils.getSettingValue("system.internalAuthorizationHash"))) {
      throw new AccessDeniedException(locale);
    }
    
    Long pageId = pageRequestContext.getLong("pageId");
    ReportChartFormat chartFormat = ReportChartFormat.valueOf(pageRequestContext.getString("chartFormat"));
    
    QueryPage queryPage = queryPageDAO.findById(pageId);
    Panel panel = RequestUtils.getPanel(pageRequestContext);

    List<QueryReportPageData> pageDatas = new ArrayList<QueryReportPageData>();

    QueryReportPageController queryReportPageController = QueryReportPageProvider.getController(queryPage.getPageType());
    QueryReportChartContext chartContext = new QueryReportChartContext(pageRequestContext.getRequest().getLocale(), RequestUtils.getActiveStamp(pageRequestContext));

    // Apply report filters from the session, if any
    
    List<QueryReplyFilter> filters = ReportUtils.getQueryFilters(pageRequestContext, queryPage.getQuerySection().getQuery().getId());
    if (filters != null) {
      for (QueryReplyFilter filter : filters) {
        chartContext.addFilter(filter);
      }
    }

    QueryReportPageData pageData = queryReportPageController.loadPageData(pageRequestContext, chartContext, queryPage);
    pageDatas.add(pageData);
    
    // Query reply ids are needed for proper filtering of comments
    
    List<QueryReply> queryReplies = ReportUtils.getQueryReplies(queryPage, chartContext);
    QueryUtils.appendQueryPageReplys(pageRequestContext, queryPage.getId(), queryReplies);
    
    ActionUtils.includeRoleAccessList(pageRequestContext);

    pageRequestContext.getRequest().setAttribute("pageId", pageId);
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("chartFormat", chartFormat);
    pageRequestContext.getRequest().setAttribute("reportPageDatas", pageDatas);
    pageRequestContext.getRequest().setAttribute("reportReplyFilters", chartContext.getReplyFilters()); // for rendering chart images
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/report/showreport.jsp");
  }
  
}
