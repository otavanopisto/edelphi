package fi.internetix.edelphi.pages.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.actions.DelfoiActionDAO;
import fi.internetix.edelphi.dao.panels.PanelSettingsTemplateDAO;
import fi.internetix.edelphi.dao.panels.PanelSettingsTemplateRoleDAO;
import fi.internetix.edelphi.dao.users.UserRoleDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiAction;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelSettingsTemplate;
import fi.internetix.edelphi.domainmodel.panels.PanelSettingsTemplateRole;
import fi.internetix.edelphi.domainmodel.users.UserRole;
import fi.internetix.edelphi.pages.PageController;
import fi.internetix.edelphi.utils.LocalizationUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ManagePanelTemplatePageController extends PageController {

  public ManagePanelTemplatePageController() {
    super();
    
    setAccessAction(DelfoiActionName.MANAGE_SYSTEM_SETTINGS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    DelfoiActionDAO delfoiActionDAO = new DelfoiActionDAO();
    PanelSettingsTemplateDAO panelSettingsTemplateDAO = new PanelSettingsTemplateDAO();
    PanelSettingsTemplateRoleDAO panelSettingsTemplateRoleDAO = new PanelSettingsTemplateRoleDAO();
    UserRoleDAO userRoleDAO = new UserRoleDAO();

    Long templateId = pageRequestContext.getLong("templateId");
    PanelSettingsTemplate template = panelSettingsTemplateDAO.findById(templateId);
    
    // Populate role list
    List<UserRole> roleList = userRoleDAO.listAll();
    
    List<DelfoiAction> actionList = delfoiActionDAO.listByScope(DelfoiActionScope.PANEL);
    List<PanelSettingsTemplateRole> rolesByTemplate = panelSettingsTemplateRoleDAO.listByTemplate(template);
    
    Map<Long, Map<Long, Boolean>> roleMapForActions = new HashMap<Long, Map<Long, Boolean>>();

    for (UserRole role : roleList) {
      roleMapForActions.put(role.getId(), new HashMap<Long, Boolean>());
    }

    for (PanelSettingsTemplateRole templateRole : rolesByTemplate) {
      Map<Long, Boolean> actionsEnabledForRole = roleMapForActions.get(templateRole.getUserRole().getId());
      
      actionsEnabledForRole.put(templateRole.getDelfoiAction().getId(), Boolean.TRUE);
    }
    
    List<UserRoleBean> userRoleBeans = new ArrayList<UserRoleBean>();
    for (UserRole userRole : roleList) {
      String name = LocalizationUtils.getLocalizedText(userRole.getName(), pageRequestContext.getRequest().getLocale());
      userRoleBeans.add(new UserRoleBean(userRole.getId(), name));
    }
    pageRequestContext.getRequest().setAttribute("roleList", userRoleBeans);
    pageRequestContext.getRequest().setAttribute("template", template);
    pageRequestContext.getRequest().setAttribute("actionList", actionList);
    pageRequestContext.getRequest().setAttribute("actionStatus", roleMapForActions);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/admin/managepaneltemplate.jsp");
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