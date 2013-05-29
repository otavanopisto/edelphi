package fi.internetix.edelphi.pages.panel;

import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class SetActiveStampPageController extends PanelPageController {

  public SetActiveStampPageController() {
    super();
  }
  
  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    RequestUtils.setActiveStamp(pageRequestContext, pageRequestContext.getLong("stampId"));
    String url = pageRequestContext.getRequest().getHeader("Referer");
    pageRequestContext.setRedirectURL(url);
  }
  
}
