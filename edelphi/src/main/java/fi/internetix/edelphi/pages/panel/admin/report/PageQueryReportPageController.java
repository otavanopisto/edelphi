package fi.internetix.edelphi.pages.panel.admin.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReplyFilter;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReplyFilterType;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportChartContext;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageData;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportPageProvider;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.AuthUtils;
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
    
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    
    Long pageId = pageRequestContext.getLong("pageId");
    ReportChartFormat chartFormat = ReportChartFormat.valueOf(pageRequestContext.getString("chartFormat"));
    
    QueryPage queryPage = queryPageDAO.findById(pageId);

    String queryExpertiseFilter = pageRequestContext.getString("queryExpertiseFilter");
    Panel panel = RequestUtils.getPanel(pageRequestContext);

    List<QueryReportPageData> pageDatas = new ArrayList<QueryReportPageData>();

    QueryReportPageController queryReportPageController = QueryReportPageProvider.getController(queryPage.getPageType());
    QueryReportChartContext chartContext = new QueryReportChartContext(pageRequestContext.getRequest().getLocale(), RequestUtils.getActiveStamp(pageRequestContext));
    chartContext.populateRequestParameters(pageRequestContext);
    QueryReportPageData pageData = queryReportPageController.loadPageData(pageRequestContext, chartContext, queryPage);
    pageDatas.add(pageData);

    if (!StringUtils.isEmpty(queryExpertiseFilter))
      chartContext.addFilter(QueryReplyFilter.createFilter(QueryReplyFilterType.EXPERTISE, queryExpertiseFilter));
    
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
        PanelUserExpertiseGroup group = panelUserExpertiseGroupDAO.findByInterestAndExpertiseAndStamp(intressClass, expertiseClass, panel.getCurrentStamp());
        Long userCount = panelExpertiseGroupUserDAO.getUserCountInGroup(group);
        
        expertiseGroupMap.get(intressClass.getId()).put(expertiseClass.getId(), group.getId());
        expertiseGroupUserCount.put(group.getId(), userCount);
      }
    }
    
    ActionUtils.includeRoleAccessList(pageRequestContext);

    pageRequestContext.getRequest().setAttribute("pageId", pageId);
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("chartFormat", chartFormat);
    pageRequestContext.getRequest().setAttribute("reportPageDatas", pageDatas);
    pageRequestContext.getRequest().setAttribute("reportReplyFilters", chartContext.getReplyFilters());
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterExpertises", expertiseClasses);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterInterests", intressClasses);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterGroupMap", expertiseGroupMap);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterGroupUserCount", expertiseGroupUserCount);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilter", queryExpertiseFilter);
    pageRequestContext.getRequest().setAttribute("queryExpertiseFilterSelected", getSelectedExpertiseMap(queryExpertiseFilter));
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/report/showreport.jsp");
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
  
}
