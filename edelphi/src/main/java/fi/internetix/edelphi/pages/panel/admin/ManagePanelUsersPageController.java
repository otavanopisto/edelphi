package fi.internetix.edelphi.pages.panel.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserRoleDAO;
import fi.internetix.edelphi.dao.users.UserIdentificationDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserRole;
import fi.internetix.edelphi.domainmodel.users.UserIdentification;
import fi.internetix.edelphi.pages.panel.PanelPageController;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.LocalizationUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ManagePanelUsersPageController extends PanelPageController {
  
  public ManagePanelUsersPageController() {
    setAccessAction(DelfoiActionName.MANAGE_PANEL_USERS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    Panel panel = RequestUtils.getPanel(pageRequestContext);
    pageRequestContext.getRequest().setAttribute("panel", panel);
    
    PanelUserRoleDAO panelUserRoleDAO = new PanelUserRoleDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    UserIdentificationDAO userIdentificationDAO = new UserIdentificationDAO();
    
    List<PanelUser> panelUsers = panelUserDAO.listByPanelAndStamp(panel, RequestUtils.getActiveStamp(pageRequestContext));
    Collections.sort(panelUsers, new Comparator<PanelUser>() {
      public int compare(PanelUser o1, PanelUser o2) {
        return StringUtils.trimToEmpty(o1.getUser().getFullName()).compareTo(StringUtils.trimToEmpty(o2.getUser().getFullName()));
      }
    });
    
    setJsDataVariable(pageRequestContext, "loggedUserId", pageRequestContext.getLoggedUserId().toString());
    
    List<PanelUserRole> panelUserRoles = panelUserRoleDAO.listAll();
    List<UserRoleBean> userRoleBeans = new ArrayList<UserRoleBean>();
    for (PanelUserRole panelUserRole : panelUserRoles) {
      String name = LocalizationUtils.getLocalizedText(panelUserRole.getName(), pageRequestContext.getRequest().getLocale());
      userRoleBeans.add(new UserRoleBean(panelUserRole.getId(), name));
    }
    pageRequestContext.getRequest().setAttribute("panelUserRoles", userRoleBeans);
    
    JSONArray panelUsersArr = new JSONArray();
    
    for (PanelUser pUser : panelUsers) {
      JSONObject panelUserObj = new JSONObject();
      
      panelUserObj.put("id", pUser.getId().toString());
      panelUserObj.put("firstName", pUser.getUser().getFirstName());
      panelUserObj.put("lastName", pUser.getUser().getLastName());
      if (pUser.getUser().getDefaultEmail() != null)
        panelUserObj.put("email", pUser.getUser().getDefaultEmail().getObfuscatedAddress());
      if (pUser.getRole() != null)
        panelUserObj.put("roleId", pUser.getRole().getId().toString());
      panelUserObj.put("created", String.valueOf(pUser.getCreated().getTime()));      
      panelUserObj.put("userId", pUser.getUser().getId().toString());
      
      // Panel User Expertises

      List<PanelExpertiseGroupUser> pUserExpertises = panelExpertiseGroupUserDAO.listByUser(pUser);
      JSONArray pUserExpertisesArr = new JSONArray();
      
      for (PanelExpertiseGroupUser pUserExpertise : pUserExpertises) {
        JSONObject expObj = new JSONObject();
        
        expObj.put("groupUserId", pUserExpertise.getId().toString());
        expObj.put("groupId", pUserExpertise.getExpertiseGroup().getId().toString());
        expObj.put("intressId", pUserExpertise.getExpertiseGroup().getIntressClass().getId().toString());
        expObj.put("expertiseId", pUserExpertise.getExpertiseGroup().getExpertiseClass().getId().toString());
        expObj.put("intressName", pUserExpertise.getExpertiseGroup().getIntressClass().getName());
        expObj.put("expertiseName", pUserExpertise.getExpertiseGroup().getExpertiseClass().getName());
        
        pUserExpertisesArr.add(expObj);
      }
      panelUserObj.put("expertises", pUserExpertisesArr.toString());
      
      // Panel User Auth strategies
      JSONArray pUserAuthsArr = new JSONArray();
      List<UserIdentification> authStrategies = userIdentificationDAO.listByUser(pUser.getUser());
      for (UserIdentification authStrategy : authStrategies) {
        JSONObject authObj = new JSONObject();
        
        authObj.put("authId", authStrategy.getId().toString());
        authObj.put("authName", authStrategy.getAuthSource().getName());
        
        pUserAuthsArr.add(authObj);
      }
      panelUserObj.put("auths", pUserAuthsArr.toString());
      
      panelUsersArr.add(panelUserObj);
    }
    
    setJsDataVariable(pageRequestContext, "panelUsers", panelUsersArr.toString());

    JSONArray jsonArr;
    JSONObject jsonObj;
    
    // PanelUserExpertiseClass

    List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(panel);
    Collections.sort(expertiseClasses, new Comparator<PanelUserExpertiseClass>() {
      @Override
      public int compare(PanelUserExpertiseClass o1, PanelUserExpertiseClass o2) {
        return o1.getId().compareTo(o2.getId());
      }
    });

    jsonArr = new JSONArray();
    for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
      jsonObj = new JSONObject();
      
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
      jsonObj = new JSONObject();
      
      jsonObj.put("id", intressClass.getId().toString());
      jsonObj.put("name", intressClass.getName());
      
      jsonArr.add(jsonObj);
    }
    setJsDataVariable(pageRequestContext, "panelIntressClasses", jsonArr.toString());

    // Expertise Groups
    
    jsonArr = new JSONArray();
    List<PanelUserExpertiseGroup> panelUserExpertiseGroups = panelUserExpertiseGroupDAO.listByPanelAndStamp(panel, RequestUtils.getActiveStamp(pageRequestContext));
    for (PanelUserExpertiseGroup expertiseGroup : panelUserExpertiseGroups) {
      jsonObj = new JSONObject();
      
      jsonObj.put("id", expertiseGroup.getId().toString());
      jsonObj.put("expertiseClassId", expertiseGroup.getExpertiseClass().getId().toString());
      jsonObj.put("intressClassId", expertiseGroup.getIntressClass().getId().toString());
      
      jsonArr.add(jsonObj);
    }
    setJsDataVariable(pageRequestContext, "panelExpertiseGroups", jsonArr.toString());
    
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
    
    ActionUtils.includeRoleAccessList(pageRequestContext);
    
    pageRequestContext.setIncludeJSP("/jsp/pages/panel/admin/managepanelusers.jsp");
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