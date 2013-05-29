package fi.internetix.edelphi.jsons.panel.admin;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.panels.PanelExpertiseGroupUserDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseClassDAO;
import fi.internetix.edelphi.dao.panels.PanelUserExpertiseGroupDAO;
import fi.internetix.edelphi.dao.panels.PanelUserIntressClassDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelExpertiseGroupUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseClass;
import fi.internetix.edelphi.domainmodel.panels.PanelUserExpertiseGroup;
import fi.internetix.edelphi.domainmodel.panels.PanelUserIntressClass;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPageType;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionField;
import fi.internetix.edelphi.domainmodel.querymeta.QueryOptionFieldOption;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.query.QueryPageHandlerFactory;
import fi.internetix.edelphi.query.expertise.ExpertiseQueryPageHandler;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class DeletePanelInterestClassJSONRequestController extends JSONController {

  public DeletePanelInterestClassJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long interestClassId = jsonRequestContext.getLong("interestClassId");
    PanelUserIntressClassDAO interestClassDAO = new PanelUserIntressClassDAO();
    PanelUserExpertiseGroupDAO expertiseGroupDAO = new PanelUserExpertiseGroupDAO();
    PanelExpertiseGroupUserDAO groupUserDAO = new PanelExpertiseGroupUserDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    
    PanelUserIntressClass interestClass = interestClassDAO.findById(interestClassId);
    
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    if (hasAnswers(interestClass)) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INTEREST_CONTAINS_ANSWERS, messages.getText(locale, "exception.1025.interestContainsAnswers"));
    }
    
    List<PanelUserExpertiseGroup> groups = expertiseGroupDAO.listByInterestAndStamp(interestClass, panel.getCurrentStamp());
    
    for (PanelUserExpertiseGroup group : groups) {
      List<PanelExpertiseGroupUser> users = groupUserDAO.listByGroupAndArchived(group, Boolean.FALSE);
      
      for (PanelExpertiseGroupUser user : users) {
        groupUserDAO.delete(user);
      }
      
      expertiseGroupDAO.delete(group);
    }
    
    interestClassDAO.delete(interestClass);

    List<QueryPage> expertisePages = queryPageDAO.listByQueryParentFolderAndPageType(interestClass.getPanel().getRootFolder(), QueryPageType.EXPERTISE);
    for (QueryPage expertisePage : expertisePages) {
      ExpertiseQueryPageHandler pageHandler = (ExpertiseQueryPageHandler) QueryPageHandlerFactory.getInstance().buildPageHandler(QueryPageType.EXPERTISE);
      pageHandler.synchronizedFields(expertisePage);
    }

  }
  
  private boolean hasAnswers(PanelUserIntressClass interestClass) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    PanelUserExpertiseClassDAO panelUserExpertiseClassDAO = new PanelUserExpertiseClassDAO();
    QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();
    
    List<PanelUserExpertiseClass> expertiseClasses = panelUserExpertiseClassDAO.listByPanel(interestClass.getPanel());
    List<QueryPage> expertisePages = queryPageDAO.listByQueryParentFolderAndPageType(interestClass.getPanel().getRootFolder(), QueryPageType.EXPERTISE);
    for (QueryPage expertisePage : expertisePages) {
      for (PanelUserExpertiseClass expertiseClass : expertiseClasses) {
        QueryOptionField queryField = (QueryOptionField) queryFieldDAO.findByQueryPageAndName(expertisePage, getFieldName(expertiseClass));
        List<QueryQuestionMultiOptionAnswer> answers = queryQuestionMultiOptionAnswerDAO.listByQueryField(queryField);
        for (QueryQuestionMultiOptionAnswer answer : answers) {
          for (QueryOptionFieldOption option : answer.getOptions()) {
            Long optionIterestId = NumberUtils.createLong(option.getValue());
            if (interestClass.getId().equals(optionIterestId))
              return true;
          }
        }
      }
    }
    
    return false;
  }
  
  private String getFieldName(PanelUserExpertiseClass expertiseClass) {
    return "expertise." + expertiseClass.getId();
  }
}
