package fi.internetix.edelphi.pages.panel;

import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.users.UserIdentificationDAO;
import fi.internetix.edelphi.dao.users.UserPasswordDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.panels.PanelAccessLevel;
import fi.internetix.edelphi.domainmodel.panels.PanelState;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserIdentification;
import fi.internetix.edelphi.domainmodel.users.UserPassword;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ProfilePageController extends PanelPageController {

  public ProfilePageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelDAO panelDAO = new PanelDAO();
    Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
    User loggedUser = RequestUtils.getUser(pageRequestContext);
    
    if (loggedUser != null) {
      UserPasswordDAO userPasswordDAO = new UserPasswordDAO();
      pageRequestContext.getRequest().setAttribute("user", loggedUser);

      UserPassword userPassword = userPasswordDAO.findByUser(loggedUser);
      boolean hasPassword = userPassword != null;
      
      // Logon types
      
      AuthUtils.includeAuthSources(pageRequestContext);
      UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
      List<UserIdentification> userIdentifications = userIdentificationDAO.listByUser(loggedUser);
      pageRequestContext.getRequest().setAttribute("userIdentifications", userIdentifications);

      // Ensures returning to the profile page if an external authentication source is added to the profile
      AuthUtils.storeRedirectUrl(pageRequestContext, RequestUtils.getCurrentUrl(pageRequestContext.getRequest(), true));

      pageRequestContext.getRequest().setAttribute("userHasPassword", hasPassword);
    }

    ActionUtils.includeRoleAccessList(pageRequestContext);

    pageRequestContext.getRequest().setAttribute("panel", RequestUtils.getPanel(pageRequestContext));
    
    pageRequestContext.getRequest().setAttribute("openPanels", 
        panelDAO.listByDelfoiAndAccessLevelAndState(delfoi, PanelAccessLevel.OPEN, PanelState.IN_PROGRESS));

    pageRequestContext.setIncludeJSP("/jsp/pages/panel/profile.jsp");
  }

}
