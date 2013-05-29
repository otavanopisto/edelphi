package fi.internetix.edelphi.pages.panel;

import java.util.ArrayList;
import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelUserRoleDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelUserRole;
import fi.internetix.edelphi.utils.LocalizationUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class CreateUserPageController extends PanelPageController {

  public CreateUserPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelUserRoleDAO panelUserRoleDAO = new PanelUserRoleDAO();
    
    List<PanelUserRole> panelUserRoles = panelUserRoleDAO.listAll();
    List<UserRoleBean> userRoleBeans = new ArrayList<UserRoleBean>();
    for (PanelUserRole panelUserRole : panelUserRoles) {
      String name = LocalizationUtils.getLocalizedText(panelUserRole.getName(), pageRequestContext.getRequest().getLocale());
      userRoleBeans.add(new UserRoleBean(panelUserRole.getId(), name));
    }
    pageRequestContext.getRequest().setAttribute("panelUserRoles", userRoleBeans);
    
    pageRequestContext.setIncludeJSP("/jsp/panels/createuser.jsp");
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
