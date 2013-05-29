package fi.internetix.edelphi.pages.panel;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelBulletinDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelBulletin;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ViewBulletinPageController extends PanelPageController {

  public ViewBulletinPageController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) { 
    // TODO: If query is hidden only users with manage material rights should be able to enter
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    PanelBulletinDAO panelBulletinDAO = new PanelBulletinDAO();

    Long bulletinId = pageRequestContext.getLong("bulletinId");
    PanelBulletin bulletin = panelBulletinDAO.findById(bulletinId);
    if (bulletin == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("bulletin", bulletin);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/viewbulletin.jsp");
  }
}
