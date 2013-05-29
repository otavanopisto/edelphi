package fi.internetix.edelphi.pages;

import java.util.Locale;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.panels.PanelInvitationDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitation;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitationState;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.UserUtils;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class JoinPanelPageController extends PageController {

  @Override
  public void process(PageRequestContext pageRequestContext) {
    Long panelId = pageRequestContext.getLong("panelId");
    String hash = pageRequestContext.getString("hash");
    boolean accepted = new Integer(1).equals(pageRequestContext.getInteger("join"));
    
    PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();
    PanelInvitation panelInvitation = panelInvitationDAO.findByHash(hash);
    
    Messages messages = Messages.getInstance();
    Locale locale = pageRequestContext.getRequest().getLocale();
    if (panelInvitation == null || !panelInvitation.getPanel().getId().equals(panelId)) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_INVITATION, messages.getText(locale, "exception.1008.invalidInvitation"));
    }

    if (accepted) {

      AuthUtils.storeRedirectUrl(pageRequestContext, RequestUtils.getCurrentUrl(pageRequestContext.getRequest(), true));
      
      User user = RequestUtils.getUser(pageRequestContext);
      Panel panel = panelInvitation.getPanel();
      Query query = panelInvitation.getQuery();
      if (user == null) {
        pageRequestContext.getRequest().setAttribute("panel", panel);
        pageRequestContext.getRequest().setAttribute("query", query);
        pageRequestContext.getRequest().setAttribute("invitationUserMail", panelInvitation.getEmail());
        AuthUtils.includeAuthSources(pageRequestContext, "PANEL", panel.getId());
        pageRequestContext.setIncludeJSP("/jsp/pages/panel/joinpanel.jsp");
      }
      else {
        UserEmailDAO userEmailDAO = new UserEmailDAO();
        UserEmail userEmail = userEmailDAO.findByAddress(panelInvitation.getEmail());
        if (userEmail != null && user != null && user.getId().equals(userEmail.getUser().getId())) {
          PanelUserDAO panelUserDAO = new PanelUserDAO();
          PanelUser panelUser = panelUserDAO.findByPanelAndUserAndStamp(panelInvitation.getPanel(), user, panelInvitation.getPanel().getCurrentStamp());
          if (panelUser == null) {
            panelUser = panelUserDAO.create(panelInvitation.getPanel(), user, panelInvitation.getRole(), PanelUserJoinType.INVITED, panelInvitation.getPanel().getCurrentStamp(), user);
          }
          panelInvitationDAO.archive(panelInvitation);
          String redirectUrl = pageRequestContext.getRequest().getContextPath() + "/" + panel.getRootFolder().getUrlName();
          if (query != null) {
            redirectUrl += "/" + query.getUrlName();
          }
          AuthUtils.retrieveRedirectUrl(pageRequestContext);
          pageRequestContext.setRedirectURL(redirectUrl);
        }
        else if (userEmail != null && !user.getId().equals(userEmail.getUser().getId())) {
          pageRequestContext.getRequest().setAttribute("panel", panel);
          pageRequestContext.getRequest().setAttribute("dualAccount", Boolean.TRUE);
          pageRequestContext.getRequest().setAttribute("currentUserMail", user.getDefaultEmail() == null ? "undefined" : user.getDefaultEmail().getAddress());
          pageRequestContext.getRequest().setAttribute("invitationUserMail", panelInvitation.getEmail());
          AuthUtils.includeAuthSources(pageRequestContext, "PANEL", panel.getId());
          pageRequestContext.setIncludeJSP("/jsp/pages/panel/joinpanel.jsp");
        }
        else {
          // ask about linking email to current account
          pageRequestContext.getRequest().setAttribute("panel", panel);
          pageRequestContext.getRequest().setAttribute("confirmLinking", Boolean.TRUE);
          pageRequestContext.getRequest().setAttribute("currentUserMail", user.getDefaultEmail() == null ? "undefined" : user.getDefaultEmail().getAddress());
          pageRequestContext.getRequest().setAttribute("invitationUserMail", panelInvitation.getEmail());
          setJsDataVariable(pageRequestContext, "invitationUserMail", panelInvitation.getEmail());
          pageRequestContext.setIncludeJSP("/jsp/pages/panel/joinpanel.jsp");
        }
      }
    }
    else {
      panelInvitationDAO.updateState(panelInvitation, PanelInvitationState.DECLINED, null);
      UserEmailDAO userEmailDAO = new UserEmailDAO();
      UserEmail userEmail = userEmailDAO.findByAddress(panelInvitation.getEmail());
      if (userEmail != null) {
        PanelUserDAO panelUserDAO = new PanelUserDAO();
        PanelUser panelUser = panelUserDAO.findByPanelAndUserAndStamp(panelInvitation.getPanel(), userEmail.getUser(), panelInvitation.getPanel().getCurrentStamp());
        if (panelUser != null) {
          UserUtils.archivePanelUser(panelUser, panelUser.getUser());
        }
      }
      pageRequestContext.getRequest().setAttribute("statusCode", EdelfoiStatusCode.OK);
      pageRequestContext.addMessage(Severity.INFORMATION, messages.getText(locale, "information.invitationDeclined"));
      pageRequestContext.setIncludeJSP("/jsp/pages/error.jsp");
    }
  }

}
