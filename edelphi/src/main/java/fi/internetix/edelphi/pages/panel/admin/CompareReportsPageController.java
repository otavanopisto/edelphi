package fi.internetix.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.pages.panel.admin.report.util.ReportContext;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class CompareReportsPageController extends PanelPageController {

  public CompareReportsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {

    // Data access objects

    QueryDAO queryDAO = new QueryDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    
    // Page attribute: panel (for displaying panel name)

    Panel panel = RequestUtils.getPanel(pageRequestContext);
    pageRequestContext.getRequest().setAttribute("panel", panel);
    
    // Page attribute: queries (for listing all queries in dropdown menus)
    
    List<Query> queries = queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
    Collections.sort(queries, new Comparator<Query>() {
      @Override
      public int compare(Query o1, Query o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    pageRequestContext.getRequest().setAttribute("queries", queries);
    
    // Main JSP
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/comparereports.jsp");
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

  private static final String CHART_PARAMETER_PREFIX = "chart_";
  private static final String CHART_FILTER_PARAMETER_PREFIX = "filter:";
  private static final String SHOW_2D_AS_1D_PARAM = "show2dAs1d";

}