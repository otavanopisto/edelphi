package fi.internetix.edelphi.jsons.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class ListAvailablePanelsJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    PanelDAO panelDAO = new PanelDAO();
    User user = userDAO.findById(jsonRequestContext.getLoggedUserId());
    Delfoi delfoi = RequestUtils.getDelfoi(jsonRequestContext);
    List<Panel> userPanels = panelDAO.listByDelfoiAndUser(delfoi, user);
    Collections.sort(userPanels, new Comparator<Panel>() {
      @Override
      public int compare(Panel o1, Panel o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    JSONArray jsonArr = new JSONArray();
    for (Panel userPanel : userPanels) {
      JSONObject jsonObj = new JSONObject();
      jsonObj.put("id", userPanel.getId().toString());
      jsonObj.put("name", userPanel.getName());
      jsonArr.add(jsonObj);
    }
    jsonRequestContext.addResponseParameter("panels", jsonArr.toString());
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel != null) {
      jsonRequestContext.addResponseParameter("currentPanel", panel.getId());
    }
  }
}
