package fi.internetix.edelphi.pages.panel;

import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.pages.PageController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public abstract class PanelPageController extends PageController {

  public abstract void processPageRequest(PageRequestContext pageRequestContext);

  @Override
  public void process(PageRequestContext pageRequestContext) {
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new IllegalStateException("PanelPageController has no panel");
    }
    setJsDataVariable(pageRequestContext, "securityContextId", panel.getId().toString());
    setJsDataVariable(pageRequestContext, "securityContextType", "PANEL");
    
    processPageRequest(pageRequestContext);
  }
}
