package fi.internetix.edelphi.jsons.panel.admin;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.QueryUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class CopyQueryJSONRequestController extends JSONController {

  public CopyQueryJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    QueryDAO queryDAO = new QueryDAO();
    PanelDAO panelDAO = new PanelDAO();
    
    Query query = queryDAO.findById(jsonRequestContext.getLong("query"));
    Panel targetPanel = panelDAO.findById(jsonRequestContext.getLong("panel"));
    String newName = jsonRequestContext.getString("name");
    boolean copyData = jsonRequestContext.getBoolean("copyData");

    QueryUtils.copyQuery(jsonRequestContext, query, newName, targetPanel, copyData, copyData);
  }

}
