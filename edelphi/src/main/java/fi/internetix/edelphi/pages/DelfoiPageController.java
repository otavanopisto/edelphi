package fi.internetix.edelphi.pages;

import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public abstract class DelfoiPageController extends PageController {

  public abstract void processPageRequest(PageRequestContext pageRequestContext);

  @Override
  public void process(PageRequestContext pageRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    if (delfoi == null) {
      throw new IllegalStateException("DelfoiPageController has no delfoi");
    }
    setJsDataVariable(pageRequestContext, "securityContextId", delfoi.getId().toString());
    setJsDataVariable(pageRequestContext, "securityContextType", "DELFOI");
    
    processPageRequest(pageRequestContext);
  }
}
