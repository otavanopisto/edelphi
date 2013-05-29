package fi.internetix.edelphi.pages;

import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class LoginPageController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    Boolean logout = pageRequestContext.getBoolean("logoff");
    if (logout) {
      RequestUtils.logoutUser(pageRequestContext);
    }
    AuthUtils.includeAuthSources(pageRequestContext);
    pageRequestContext.setIncludeJSP("/jsp/pages/login.jsp");
  }

}
