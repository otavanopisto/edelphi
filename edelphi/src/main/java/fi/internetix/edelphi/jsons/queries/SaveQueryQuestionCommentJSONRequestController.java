package fi.internetix.edelphi.jsons.queries;

import java.util.Locale;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.QueryDataUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class SaveQueryQuestionCommentJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    
    Long queryPageId = jsonRequestContext.getLong("queryPageId");
    Long parentCommentId = jsonRequestContext.getLong("parentCommentId");
    String comment = jsonRequestContext.getString("comment");
    
    QueryPage queryPage = queryPageDAO.findById(queryPageId);
    Query query = queryPage.getQuerySection().getQuery();
    
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    
    QueryReply queryReply = QueryDataUtils.findQueryReply(jsonRequestContext, loggedUser, query);
    if (queryReply == null) {
      Messages messages = Messages.getInstance();
      Locale locale = jsonRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNKNOWN_REPLICANT, messages.getText(locale, "exception.1026.unknownReplicant"));
    }

    QueryQuestionComment parentComment = null;
    if (parentCommentId != null)
      parentComment = queryQuestionCommentDAO.findById(parentCommentId);

    QueryQuestionComment questionComment = queryQuestionCommentDAO.create(queryReply, queryPage, parentComment, comment, false, loggedUser);
    
    QueryDataUtils.storeQueryReplyId(jsonRequestContext.getRequest().getSession(), queryReply);
    
    jsonRequestContext.addResponseParameter("commentId", questionComment.getId());
    jsonRequestContext.addResponseParameter("queryPageId", questionComment.getQueryPage().getId());
  }
}
