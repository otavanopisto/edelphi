package fi.internetix.edelphi.jsons.queries;

import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.resources.QueryState;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.query.QueryPageHandler;
import fi.internetix.edelphi.query.QueryPageHandlerFactory;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.QueryDataUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class SaveQueryAnswersJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    
    Long queryPageId = jsonRequestContext.getLong("queryPageId");
    
    QueryPage queryPage = queryPageDAO.findById(queryPageId);
    Query query = queryPage.getQuerySection().getQuery();
  
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    if (query.getState() == QueryState.CLOSED)
      throw new SmvcRuntimeException(EdelfoiStatusCode.CANNOT_SAVE_REPLY_QUERY_CLOSED, messages.getText(locale, "exception.1027.cannotSaveReplyQueryClosed"));
    
    if (query.getState() == QueryState.EDIT) {
      if (!ActionUtils.hasPanelAccess(jsonRequestContext, DelfoiActionName.MANAGE_DELFOI_MATERIALS.toString()))
        throw new SmvcRuntimeException(EdelfoiStatusCode.CANNOT_SAVE_REPLY_QUERY_IN_EDIT_STATE, messages.getText(locale, "exception.1028.cannotSaveReplyQueryInEditState"));
    }
    else {
      User loggedUser = null;
      if (jsonRequestContext.isLoggedIn())
        loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
      
      QueryReply queryReply = QueryDataUtils.findQueryReply(jsonRequestContext, loggedUser, query);
      if (queryReply == null) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.UNKNOWN_REPLICANT, messages.getText(locale, "exception.1026.unknownReplicant"));
      }
      
      QueryDataUtils.storeQueryReplyId(jsonRequestContext.getRequest().getSession(), queryReply);
      
      QueryPageHandler queryPageHandler = QueryPageHandlerFactory.getInstance().buildPageHandler(queryPage.getPageType());
      queryPageHandler.saveAnswers(jsonRequestContext, queryPage, queryReply);
    }
  }
}
