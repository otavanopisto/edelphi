package fi.internetix.edelphi.jsons.admin;

import java.util.ArrayList;
import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.actions.DelfoiActionDAO;
import fi.internetix.edelphi.dao.actions.DelfoiUserRoleActionDAO;
import fi.internetix.edelphi.dao.base.SystemUserRoleDAO;
import fi.internetix.edelphi.dao.users.DelfoiUserRoleDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiAction;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.actions.DelfoiUserRoleAction;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.SystemUserRole;
import fi.internetix.edelphi.domainmodel.base.SystemUserRoleType;
import fi.internetix.edelphi.domainmodel.users.DelfoiUserRole;
import fi.internetix.edelphi.domainmodel.users.UserRole;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class SaveDelfoiActionAccessJSONRequestController extends JSONController {

  public SaveDelfoiActionAccessJSONRequestController() {
    super();
    
    setAccessAction(DelfoiActionName.MANAGE_SYSTEM_SETTINGS, DelfoiActionScope.DELFOI);
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    DelfoiUserRoleDAO userRoleDAO = new DelfoiUserRoleDAO();
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    DelfoiUserRoleActionDAO delfoiUserRoleActionDAO = new DelfoiUserRoleActionDAO();
    SystemUserRoleDAO systemUserRoleDAO = new SystemUserRoleDAO();
    
    Delfoi delfoi = RequestUtils.getDelfoi(jsonRequestContext);
    
    List<DelfoiUserRole> delfoiRoles = userRoleDAO.listAll();
    List<UserRole> roleList = new ArrayList<UserRole>();
    roleList.addAll(delfoiRoles);
    SystemUserRole systemUserRole = systemUserRoleDAO.findByType(SystemUserRoleType.EVERYONE);
    roleList.add(systemUserRole);
    
    List<DelfoiAction> delfoiActions = delfoiActionDAO.listAll();

    for (UserRole role : roleList) {
      // List the old rules that this role has
      List<DelfoiUserRoleAction> oldRoleActions = delfoiUserRoleActionDAO.listByDelfoiAndUserRole(RequestUtils.getDelfoi(jsonRequestContext), role);
      
      // Go through all the actions in system
      for (DelfoiAction action : delfoiActions) {
        Long long1 = jsonRequestContext.getLong("delfoiActionRole." + role.getId() + "." + action.getId());
        boolean selected = long1 != null ? long1.intValue() == 1 : false;
        DelfoiUserRoleAction oldAction = null;
        
        // Check if the rule already exists 
        for (DelfoiUserRoleAction oldRoleAction : oldRoleActions) {
          if (oldRoleAction.getDelfoiAction().getId() == action.getId()) {
            oldAction = oldRoleAction;
            break;
          }
        }
        
        if ((!selected) && (oldAction != null)) {
          // Exists but is not selected -> delete
          delfoiUserRoleActionDAO.delete(oldAction);
        } else if ((selected) && (oldAction == null)) {
          // Selected but doesn't exists -> create
          delfoiUserRoleActionDAO.create(delfoi, action, role);
        }
      }
    }
    
//    String redirectURL = jsonRequestContext.getRequest().getContextPath() + "/panels/viewpanel.page?panelId=" + panel.getId();
//    jsonRequestContext.setRedirectURL(redirectURL);
  }
  
}
