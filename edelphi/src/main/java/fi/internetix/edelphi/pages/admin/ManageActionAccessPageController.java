package fi.internetix.edelphi.pages.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.actions.DelfoiActionDAO;
import fi.internetix.edelphi.dao.actions.DelfoiUserRoleActionDAO;
import fi.internetix.edelphi.dao.base.SystemUserRoleDAO;
import fi.internetix.edelphi.dao.users.DelfoiUserRoleDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiAction;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.actions.DelfoiUserRoleAction;
import fi.internetix.edelphi.domainmodel.base.SystemUserRole;
import fi.internetix.edelphi.domainmodel.base.SystemUserRoleType;
import fi.internetix.edelphi.domainmodel.users.DelfoiUserRole;
import fi.internetix.edelphi.domainmodel.users.UserRole;
import fi.internetix.edelphi.pages.PageController;
import fi.internetix.edelphi.utils.LocalizationUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ManageActionAccessPageController extends PageController {

  public ManageActionAccessPageController() {
    super();
    
    setAccessAction(DelfoiActionName.MANAGE_SYSTEM_SETTINGS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    DelfoiUserRoleDAO userRoleDAO = new DelfoiUserRoleDAO();
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    DelfoiUserRoleActionDAO delfoiUserRoleActionDAO = new DelfoiUserRoleActionDAO();
    SystemUserRoleDAO systemUserRoleDAO = new SystemUserRoleDAO();

    // Populate role list
    List<DelfoiUserRole> delfoiRoleList = userRoleDAO.listAll();
    List<UserRole> roleList = new ArrayList<UserRole>();
    roleList.addAll(delfoiRoleList);
    SystemUserRole systemUserRole = systemUserRoleDAO.findByType(SystemUserRoleType.EVERYONE);
    roleList.add(systemUserRole);
    
    List<DelfoiAction> actionList = delfoiActionDAO.listByScope(DelfoiActionScope.DELFOI);
    List<DelfoiUserRoleAction> roleActionsByDelfoi = delfoiUserRoleActionDAO.listByDelfoi(RequestUtils.getDelfoi(pageRequestContext));
    
    Map<Long, Map<Long, Boolean>> roleMapForActions = new HashMap<Long, Map<Long, Boolean>>();

    for (UserRole role : roleList) {
      roleMapForActions.put(role.getId(), new HashMap<Long, Boolean>());
    }

    for (DelfoiUserRoleAction action : roleActionsByDelfoi) {
      Map<Long, Boolean> actionsEnabledForRole = roleMapForActions.get(action.getUserRole().getId());
      
      actionsEnabledForRole.put(action.getDelfoiAction().getId(), Boolean.TRUE);
    }
    
    List<UserRoleBean> userRoleBeans = new ArrayList<UserRoleBean>();
    for (UserRole userRole : roleList) {
      String name = LocalizationUtils.getLocalizedText(userRole.getName(), pageRequestContext.getRequest().getLocale());
      userRoleBeans.add(new UserRoleBean(userRole.getId(), name));
    }
    pageRequestContext.getRequest().setAttribute("roleList", userRoleBeans);
    pageRequestContext.getRequest().setAttribute("actionList", actionList);
    pageRequestContext.getRequest().setAttribute("actionStatus", roleMapForActions);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/managedelfoiactionaccess.jsp");
  }

  public class UserRoleBean {
    
    public UserRoleBean(Long id, String name) {
      this.id = id;
      this.name = name;
    }
    
    public Long getId() {
      return id;
    }
    
    public String getName() {
      return name;
    }
    
    private Long id;
    private String name;
  }

}