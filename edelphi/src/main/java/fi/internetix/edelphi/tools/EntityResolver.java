package fi.internetix.edelphi.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.querymeta.QueryOptionFieldOptionDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.utils.ResourceUtils;

public class EntityResolver {
  
  public EntityResolver(Long queryId) {
    QueryDAO queryDAO = new QueryDAO();
    targetQuery = queryDAO.findById(queryId);
    if (targetQuery == null) {
      throw new IllegalArgumentException("Unknown target query: " + queryId);
    }
  }
  
  public void mapCommentParameter(Integer pageNumber, String commentParameter) {
    
    // page number = set of old comment field names
    
    List<String> commentParameters = commentParameterMap.get(pageNumber);
    if (commentParameters == null) {
      commentParameters = new ArrayList<String>();
      commentParameterMap.put(pageNumber, commentParameters);
    }
    commentParameters.add(commentParameter);
  }
  
  // param1 = 0.scale2d.x
  // param2 = 0.scale2d.y
  // param3 = 1.scale1d
  // etc.
  public void mapField(String oldFieldName, Integer pageNumber, String newFieldName) {
    
    // page number = set of old field names 
    
    Set<String> parametersOnPage = parameterPageMap.get(pageNumber);
    if (parametersOnPage == null) {
      parametersOnPage = new HashSet<String>();
      parameterPageMap.put(pageNumber, parametersOnPage);
    }
    parametersOnPage.add(oldFieldName);

    // old field name = corresponding QueryField 
    
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryPage queryPage = queryPageDAO.findByQueryAndPageNumber(targetQuery, pageNumber);
    if (queryPage == null) {
      throw new IllegalArgumentException("Unknown QueryPage by page number: " + pageNumber);
    }
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryField queryField = queryFieldDAO.findByQueryPageAndName(queryPage, newFieldName);
    if (queryField == null) {
      throw new IllegalArgumentException("Incorrect QueryField mapping: " + oldFieldName + "=" + pageNumber + "." + newFieldName);
    }
    if (queryFieldMap.containsKey(oldFieldName)) {
      throw new IllegalArgumentException("Already mapped QueryField: " + oldFieldName);
    }
    queryFieldMap.put(oldFieldName, queryField);
  }
  
  // param1.-3 = 0
  // param1.-2 = 1
  // param1.-1 = 2
  // etc.
  
  public void mapOptionFieldOption(String oldFieldName, String oldValue, String newValue) {
    Map<String, String> optionMap = optionFieldOptionMap.get(oldFieldName);
    if (optionMap == null) {
      optionMap = new HashMap<String, String>();
      optionFieldOptionMap.put(oldFieldName,  optionMap);
    }
    optionMap.put(oldValue, newValue);
  }
  
  public void mapExpertiseGroup(Integer cellNumber, Long intressClassId, Long expertiseClassId) {
    PanelUserIntressClassDAO panelUserIntressClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    
    PanelUserIntressClass intressClass = panelUserIntressClassDAO.findById(intressClassId);
    if (intressClass == null) {
      throw new IllegalArgumentException("Unknown interest class: " + intressClassId);
    }
    PanelUserExpertiseClass expertiseClass = panelUserExpertiseClassDAO.findById(expertiseClassId);
    if (expertiseClass == null) {
      throw new IllegalArgumentException("Unknown expertise class: " + expertiseClassId);
    }
    
    Panel panel = ResourceUtils.getResourcePanel(targetQuery);
    PanelUserExpertiseGroupDAO panelUserExpertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelUserExpertiseGroup expertiseGroup = panelUserExpertiseGroupDAO.findByInterestAndExpertiseAndStamp(intressClass, expertiseClass, panel.getCurrentStamp());
    if (expertiseGroup == null) {
      throw new IllegalArgumentException("Unknown expertise group for interest: " + intressClassId + " expertise: " + expertiseClassId);
    }
    expertiseGroupMap.put(cellNumber,  expertiseGroup);
  }
  
  // --------------------------------------------------------------------------------------------------------------------
  
  public Set<String> getFieldParametersOnPage(Integer pageNumber) {
    Set<String> fieldParameters = parameterPageMap.get(pageNumber);
    if (fieldParameters == null) {
      fieldParameters = new HashSet<String>();
    }
    return fieldParameters;
  }

  public List<String> getCommentParametersOnPage(Integer pageNumber) {
    List<String> commentParameters = commentParameterMap.get(pageNumber);
    if (commentParameters == null) {
      commentParameters = new ArrayList<String>();
    }
    return commentParameters;
  }

  public QueryField getQueryFieldByName(String oldFieldName) {
    QueryField queryField = queryFieldMap.get(oldFieldName);
    if (queryField == null) {
      throw new IllegalArgumentException("Unmapped QueryField: " + oldFieldName);
    }
    return queryField;
  }
  
  public QueryOptionFieldOption getQueryOption(String oldFieldName, String oldValue) {
    
    // Convert old field name to QueryField
    
    QueryOptionField queryOptionField = null;
    QueryField queryField = getQueryFieldByName(oldFieldName);
    if (queryField instanceof QueryOptionField) {
      queryOptionField = (QueryOptionField) queryField;
    }
    else {
      throw new IllegalArgumentException("Incorrectly mapped QueryOptionField: " + oldFieldName);
    }
    
    // Convert old option value to new option value
    
    Map<String, String> optionMap = optionFieldOptionMap.get(oldFieldName);
    if (optionMap == null) {
      throw new IllegalArgumentException("Unmapped QueryOptionField: " + oldFieldName);
    }
    String newValue = optionMap.get(oldValue);
    if (newValue == null) {
      throw new IllegalArgumentException("Unmapped QueryOptionFieldOption: " + oldFieldName + "[" + oldValue + "]");
    }
    
    // Convert new value to QueryOptionFieldOption
    
    QueryOptionFieldOptionDAO queryOptionFieldOptionDAO = new QueryOptionFieldOptionDAO();
    List<QueryOptionFieldOption> options = queryOptionFieldOptionDAO.listByQueryField(queryOptionField);
    QueryOptionFieldOption queryOption = null;
    for (QueryOptionFieldOption option : options) {
      if (newValue.equals(option.getValue())) {
        queryOption = option;
        break;
      }
    }
    if (queryOption == null) {
      throw new IllegalArgumentException("Unmapped QueryOptionFieldOption: " + oldFieldName + "[" + oldValue + "=" + newValue + "]");
    }
    return queryOption;
  }
  
  // --------------------------------------------------------------------------------------------------------------------
  
  public PanelUser resolvePanelUser(Panel panel, User user) {
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    PanelUser panelUser = panelUserDAO.findByPanelAndUserAndStamp(panel, user, panel.getCurrentStamp());
    if (panelUser == null) {
      panelUser = panelUserDAO.create(panel, user, panel.getDefaultPanelUserRole(), PanelUserJoinType.ADDED, panel.getCurrentStamp(), user);
    }
    return panelUser;
  }
  
  public QueryReply resolveQueryReply(User user, Date creationDate, Date modifiedDate) {
    QueryReply queryReply = replyMap.get(user);
    if (queryReply == null) {
      QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
      Panel panel = ResourceUtils.getResourcePanel(targetQuery);
      queryReply = queryReplyDAO.findByUserAndQueryAndStamp(user, targetQuery, panel.getCurrentStamp());
      if (queryReply == null) {
        queryReply = queryReplyDAO.create(user, targetQuery, panel.getCurrentStamp(), Boolean.FALSE, user, creationDate, user, modifiedDate);
      }
      replyMap.put(user, queryReply);
    }
    return queryReply;
  }

  public User resolveUser(String email) {
    User user = userMap.get(email);
    if (user == null) {
      UserEmailDAO userEmailDAO = new UserEmailDAO();
      UserEmail userEmail = userEmailDAO.findByAddress(email);
      if (userEmail == null) {
        UserDAO userDAO = new UserDAO();
        user = userDAO.create(null, null, null, null);
        userEmail = userEmailDAO.create(user, email);
        userDAO.addUserEmail(user, userEmail, true, user);
      }
      else {
        user = userEmail.getUser();
      }
      userMap.put(email, user);
    }
    return user;
  }
  
  public PanelExpertiseGroupUser resolveExpertiseGroupUser(PanelUser panelUser, Integer cellNumber) {
    PanelExpertiseGroupUserDAO panelExpertiseGroupUserDAO = new PanelExpertiseGroupUserDAO();
    PanelUserExpertiseGroup expertiseGroup = expertiseGroupMap.get(cellNumber);
    PanelExpertiseGroupUser groupUser = panelExpertiseGroupUserDAO.findByGroupAndPanelUser(expertiseGroup, panelUser);
    if (groupUser == null) {
      groupUser = panelExpertiseGroupUserDAO.create(expertiseGroup, panelUser, null);
    }
    return groupUser;
  }
  
  private Query targetQuery;
  private Map<User, QueryReply> replyMap = new HashMap<User, QueryReply>();
  private Map<String, User> userMap = new HashMap<String, User>();
  private Map<String, QueryField> queryFieldMap = new HashMap<String, QueryField>();
  private Map<Integer, Set<String>> parameterPageMap = new HashMap<Integer, Set<String>>();
  private Map<String, Map<String, String>> optionFieldOptionMap = new HashMap<String, Map<String, String>>();
  private Map<Integer, List<String>> commentParameterMap = new HashMap<Integer, List<String>>();
  private Map<Integer, PanelUserExpertiseGroup> expertiseGroupMap = new HashMap<Integer, PanelUserExpertiseGroup>();
  
}
