package fi.internetix.edelphi.pages.panel.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelDAO;
import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class PanelExpertsPageController extends PanelPageController {
  
  public PanelExpertsPageController() {
    super();
    
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    PanelDAO panelDAO = new PanelDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    
    Long panelId = pageRequestContext.getLong("panelId");
    Panel panel = panelDAO.findById(panelId);
    pageRequestContext.getRequest().setAttribute("panel", panel);

    // Panel
    
    JSONObject jsonObj = new JSONObject();
    jsonObj.put("id", panel.getId().toString());
    jsonObj.put("name", panel.getName());
    setJsDataVariable(pageRequestContext, "panel", jsonObj.toString());
    
    // Panel Users
    
    JSONArray jsonArr = new JSONArray();
    List<PanelUser> panelUsers = panelUserDAO.listByPanelAndStamp(panel, panel.getCurrentStamp());
    Collections.sort(panelUsers, new Comparator<PanelUser>() {
      @Override
      public int compare(PanelUser o1, PanelUser o2) {
        return StringUtils.trimToEmpty(o1.getUser().getFullName()).compareTo(StringUtils.trimToEmpty(o2.getUser().getFullName()));
      }
    });
    for (PanelUser panelUser : panelUsers) {
      jsonObj = new JSONObject();
      
      jsonObj.put("id", panelUser.getId().toString());
      jsonObj.put("role", panelUser.getRole().toString());
      jsonObj.put("name", panelUser.getUser().getFullName(true, false));
      jsonObj.put("email", panelUser.getUser().getDefaultEmailAsObfuscatedString());
      
      jsonArr.add(jsonObj);
    }
    setJsDataVariable(pageRequestContext, "panelUsers", jsonArr.toString());
    
    // PanelUserExpertiseClass
    
    List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(panel);
    Collections.sort(expertiseClasses, new Comparator<PanelUserExpertiseClass>() {
      @Override
      public int compare(PanelUserExpertiseClass o1, PanelUserExpertiseClass o2) {
        return o1.getId().compareTo(o2.getId());
      }
    });
    List<PanelUserIntressClass> interestClasses = panelUserIntressClassDAO.listByPanel(panel);
    Collections.sort(interestClasses, new Comparator<PanelUserIntressClass>() {
      @Override
      public int compare(PanelUserIntressClass o1, PanelUserIntressClass o2) {
        return o1.getId().compareTo(o2.getId());
      }
    });
    
    List<QueryPage> expertisePages = queryPageDAO.listByQueryParentFolderAndPageType(panel.getRootFolder(), QueryPageType.EXPERTISE);
    List<Long> unusedExpertiseClasses = new ArrayList<Long>(expertiseClasses.size());
    for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
      unusedExpertiseClasses.add(expertiseClass.getId());
    }
    
    List<Long> unusedInterestClasses = new ArrayList<Long>(interestClasses.size());
    for (PanelUserIntressClass interestClass : interestClasses) {
      unusedInterestClasses.add(interestClass.getId());
    }
    
    resolveUnusedClasses(expertisePages, expertiseClasses, interestClasses, unusedExpertiseClasses, unusedInterestClasses);

    jsonArr = new JSONArray();
    for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
      jsonObj = new JSONObject();
      
      jsonObj.put("id", expertiseClass.getId().toString());
      jsonObj.put("name", expertiseClass.getName());
      jsonObj.put("inUse", !unusedExpertiseClasses.contains(expertiseClass.getId()));
      
      jsonArr.add(jsonObj);
    }
    setJsDataVariable(pageRequestContext, "panelExpertiseClasses", jsonArr.toString());

    // PanelUserIntressClass
    
    jsonArr = new JSONArray();
    for (PanelUserIntressClass interestClass : interestClasses) {
      jsonObj = new JSONObject();
      
      jsonObj.put("id", interestClass.getId().toString());
      jsonObj.put("name", interestClass.getName());
      jsonObj.put("inUse", !unusedInterestClasses.contains(interestClass.getId()));
      
      jsonArr.add(jsonObj);
    }
    setJsDataVariable(pageRequestContext, "panelIntressClasses", jsonArr.toString());
   
    // Expertise Groups
    
    jsonArr = new JSONArray();
    List<PanelUserExpertiseGroup> panelUserExpertiseGroups = panelUserExpertiseGroupDAO.listByPanelAndStamp(panel, panel.getCurrentStamp());
    for (PanelUserExpertiseGroup expertiseGroup : panelUserExpertiseGroups) {
      jsonObj = new JSONObject();
      
      jsonObj.put("id", expertiseGroup.getId().toString());
      jsonObj.put("expertiseClassId", expertiseGroup.getExpertiseClass().getId().toString());
      jsonObj.put("intressClassId", expertiseGroup.getIntressClass().getId().toString());
      
      jsonArr.add(jsonObj);
    }
    setJsDataVariable(pageRequestContext, "panelExpertiseGroups", jsonArr.toString());
    
    // Expertise Group Users
    
    jsonObj = new JSONObject();
    for (PanelUserExpertiseGroup group : panelUserExpertiseGroups) {
      List<PanelExpertiseGroupUser> usersByGroup = panelExpertiseGroupUserDAO.listByGroupAndArchived(group, Boolean.FALSE);

      jsonArr = new JSONArray();
      for (PanelExpertiseGroupUser groupUser : usersByGroup) {
        JSONObject groupUserJsonObj = new JSONObject();
        
        groupUserJsonObj.put("id", groupUser.getId().toString());
        groupUserJsonObj.put("panelUserId", groupUser.getPanelUser().getId().toString());
        groupUserJsonObj.put("name", groupUser.getPanelUser().getUser().getFullName(true, false));
        groupUserJsonObj.put("email", groupUser.getPanelUser().getUser().getDefaultEmailAsObfuscatedString());
        
        jsonArr.add(groupUserJsonObj);
      }
      
      jsonObj.put(group.getId().toString(), jsonArr);
    }
    setJsDataVariable(pageRequestContext, "panelExpertiseGroupUsers", jsonObj.toString());
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/panelexperts.jsp");
  }

  private void resolveUnusedClasses(List<QueryPage> expertisePages, List<PanelUserExpertiseClass> expertiseClasses, List<PanelUserIntressClass> interestClasses, List<Long> unusedExpertiseClasses, List<Long> unusedInterestClasses) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();
    
    for (QueryPage expertisePage : expertisePages) {
      for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
        QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(expertisePage, getFieldName(expertiseClass));
        List<QueryQuestionMultiOptionAnswer> answers = queryQuestionMultiOptionAnswerDAO.listByQueryField(queryField);
        if (answers.size() > 0) {
          for (QueryQuestionMultiOptionAnswer answer : answers) {
            Set<QueryOptionFieldOption> options = answer.getOptions();
            if (options.size() > 0) {
              for (QueryOptionFieldOption option : options) {
                Long interestId = NumberUtils.createLong(option.getValue());
                unusedInterestClasses.remove(interestId);
              }

              unusedExpertiseClasses.remove(expertiseClass.getId());
            }
          }
        }
        
        if (unusedExpertiseClasses.size() == 0 && unusedInterestClasses.size() == 0)
          return;
      }
    }
  }

  private String getFieldName(PanelUserExpertiseClass expertiseClass) {
    return "expertise." + expertiseClass.getId();
  }
}