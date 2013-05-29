package fi.internetix.edelphi.pages.createpanel;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelSettingsTemplateDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.pages.PageController;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ChoosePanelTypePage extends PageController {

  public ChoosePanelTypePage() {
    setAccessAction(DelfoiActionName.CREATE_PANEL, DelfoiActionScope.DELFOI);
  }
  
  @Override
  public void process(PageRequestContext pageRequestContext) {
    PanelSettingsTemplateDAO panelSettingsTemplateDAO = new PanelSettingsTemplateDAO();
    pageRequestContext.getRequest().setAttribute("panelSettingsTemplates", panelSettingsTemplateDAO.listAll());
    
    pageRequestContext.setIncludeJSP("/jsp/blocks/createpanel/choosepaneltype.jsp");
  }
}
