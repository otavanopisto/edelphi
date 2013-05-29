package fi.internetix.edelphi.pages;

import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class LogoutPageController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    RequestUtils.logoutUser(pageRequestContext);
    
    pageRequestContext.setRedirectURL(pageRequestContext.getRequest().getContextPath() + "/index.page");
  }
}
