package fi.internetix.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class CreateQueryPageController extends PanelPageController {

  public CreateQueryPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    QueryDAO queryDAO = new QueryDAO();

    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }

    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    
    List<Query> queries = queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
    Collections.sort(queries, new Comparator<Query>() {
      @Override
      public int compare(Query o1, Query o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });
    
    JSONArray querySectionsJson = new JSONArray();
    
    JSONObject querySectionJson = new JSONObject();

    querySectionJson.put("id", "temp" + new Date().getTime());
    querySectionJson.put("title", messages.getText(locale, "generic.query.defaultSectionTitle"));
    querySectionJson.put("visible", "1");
    querySectionJson.put("isNew", "true");
    querySectionsJson.add(querySectionJson);
    
    setJsDataVariable(pageRequestContext, "querySections", querySectionsJson.toString());
    
    appendExpertiseClasses(pageRequestContext);

    ActionUtils.includeRoleAccessList(pageRequestContext);

    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("queries", queries);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/createquery.jsp");
  }
 
  private void appendExpertiseClasses(PageRequestContext pageRequestContext) {
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();

    Panel panel = RequestUtils.getPanel(pageRequestContext);
    
    // PanelUserExpertiseClass
    
    List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(panel);
    Collections.sort(expertiseClasses, new Comparator<PanelUserExpertiseClass>() {
      @Override
      public int compare(PanelUserExpertiseClass o1, PanelUserExpertiseClass o2) {
        return o1.getId().compareTo(o2.getId());
      }
    });

    JSONArray jsonArr = new JSONArray();
    for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
      JSONObject jsonObj = new JSONObject();
      
      jsonObj.put("id", expertiseClass.getId().toString());
      jsonObj.put("name", expertiseClass.getName());
      
      jsonArr.add(jsonObj);
    }
    setJsDataVariable(pageRequestContext, "panelExpertiseClasses", jsonArr.toString());

    // PanelUserIntressClass
    
    List<PanelUserIntressClass> intressClasses = panelUserIntressClassDAO.listByPanel(panel);
    Collections.sort(intressClasses, new Comparator<PanelUserIntressClass>() {
      @Override
      public int compare(PanelUserIntressClass o1, PanelUserIntressClass o2) {
        return o1.getId().compareTo(o2.getId());
      }
    });
    
    jsonArr = new JSONArray();
    for (PanelUserIntressClass intressClass : intressClasses) {
      JSONObject jsonObj = new JSONObject();
      
      jsonObj.put("id", intressClass.getId().toString());
      jsonObj.put("name", intressClass.getName());
      
      jsonArr.add(jsonObj);
    }
    setJsDataVariable(pageRequestContext, "panelIntressClasses", jsonArr.toString());

    // Expertise Groups
    
    jsonArr = new JSONArray();
    List<PanelUserExpertiseGroup> panelUserExpertiseGroups = panelUserExpertiseGroupDAO.listByPanelAndStamp(panel, panel.getCurrentStamp());
    for (PanelUserExpertiseGroup expertiseGroup : panelUserExpertiseGroups) {
      JSONObject jsonObj = new JSONObject();
      
      jsonObj.put("id", expertiseGroup.getId().toString());
      jsonObj.put("expertiseClassId", expertiseGroup.getExpertiseClass().getId().toString());
      jsonObj.put("intressClassId", expertiseGroup.getIntressClass().getId().toString());
      
      jsonArr.add(jsonObj);
    }
    setJsDataVariable(pageRequestContext, "panelExpertiseGroups", jsonArr.toString());
  }
  
}