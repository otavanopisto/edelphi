package fi.internetix.edelphi.pages;

import fi.internetix.smvc.controllers.PageRequestContext;

public class RegisterInfoController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    pageRequestContext.setIncludeJSP("/jsp/pages/registerinfo.jsp");
  }

}
