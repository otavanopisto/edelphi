package fi.internetix.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserGroupDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserGroup;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ManagePanelUsergroupsPageController extends PanelPageController {
  
  public ManagePanelUsergroupsPageController() {
	super();
	setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();

    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    pageRequestContext.getRequest().setAttribute("panel", panel);
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    // Panel's user groups
    
    List<PanelUserGroup> userGroups = panelUserGroupDAO.listByPanelAndStamp(panel, RequestUtils.getActiveStamp(pageRequestContext));
    Collections.sort(userGroups, new Comparator<PanelUserGroup>() {
      @Override
      public int compare(PanelUserGroup o1, PanelUserGroup o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    JSONArray userGroupsJs = new JSONArray();
    for (PanelUserGroup userGroup : userGroups) {
      JSONObject userGroupJs = new JSONObject();
      userGroupJs.put("id", userGroup.getId());
      userGroupJs.put("name", userGroup.getName());
      userGroupJs.put("created", userGroup.getCreated().getTime());
      userGroupJs.put("modified", userGroup.getLastModified().getTime());
      userGroupsJs.add(userGroupJs);
    }
    setJsDataVariable(pageRequestContext, "userGroups", userGroupsJs.toString());
    
    // Panelists
    
    List<PanelUser> panelUsers = panelUserDAO.listByPanelAndStamp(panel, RequestUtils.getActiveStamp(pageRequestContext));
    Collections.sort(panelUsers, new Comparator<PanelUser>() {
      @Override
      public int compare(PanelUser o1, PanelUser o2) {
        return StringUtils.trimToEmpty(o1.getUser().getFullName()).compareTo(StringUtils.trimToEmpty(o2.getUser().getFullName()));
      }
    });
    JSONArray panelUsersJs = new JSONArray();
    for (PanelUser panelUser : panelUsers) {
      JSONObject panelUserJs = new JSONObject();
      panelUserJs.put("id", panelUser.getId().toString());
      panelUserJs.put("userId", panelUser.getUser().getId());
      panelUserJs.put("name", panelUser.getUser().getFullName(true, false)); // last name first, don't fall back to email
      panelUserJs.put("mail", panelUser.getUser().getDefaultEmailAsString());
      panelUsersJs.add(panelUserJs);
    }
    setJsDataVariable(pageRequestContext, "panelUsers", panelUsersJs.toString());
    
    setJsDataVariable(pageRequestContext, "panelId", panel.getId().toString());

    // Panel stamps

    List<PanelStamp> stamps = panelStampDAO.listByPanel(panel);
    Collections.sort(stamps, new Comparator<PanelStamp>() {
      @Override
      public int compare(PanelStamp o1, PanelStamp o2) {
        return o1.getStampTime() == null ? 1 : o2.getStampTime() == null ? -1 : o1.getStampTime().compareTo(o2.getStampTime());
      }
    });
    PanelStamp latestStamp = panel.getCurrentStamp();
    PanelStamp activeStamp = RequestUtils.getActiveStamp(pageRequestContext);
    pageRequestContext.getRequest().setAttribute("stamps", stamps);
    pageRequestContext.getRequest().setAttribute("latestStamp", latestStamp);
    pageRequestContext.getRequest().setAttribute("activeStamp", activeStamp);
    setJsDataVariable(pageRequestContext, "latestStampId", latestStamp.getId().toString());
    setJsDataVariable(pageRequestContext, "activeStampId", activeStamp.getId().toString());
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/managepanelusergroups.jsp");
  }

}