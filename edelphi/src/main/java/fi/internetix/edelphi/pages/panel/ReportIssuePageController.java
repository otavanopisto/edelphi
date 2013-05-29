package fi.internetix.edelphi.pages.panel;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ReportIssuePageController extends PanelPageController {

  public ReportIssuePageController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    pageRequestContext.getRequest().setAttribute("panel", RequestUtils.getPanel(pageRequestContext));
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/reportissue.jsp");
  }
}
