package fi.internetix.edelphi.binaries;

import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.controllers.BinaryRequestController;
import fi.internetix.smvc.controllers.RequestContext;

public abstract class BinaryController implements BinaryRequestController {

  @Deprecated
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
  }

}
