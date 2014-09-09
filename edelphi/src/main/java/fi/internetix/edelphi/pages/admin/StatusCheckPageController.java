package fi.internetix.edelphi.pages.admin;

import java.io.IOException;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.base.DelfoiDefaultsDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiDefaults;
import fi.internetix.edelphi.pages.PageController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class StatusCheckPageController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    DelfoiDefaultsDAO delfoiDefaultsDAO = new DelfoiDefaultsDAO();
    DelfoiDefaults delfoiDefaults = delfoiDefaultsDAO.findByDelfoi(delfoi);
    if (delfoiDefaults != null) {
      try {
        pageRequestContext.getResponse().getWriter().print("OK");
      }
      catch (IOException ioe) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, "Response I/O error", ioe);
      }
    }
    else {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, "Missing DelfoiDefaults");
    }
  }

}