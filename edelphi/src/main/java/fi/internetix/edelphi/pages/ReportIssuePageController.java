package fi.internetix.edelphi.pages;

import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ReportIssuePageController extends PageController {

  public ReportIssuePageController() {
    super();
    // TODO: User should be at least guest to access this page
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    pageRequestContext.getRequest().setAttribute("panel", RequestUtils.getPanel(pageRequestContext));
    pageRequestContext.setIncludeJSP("/jsp/pages/reportissue.jsp");
  }
}
