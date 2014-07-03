package fi.internetix.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class CompareReportsPageController extends PanelPageController {

  public CompareReportsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {

    // Data access objects

    QueryDAO queryDAO = new QueryDAO();
    
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

}