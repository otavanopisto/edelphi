package fi.internetix.edelphi.jsons.queries;

import java.util.Locale;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.base.EmailMessageDAO;
import fi.internetix.edelphi.dao.base.MailQueueItemDAO;
import fi.internetix.edelphi.dao.querydata.QueryQuestionCommentDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.dao.users.UserSettingDAO;
import fi.internetix.edelphi.domainmodel.base.EmailMessage;
import fi.internetix.edelphi.domainmodel.base.MailQueueItemState;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.querydata.QueryQuestionComment;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserSetting;
import fi.internetix.edelphi.domainmodel.users.UserSettingKey;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.QueryDataUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.SystemUtils;
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
    
    // Comment reply e-mail support
    
    if (SystemUtils.isProductionEnvironment()) {
      if (parentComment != null && !parentComment.getCreator().getId().equals(loggedUser.getId())) {
        User user = parentComment.getCreator();
        UserSettingDAO userSettingDAO = new UserSettingDAO();
        UserSetting userSetting = userSettingDAO.findByUserAndKey(user, UserSettingKey.MAIL_COMMENT_REPLY);
        if (userSetting != null && "1".equals(userSetting.getValue())) {
          
          // URL to the newly added comment
          
          Panel panel = RequestUtils.getPanel(jsonRequestContext);
          StringBuilder commentUrl = new StringBuilder();
          commentUrl.append(RequestUtils.getBaseUrl(jsonRequestContext.getRequest()));
          commentUrl.append('/');
          commentUrl.append(panel.getUrlName());
          commentUrl.append('/');
          commentUrl.append(query.getUrlName());
          commentUrl.append("?page=");
          commentUrl.append(queryPage.getPageNumber());
          commentUrl.append("&comment=");
          commentUrl.append(questionComment.getId());
          
          // Comment mail
          
          Messages messages = Messages.getInstance();
          Locale locale = jsonRequestContext.getRequest().getLocale();
          String subject = messages.getText(locale, "mail.newReply.template.subject");
          String content = messages.getText(locale, "mail.newReply.template.content", new Object[] {panel.getName(), query.getName(), commentUrl.toString()});
          EmailMessageDAO emailMessageDAO = new EmailMessageDAO();
          // TODO system e-mail address could probably be fetched from somewhere?
          EmailMessage emailMessage = emailMessageDAO.create("edelfoi-system@otavanopisto.fi", user.getDefaultEmailAsString(), subject, content, loggedUser);
          MailQueueItemDAO mailQueueItemDAO = new MailQueueItemDAO();
          mailQueueItemDAO.create(MailQueueItemState.IN_QUEUE, emailMessage, loggedUser);
        }
      }
    }
    
    // JSON response parameters
    
    jsonRequestContext.addResponseParameter("commentId", questionComment.getId());
    jsonRequestContext.addResponseParameter("queryPageId", questionComment.getQueryPage().getId());
  }
}
