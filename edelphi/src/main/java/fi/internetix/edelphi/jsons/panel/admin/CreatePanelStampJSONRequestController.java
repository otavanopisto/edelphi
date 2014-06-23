package fi.internetix.edelphi.jsons.panel.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserGroupDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserGroup;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.QueryDataUtils;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class CreatePanelStampJSONRequestController extends JSONController {

  public CreatePanelStampJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }

    User loggedUser = RequestUtils.getUser(jsonRequestContext);

    QueryDAO queryDAO = new QueryDAO();
    PanelDAO panelDAO = new PanelDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    // Create the stamp

    String name = jsonRequestContext.getString("title");
    String description = jsonRequestContext.getString("description");
    PanelStamp oldStamp = panel.getCurrentStamp();
    panelStampDAO.update(oldStamp, name, description, new Date(), loggedUser);
    PanelStamp newStamp = panelStampDAO.create(panel, messages.getText(locale, "createPanel.server.defaultStampName"), null, null, loggedUser);

    // Stamp users
    
    Map<Long, PanelUser> panelUserMap = new HashMap<Long, PanelUser>();
    List<PanelUser> panelUsers = panelUserDAO.listByPanelAndStamp(panel, oldStamp);
    for (PanelUser panelUser : panelUsers) {
      PanelUser newPanelUser = panelUserDAO.create(
          panel,
          panelUser.getUser(),
          panelUser.getRole(),
          panelUser.getJoinType(),
          newStamp,
          panelUser.getCreator(),
          panelUser.getCreated(),
          panelUser.getLastModifier(),
          panelUser.getLastModified());
      panelUserMap.put(panelUser.getId(), newPanelUser);
    }
    
    // Stamp user groups
    
    List<PanelUserGroup> panelUserGroups = panelUserGroupDAO.listByPanelAndStamp(panel, oldStamp);
    for (PanelUserGroup panelUserGroup : panelUserGroups) {
      List<User> groupUsers = new ArrayList<User>();
      groupUsers.addAll(panelUserGroup.getUsers());
      panelUserGroupDAO.create(
          panel,
          panelUserGroup.getName(),
          groupUsers,
          newStamp,
          panelUserGroup.getCreator(),
          panelUserGroup.getCreated(),
          panelUserGroup.getLastModifier(),
          panelUserGroup.getLastModified());
    }
    
    // Stamp queries
    
    List<Query> queries = queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
    for (Query query : queries) {
      QueryUtils.stampQuery(query, oldStamp, newStamp);
    }
    
    // Stamp panel user expertise groups

    List<PanelUserExpertiseGroup> groups = panelUserExpertiseGroupDAO.listByPanelAndStamp(panel, oldStamp);
    for (PanelUserExpertiseGroup group : groups) {
      PanelUserExpertiseGroup newExpertiseGroup = panelUserExpertiseGroupDAO.create(panel, group.getExpertiseClass(), group.getIntressClass(), group.getColor(), newStamp);
      List<PanelExpertiseGroupUser> expertiseGroupUsers = panelExpertiseGroupUserDAO.listByGroupAndArchived(group, Boolean.FALSE);
      for (PanelExpertiseGroupUser expertiseGroupUser : expertiseGroupUsers) {
        panelExpertiseGroupUserDAO.create(newExpertiseGroup, panelUserMap.get(expertiseGroupUser.getPanelUser().getId()), expertiseGroupUser.getWeight());
      }
    }

    // Update current stamp
    
    panelDAO.updateCurrentStamp(panel, newStamp, loggedUser);
    
    // Ensure that the new stamp is immediately active. Also clear all query reply ids in session
    // as they could potentially point to the query reply id of the previous stamp
    
    RequestUtils.setActiveStamp(jsonRequestContext, newStamp.getId());
    QueryDataUtils.clearQueryReplyIds(jsonRequestContext.getRequest().getSession());
    
    jsonRequestContext.addResponseParameter("stampId", oldStamp.getId());
    jsonRequestContext.addResponseParameter("name", oldStamp.getName());
    jsonRequestContext.addResponseParameter("description", oldStamp.getDescription());
    jsonRequestContext.addResponseParameter("stampTime", oldStamp.getStampTime().getTime());
  }
  
}
