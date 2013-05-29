package fi.internetix.edelphi.jsons.queries;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class FinishQueryJSONRequestController extends JSONController {
  
  public FinishQueryJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Long replyId = jsonRequestContext.getLong("replyId");
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    if (loggedUser != null && replyId != null) {
      QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
      QueryReply queryReply = queryReplyDAO.findById(replyId);
      if (queryReply != null) {
        queryReplyDAO.updateComplete(queryReply, loggedUser, Boolean.TRUE);
      }
    }
  }
}
