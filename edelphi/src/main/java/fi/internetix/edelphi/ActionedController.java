package fi.internetix.edelphi;

import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;

public interface ActionedController {

  public DelfoiActionName getAccessActionName();
  
  public DelfoiActionScope getAccessActionScope();
  
}
