package fi.internetix.edelphi.pages.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.base.DelfoiBulletinDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiBulletin;
import fi.internetix.edelphi.pages.DelfoiPageController;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class CreateDelfoiBulletinPageController extends DelfoiPageController {

  public CreateDelfoiBulletinPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_BULLETINS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    DelfoiBulletinDAO bulletinDAO = new DelfoiBulletinDAO();
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);

    List<DelfoiBulletin> bulletins = bulletinDAO.listByDelfoiAndArchived(delfoi, Boolean.FALSE);
    Collections.sort(bulletins, new Comparator<DelfoiBulletin>() {
      @Override
      public int compare(DelfoiBulletin o1, DelfoiBulletin o2) {
        return o2.getCreated().compareTo(o1.getCreated());
      }
    });

    ActionUtils.includeRoleAccessList(pageRequestContext);

    pageRequestContext.getRequest().setAttribute("delfoi", delfoi);
    pageRequestContext.getRequest().setAttribute("bulletins", bulletins);
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/createbulletin.jsp");
  }

}