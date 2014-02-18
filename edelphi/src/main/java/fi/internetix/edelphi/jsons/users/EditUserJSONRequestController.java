package fi.internetix.edelphi.jsons.users;

import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserRoleDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.internetix.edelphi.domainmodel.panels.PanelUserRole;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.UserUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class EditUserJSONRequestController extends JSONController {

  public EditUserJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    PanelDAO panelDAO = new PanelDAO();
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUserRoleDAO panelUserRoleDAO = new PanelUserRoleDAO();
    
    User user = userDAO.findById(jsonRequestContext.getLong("userId"));
    
    String firstName = jsonRequestContext.getString("firstName");
    String lastName = jsonRequestContext.getString("lastName");
    String nickname = jsonRequestContext.getString("nickname");
    String email = jsonRequestContext.getLowercaseString("email");
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    // Update user's basic information
    
    userDAO.update(user, firstName, lastName, nickname, loggedUser);
    
    // Add, edit, or delete user e-mail address
    
    Long userEmailId = jsonRequestContext.getLong("emailId");
    UserEmail userEmail = userEmailId == null ? null : userEmailDAO.findById(userEmailId); 
    if (email == null && userEmail != null) {
      userDAO.updateDefaultEmail(user, null, loggedUser);
      userDAO.removeUserEmail(user, userEmail, loggedUser);
      userEmailDAO.delete(userEmail);
      List<UserEmail> userEmails = userEmailDAO.listByUser(user);
      if (!userEmails.isEmpty()) {
        userDAO.updateDefaultEmail(user, userEmails.get(0), loggedUser);
      }
    }
    else if (email != null && userEmail != null && !email.equals(userEmail.getAddress())) {
      userEmailDAO.updateAddress(userEmail, email);
    }
    else if (email != null && userEmail == null) {
      userEmail = userEmailDAO.create(user, email);
      userDAO.addUserEmail(user, userEmail, true, loggedUser);
    }
    
    // Handle user's roles in the panel
    
    Long panelId = jsonRequestContext.getLong("panelId");
    Panel panel = panelId == null ? null : panelDAO.findById(panelId);
    if (panel != null) {
      HashSet<Long> selectedRoles = new HashSet<Long>();
      String roleStr = jsonRequestContext.getString("roles");
      if (roleStr != null) {
        StringTokenizer roles = new StringTokenizer(roleStr, ",");
        while (roles.hasMoreTokens()) {
          selectedRoles.add(new Long(roles.nextToken()));
        }
      }
      List<PanelUser> panelUsers = panelUserDAO.listByPanelAndUserAndStamp(panel, user, panel.getCurrentStamp());
      HashSet<Long> currentRoles = new HashSet<Long>();
      for (PanelUser panelUser : panelUsers) {
        currentRoles.add(panelUser.getRole().getId());
      }
      for (Long roleId : selectedRoles) {
        if (!currentRoles.contains(roleId)) {
          PanelUserRole panelUserRole = panelUserRoleDAO.findById(roleId);
          panelUserDAO.create(panel, user, panelUserRole, PanelUserJoinType.ADDED, panel.getCurrentStamp(), loggedUser);
        }
        else {
          for (int i = 0; i < panelUsers.size(); i++) {
            if (panelUsers.get(i).getRole().getId().equals(roleId)) {
              panelUsers.remove(i);
              break;
            }
          }
        }
      }
      for (PanelUser panelUser : panelUsers) {
        UserUtils.archivePanelUser(panelUser, loggedUser);
      }
    }
  }

}
