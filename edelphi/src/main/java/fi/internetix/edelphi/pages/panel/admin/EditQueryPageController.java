package fi.internetix.edelphi.pages.panel.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionAnswerDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querylayout.QuerySectionDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QuerySection;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.query.QueryOption;
import fi.internetix.edelphi.query.QueryOptionDataType;
import fi.internetix.edelphi.query.QueryOptionEditor;
import fi.internetix.edelphi.query.QueryPageHandler;
import fi.internetix.edelphi.query.QueryPageHandlerFactory;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.QueryPageUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.ResourceLockUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.controllers.PageRequestContext;

public class EditQueryPageController extends PanelPageController {

  public EditQueryPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    QueryDAO queryDAO = new QueryDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QuerySectionDAO querySectionDAO = new QuerySectionDAO();
    UserDAO userDAO = new UserDAO();
    
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    Long queryId = pageRequestContext.getLong("queryId");
    Query query = queryDAO.findById(queryId);
    if (query == null) {
      throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
    }
    
    Locale locale = pageRequestContext.getRequest().getLocale();
    boolean resourceLocked = false;
    
    User loggedUser = userDAO.findById(pageRequestContext.getLoggedUserId());
    if (ResourceLockUtils.isLocked(loggedUser, query)) {
      User lockCreator = ResourceLockUtils.getResourceLockCreator(query);
      Messages messages = Messages.getInstance();
      pageRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panelAdmin.block.query.lockedMessage", new Object[] { lockCreator.getFullName(false, true) }));
      resourceLocked = true;
    }
    
    List<Query> queries = queryDAO.listByFolderAndArchived(panel.getRootFolder(), Boolean.FALSE);
    Collections.sort(queries, new Comparator<Query>() {
      @Override
      public int compare(Query o1, Query o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
      }
    });

    pageRequestContext.getRequest().setAttribute("panel", panel);
    pageRequestContext.getRequest().setAttribute("queries", queries);
    
    if (resourceLocked == false) {
      ResourceLockUtils.lockResource(loggedUser, query);
      appendExpertiseClasses(pageRequestContext);
      
      JSONArray querySectionsJson = new JSONArray();
      
      List<QuerySection> querySections = querySectionDAO.listByQuery(query);
      
      Collections.sort(querySections, new Comparator<QuerySection>() {
        @Override
        public int compare(QuerySection o1, QuerySection o2) {
          return o1.getSectionNumber() - o2.getSectionNumber();
        }
      });
      
      for (QuerySection querySection : querySections) {
        JSONObject querySectionJson = new JSONObject();
  
        JSONArray queryPagesJson = new JSONArray();
        List<QueryPage> queryPages = queryPageDAO.listByQuerySection(querySection);
        
        Collections.sort(queryPages, new Comparator<QueryPage>() {
          @Override
          public int compare(QueryPage o1, QueryPage o2) {
            return o1.getPageNumber() - o2.getPageNumber();
          }
        });
        
        for (QueryPage queryPage : queryPages) {
          queryPagesJson.add(queryPage.getId());
        }
  
        querySectionJson.put("id", querySection.getId());
        querySectionJson.put("title", querySection.getTitle());
        querySectionJson.put("visible", querySection.getVisible() ? "1" : "0");
        querySectionJson.put("commentable", querySection.getCommentable() ? "1" : "0");
        querySectionJson.put("viewDiscussions", querySection.getViewDiscussions() ? "1" : "0");
        querySectionJson.put("pages", queryPagesJson);
      
        querySectionsJson.add(querySectionJson);
      }
  
      JSONArray queryPagesJson = new JSONArray();
      List<QueryPage> queryPages = queryPageDAO.listByQuery(query);
      for (QueryPage queryPage : queryPages) {
        JSONObject queryPageJson = new JSONObject();
        
        JSONArray optionsJson = new JSONArray();
        QueryPageHandler queryPageHandler = QueryPageHandlerFactory.getInstance().buildPageHandler(queryPage.getPageType());
        List<QueryOption> definedOptions = queryPageHandler.getDefinedOptions();
        for (QueryOption definedOption : definedOptions) {
          String caption = Messages.getInstance().getText(pageRequestContext.getRequest().getLocale(), definedOption.getLocaleKey());
          String name = definedOption.getName();
          QueryOptionEditor editor = definedOption.getEditor();
          JSONObject optionJson = new JSONObject();
          
          optionJson.put("caption", caption);
          optionJson.put("name", name);
          optionJson.put("editor", editor);
          optionJson.put("type", definedOption.getType());
          optionJson.put("editableWithAnswers", definedOption.isEditableWithAnswers());
          
          switch (definedOption.getType()) {
            case PAGE:
              if ("title".equals(name)) {
                optionJson.put("value", queryPage.getTitle());
              } else if ("visible".equals(name)) {
                optionJson.put("value", queryPage.getVisible() ? "1" : "0");
              }
            break;
            default:
              String setting = QueryPageUtils.getSetting(queryPage, name);
              
              if (editor.getDataType() == QueryOptionDataType.JSON_SERIALIZED) {
                setting = QueryPageUtils.filterJsonSerializedSetting(setting);
              }
              
              optionJson.put("value", setting);
            break;
          }
          
          optionsJson.add(optionJson);
        }
        
        QueryQuestionAnswerDAO answerDAO = new QueryQuestionAnswerDAO();
        Boolean hasAnswers = answerDAO.countByQueryPage(queryPage) > 0;
        
        queryPageJson.put("id", queryPage.getId());
        queryPageJson.put("title", queryPage.getTitle());
        queryPageJson.put("type", queryPage.getPageType());
        queryPageJson.put("number", queryPage.getPageNumber());
        queryPageJson.put("options", optionsJson);
        queryPageJson.put("hasAnswers", hasAnswers.toString());
        
        queryPagesJson.add(queryPageJson);
      }
      
      pageRequestContext.getRequest().setAttribute("resourceLocked", resourceLocked);
      setJsDataVariable(pageRequestContext, "querySections", querySectionsJson.toString());
      setJsDataVariable(pageRequestContext, "queryPages", queryPagesJson.toString());
    }
    
    pageRequestContext.getRequest().setAttribute("query", query);
    ActionUtils.includeRoleAccessList(pageRequestContext);
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/editquery.jsp");
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