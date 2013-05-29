package fi.internetix.edelphi.jsons.queries;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class HideQueryQuestionCommentJSONRequestController extends JSONController {

  public HideQueryQuestionCommentJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_QUERY_COMMENTS, DelfoiActionScope.PANEL);
  }
  
  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    QueryQuestionCommentDAO queryQuestionCommentDAO = new QueryQuestionCommentDAO();
    Long commentId = jsonRequestContext.getLong("commentId");
    
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    
    QueryQuestionComment comment = queryQuestionCommentDAO.findById(commentId);
    queryQuestionCommentDAO.updateHidden(comment, Boolean.TRUE, loggedUser);
    
    jsonRequestContext.addResponseParameter("commentId", comment.getId());
  }

}