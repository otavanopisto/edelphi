package fi.internetix.edelphi.jsons.users;

import java.util.StringTokenizer;

import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserRoleDAO;
import fi.internetix.edelphi.dao.users.DelfoiUserDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiDefaults;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.internetix.edelphi.domainmodel.panels.PanelUserRole;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class CreateUserJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    PanelDAO panelDAO = new PanelDAO();
    UserDAO userDAO = new UserDAO();
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUserRoleDAO paneluserRoleDAO = new PanelUserRoleDAO();
    
    String firstName = jsonRequestContext.getString("firstName");
    String lastName = jsonRequestContext.getString("lastName");
    String nickname = jsonRequestContext.getString("nickname");
    User creator = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    Panel panel = panelDAO.findById(jsonRequestContext.getLong("panelId"));
    User user = userDAO.create(firstName, lastName, nickname, creator);
    
    Delfoi delfoi = RequestUtils.getDelfoi(jsonRequestContext);
    DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
    DelfoiDefaults delfoiDefaults = RequestUtils.getDefaults(jsonRequestContext);
    delfoiUserDAO.create(delfoi, user, delfoiDefaults.getDefaultDelfoiUserRole(), creator);

    String email = jsonRequestContext.getLowercaseString("email");
    if (email != null) {
      UserEmail userEmail = userEmailDAO.create(user, email);
      userDAO.updateDefaultEmail(user, userEmail, creator);
    }
    
    String roles = jsonRequestContext.getString("roles");
    if (roles != null) {
      StringTokenizer roleTokenizer = new StringTokenizer(roles, ",");
      while (roleTokenizer.hasMoreTokens()) {
        PanelUserRole role = paneluserRoleDAO.findById(new Long(roleTokenizer.nextToken()));
        panelUserDAO.create(panel, user, role, PanelUserJoinType.ADDED, panel.getCurrentStamp(), creator);
      }
    }
  }

}
