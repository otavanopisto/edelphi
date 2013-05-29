package fi.internetix.edelphi.jsons.panel.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.query.QueryPageHandlerFactory;
import fi.internetix.edelphi.query.expertise.ExpertiseQueryPageHandler;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class CreatePanelExpertExpertiseJSONRequestController extends JSONController {

  public CreatePanelExpertExpertiseJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }

    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();

    String name = jsonRequestContext.getString("newExpertiseName");
    PanelUserExpertiseClass panelUserExpertiseClass = panelUserExpertiseClassDAO.create(panel, name);

    List<PanelUserIntressClass> intressClasses = panelUserIntressClassDAO.listByPanel(panel);

    // Create groups
    Map<String, Long> expertiseClassMap = new HashMap<String, Long>();
    for (PanelUserIntressClass intressClass : intressClasses) {
      PanelUserExpertiseGroup panelUserExpertiseGroup = panelUserExpertiseGroupDAO.create(panel, panelUserExpertiseClass, intressClass, null, panel.getCurrentStamp());
      expertiseClassMap.put(intressClass.getId().toString(), panelUserExpertiseGroup.getId());
    }
    
    List<QueryPage> expertisePages = queryPageDAO.listByQueryParentFolderAndPageType(panel.getRootFolder(), QueryPageType.EXPERTISE);
    for (QueryPage expertisePage : expertisePages) {
      ExpertiseQueryPageHandler pageHandler = (ExpertiseQueryPageHandler) QueryPageHandlerFactory.getInstance().buildPageHandler(QueryPageType.EXPERTISE);
      pageHandler.synchronizedFields(expertisePage);
    }
    
    jsonRequestContext.addResponseParameter("id", panelUserExpertiseClass.getId().toString());
    jsonRequestContext.addResponseParameter("name", panelUserExpertiseClass.getName());
    jsonRequestContext.addResponseParameter("newExpertiseGroups", expertiseClassMap);
  }
  
}
