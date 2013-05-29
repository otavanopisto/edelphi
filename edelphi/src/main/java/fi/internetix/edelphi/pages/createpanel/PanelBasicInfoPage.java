package fi.internetix.edelphi.pages.createpanel;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.pages.PageController;
import fi.internetix.smvc.controllers.PageRequestContext;

public class PanelBasicInfoPage extends PageController {

  public PanelBasicInfoPage() {
    setAccessAction(DelfoiActionName.CREATE_PANEL, DelfoiActionScope.DELFOI);
  }
  
  @Override
  public void process(PageRequestContext pageRequestContext) {
    pageRequestContext.setIncludeJSP("/jsp/blocks/createpanel/panelbasicinfo.jsp");
  }
}
