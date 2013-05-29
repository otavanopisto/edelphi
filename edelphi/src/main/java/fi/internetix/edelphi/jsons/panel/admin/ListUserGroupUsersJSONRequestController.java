package fi.internetix.edelphi.jsons.panel.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelUserGroupDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.PanelUserGroup;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class ListUserGroupUsersJSONRequestController extends JSONController {

  public ListUserGroupUsersJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long userGroupId = jsonRequestContext.getLong("userGroupId");
    PanelUserGroupDAO panelUserGroupDAO = new PanelUserGroupDAO();
    PanelUserGroup panelUserGroup = panelUserGroupDAO.findById(userGroupId);
    List<User> users = new ArrayList<User>();
    users.addAll(panelUserGroup.getUsers());
    Collections.sort(users, new Comparator<User>() {
      @Override
      public int compare(User o1, User o2) {
        String s1 = o1.getFullName(false);
        if (o1.getDefaultEmailAsString() != null) {
          s1 = (s1 == null ? "" : s1 + " ") + o1.getDefaultEmailAsString();
        }
        s1 = s1 ==  null ? "" : s1;
        String s2 = o2.getFullName(false);
        if (o2.getDefaultEmailAsString() != null) {
          s2 = (s2 == null ? "" : s2 + " ") + o2.getDefaultEmailAsString();
        }
        s2 = s2 ==  null ? "" : s2;
        return s1.toLowerCase().compareTo(s2.toLowerCase());
      }
    });
    JSONArray jsonArr = new JSONArray();
    for (User user : users) {
      JSONObject jsonObj = new JSONObject();
      jsonObj.put("id", user.getId().toString());
      jsonObj.put("name", user.getFullName(false));
      jsonObj.put("mail", user.getDefaultEmailAsString());
      jsonArr.add(jsonObj);
    }
    jsonRequestContext.addResponseParameter("users", jsonArr.toString());
  }
  
}
