package fi.internetix.edelphi.pages.panel;

import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ManagePanelQueriesPageController extends PanelPageController {

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelDAO panelDAO = new PanelDAO();

    Long panelId = pageRequestContext.getLong("panelId");
    Panel panel = panelDAO.findById(panelId);
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    pageRequestContext.setIncludeJSP("/jsp/panels/managepanelqueries.jsp");
  }
}
