package fi.internetix.edelphi.pages.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.smvc.controllers.PageRequestContext;

public class SelectQueryReportsPageController extends PanelPageController {

  public SelectQueryReportsPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/selectqueryreports.jsp");
  }

}