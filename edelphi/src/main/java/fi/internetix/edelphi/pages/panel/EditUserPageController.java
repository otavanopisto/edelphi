package fi.internetix.edelphi.pages.panel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserRoleDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserRole;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.utils.LocalizationUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class EditUserPageController extends PanelPageController {

  public EditUserPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    UserDAO userDAO = new UserDAO();
    PanelDAO panelDAO = new PanelDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUserRoleDAO panelUserRoleDAO = new PanelUserRoleDAO();

    User user = userDAO.findById(pageRequestContext.getLong("userId"));
    pageRequestContext.getRequest().setAttribute("user", user);
    
    List<PanelUserRole> panelUserRoles = panelUserRoleDAO.listAll();
    List<UserRoleBean> userRoleBeans = new ArrayList<UserRoleBean>();
    for (PanelUserRole panelUserRole : panelUserRoles) {
      String name = LocalizationUtils.getLocalizedText(panelUserRole.getName(), pageRequestContext.getRequest().getLocale());
      userRoleBeans.add(new UserRoleBean(panelUserRole.getId(), name));
    }
    pageRequestContext.getRequest().setAttribute("panelUserRoles", userRoleBeans);
    
    HashSet<Long> userRoles = new HashSet<Long>();
    Panel panel = panelDAO.findById(pageRequestContext.getLong("panelId"));
    List<PanelUser> panelUsers = panelUserDAO.listByPanelAndUserAndStamp(panel, user, panel.getCurrentStamp());
    for (PanelUser pUser : panelUsers) {
      userRoles.add(pUser.getRole().getId());
    }
    pageRequestContext.getRequest().setAttribute("userRoles", userRoles);
    
    pageRequestContext.setIncludeJSP("/jsp/panels/edituser.jsp");
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