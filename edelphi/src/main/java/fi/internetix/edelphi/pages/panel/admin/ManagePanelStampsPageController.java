package fi.internetix.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ManagePanelStampsPageController extends PanelPageController {

  public ManagePanelStampsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }

    PanelStampDAO panelStampDAO = new PanelStampDAO();
    
    List<PanelStamp> panelStamps = panelStampDAO.listByPanel(panel);
    Collections.sort(panelStamps, new Comparator<PanelStamp>() {
      @Override
      public int compare(PanelStamp o1, PanelStamp o2) {
        return o1.getStampTime() == null ? -1 : o2.getStampTime() == null ? 1 : o2.getStampTime().compareTo(o1.getStampTime());
      }
    });
    pageRequestContext.getRequest().setAttribute("panelStamps", panelStamps);
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    setJsDataVariable(pageRequestContext, "panelId", panel.getId().toString());

    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/managepanelstamps.jsp");
  }

}