package fi.internetix.edelphi.jsons.panel.admin;

import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.querydata.QueryQuestionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionMultiOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionNumericAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionOptionGroupOptionAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionTextAnswerDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.querylayout.QuerySectionDAO;
import fi.internetix.edelphi.dao.querymeta.QueryFieldDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionMultiOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionNumericAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionOptionGroupOptionAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionTextAnswer;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.querylayout.QuerySection;
import fi.internetix.edelphi.domainmodel.querymeta.QueryField;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.internetix.smvc.logging.Logging;

public class RemoveAnswersJSONRequestController extends JSONController {

  public RemoveAnswersJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.CLEAR_QUERY_DATA, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());

    if (jsonRequestContext.getLong("queryId") != null) {
      QueryDAO queryDAO = new QueryDAO();
      Query query = queryDAO.findById(jsonRequestContext.getLong("queryId"));
      if (query == null) {
        throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
      }
      Logging.logInfo("RemoveAnswersQuery - UserId: " + loggedUser.getId() + " (" + loggedUser.getFullName() + ") QueryId: " + query.getId() + " (" + query.getName() + ")");
      QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
      Panel panel = ResourceUtils.getResourcePanel(query);
      List<QueryReply> replies = queryReplyDAO.listByQueryAndStamp(query, panel.getCurrentStamp());
      for (QueryReply reply : replies) {
        queryReplyDAO.archive(reply, loggedUser);
      }
      QueryPageDAO queryPageDAO = new QueryPageDAO();
      List<QueryPage> queryPages = queryPageDAO.listByQuery(query);
      QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
      for (QueryPage queryPage : queryPages) {
        List<QueryQuestionComment> comments = queryQuestionCommentDAO.listByQueryPageAndStamp(queryPage, panel.getCurrentStamp());
        for (QueryQuestionComment comment : comments) {
          queryQuestionCommentDAO.archive(comment);
        }
      }
    }
    else if (jsonRequestContext.getLong("queryPageId") != null) {
      QueryPageDAO queryPageDAO = new QueryPageDAO();
      QueryPage queryPage = queryPageDAO.findById(jsonRequestContext.getLong("queryPageId"));
      if (queryPage == null) {
        throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
      }
      Logging.logInfo("RemoveAnswersQueryPage - UserId: " + loggedUser.getId() + " (" + loggedUser.getFullName() + ") QueryPageId: " + queryPage.getId() + " (" + queryPage.getTitle() + ")");
      Panel panel = ResourceUtils.getResourcePanel(queryPage.getQuerySection().getQuery());
      removeAnswers(queryPage, panel.getCurrentStamp());
    }
    else if (jsonRequestContext.getLong("querySectionId") != null) {
      QuerySectionDAO querySectionDAO = new QuerySectionDAO();
      QuerySection querySection = querySectionDAO.findById(jsonRequestContext.getLong("querySectionId"));
      if (querySection == null) {
        throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
      }
      Logging.logInfo("RemoveAnswersQuerySection - UserId: " + loggedUser.getId() + " (" + loggedUser.getFullName() + ") QuerySectionId: " + querySection.getId() + " (" + querySection.getTitle() + ")");
      Panel panel = ResourceUtils.getResourcePanel(querySection.getQuery());
      removeAnswers(querySection, panel.getCurrentStamp());
    }
    else if (jsonRequestContext.getLong("queryReplyId") != null) {
      QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
      QueryReply queryReply = queryReplyDAO.findById(jsonRequestContext.getLong("queryReplyId"));
      if (queryReply == null) {
        throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
      }
      Logging.logInfo("RemoveAnswersQueryReply - UserId: " + loggedUser.getId() + " (" + loggedUser.getFullName() + ") QueryReplyId: " + queryReply.getId());
      queryReplyDAO.archive(queryReply, loggedUser);
    }
  }
  
  private void removeAnswers(QuerySection querySection, PanelStamp stamp) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    List<QueryPage> queryPages = queryPageDAO.listByQuerySection(querySection);
    for (QueryPage queryPage : queryPages) {
      removeAnswers(queryPage, stamp);
    }
  }

  private void removeAnswers(QueryPage queryPage, PanelStamp stamp) {
    QueryFieldDAO queryFieldDAO = new QueryFieldDAO();
    QueryQuestionAnswerDAO queryQuestionAnswerDAO = new QueryQuestionAnswerDAO();
    List<QueryField> queryFields = queryFieldDAO.listByQueryPage(queryPage);
    for (QueryField queryField : queryFields) {
      switch (queryField.getType()) {
        case TEXT:
          QueryQuestionTextAnswerDAO queryQuestionTextAnswerDAO = new QueryQuestionTextAnswerDAO();
          List<QueryQuestionTextAnswer> textAnswers = queryQuestionTextAnswerDAO.listByQueryField(queryField);
          for (QueryQuestionTextAnswer textAnswer : textAnswers) {
            queryQuestionTextAnswerDAO.delete(textAnswer);
            queryQuestionAnswerDAO.delete(textAnswer);
          }
          break;
        case NUMERIC:
        case NUMERIC_SCALE:
          QueryQuestionNumericAnswerDAO queryQuestionNumericAnswerDAO = new QueryQuestionNumericAnswerDAO();
          List<QueryQuestionNumericAnswer> numericAnswers = queryQuestionNumericAnswerDAO.listByQueryField(queryField);
          for (QueryQuestionNumericAnswer numericAnswer : numericAnswers) {
            queryQuestionNumericAnswerDAO.delete(numericAnswer);
            queryQuestionAnswerDAO.delete(numericAnswer);
          }
          break;
        case OPTIONFIELD:
          QueryQuestionOptionAnswerDAO queryQuestionOptionAnswerDAO = new QueryQuestionOptionAnswerDAO();
          QueryQuestionMultiOptionAnswerDAO queryQuestionMultiOptionAnswerDAO = new QueryQuestionMultiOptionAnswerDAO();
          QueryQuestionOptionGroupOptionAnswerDAO queryQuestionOptionGroupOptionAnswerDAO = new QueryQuestionOptionGroupOptionAnswerDAO();
          List<QueryQuestionOptionGroupOptionAnswer> optionGroupAnswers = queryQuestionOptionGroupOptionAnswerDAO.listByQueryField(queryField);
          for (QueryQuestionOptionGroupOptionAnswer optionGroupAnswer : optionGroupAnswers) {
            queryQuestionOptionGroupOptionAnswerDAO.delete(optionGroupAnswer);
          }
          List<QueryQuestionMultiOptionAnswer> multiAnswers = queryQuestionMultiOptionAnswerDAO.listByQueryField(queryField);
          for (QueryQuestionMultiOptionAnswer multiAnswer : multiAnswers) {
            queryQuestionMultiOptionAnswerDAO.delete(multiAnswer);
            queryQuestionAnswerDAO.delete(multiAnswer);
          }
          List<QueryQuestionOptionAnswer> optionAnswers = queryQuestionOptionAnswerDAO.listByQueryField(queryField);
          for (QueryQuestionOptionAnswer optionAnswer : optionAnswers) {
            queryQuestionOptionAnswerDAO.delete(optionAnswer);
            queryQuestionAnswerDAO.delete(optionAnswer);
          }
          break;
      }
    }
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    List<QueryQuestionComment> comments = queryQuestionCommentDAO.listByQueryPageAndStamp(queryPage, stamp);
    for (QueryQuestionComment comment : comments) {
      queryQuestionCommentDAO.archive(comment);
    }
  }

}
