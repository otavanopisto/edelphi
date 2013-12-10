package fi.internetix.edelphi.pages;

import java.util.Locale;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.panels.PanelInvitationDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.users.DelfoiUserDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiDefaults;
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

    UserDAO userDAO = new UserDAO();
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();

    if (accepted) {
      
      // Invitation was accepted
      
      User user = RequestUtils.getUser(pageRequestContext);
      UserEmail userEmail = userEmailDAO.findByAddress(panelInvitation.getEmail());
      Panel panel = panelInvitation.getPanel();
      Query query = panelInvitation.getQuery();
      
      if (user != null && userEmail != null && !user.getId().equals(userEmail.getUser().getId())) {

        // Exception; someone is already logged in but the invitation email resolves to another account
        // -> ask the user to log out and try again

        AuthUtils.storeRedirectUrl(pageRequestContext, RequestUtils.getCurrentUrl(pageRequestContext.getRequest(), true));
        pageRequestContext.getRequest().setAttribute("panel", panel);
        pageRequestContext.getRequest().setAttribute("dualAccount", Boolean.TRUE);
        pageRequestContext.getRequest().setAttribute("currentUserMail", user.getDefaultEmail() == null ? "undefined" : user.getDefaultEmail().getAddress());
        pageRequestContext.getRequest().setAttribute("invitationUserMail", panelInvitation.getEmail());
        AuthUtils.includeAuthSources(pageRequestContext, "PANEL", panel.getId());
        pageRequestContext.setIncludeJSP("/jsp/pages/panel/joinpanel.jsp");
      }
      else if (user != null && userEmail == null) {

        // Exception; someone is already logged in but the invitation email resolves to no account
        // -> ask whether to create a new account or link invitation email to current account 

        AuthUtils.storeRedirectUrl(pageRequestContext, RequestUtils.getCurrentUrl(pageRequestContext.getRequest(), true));
        pageRequestContext.getRequest().setAttribute("panel", panel);
        pageRequestContext.getRequest().setAttribute("confirmLinking", Boolean.TRUE);
        pageRequestContext.getRequest().setAttribute("currentUserMail", user.getDefaultEmail() == null ? "undefined" : user.getDefaultEmail().getAddress());
        pageRequestContext.getRequest().setAttribute("invitationUserMail", panelInvitation.getEmail());
        setJsDataVariable(pageRequestContext, "invitationUserMail", panelInvitation.getEmail());
        pageRequestContext.setIncludeJSP("/jsp/pages/panel/joinpanel.jsp");
      }
      else {
        if (user == null && userEmail == null) {

          // No one is logged in and the invitation email is available
          // -> automatically create a new account
          
          user = userDAO.create(null, null, null,  null);
          userEmail = userEmailDAO.create(user, panelInvitation.getEmail());
          userDAO.addUserEmail(user, userEmail, true, user);
          Delfoi delfoi = RequestUtils.getDelfoi(pageRequestContext);
          DelfoiDefaults delfoiDefaults = RequestUtils.getDefaults(pageRequestContext);
          delfoiUserDAO.create(delfoi, user, delfoiDefaults.getDefaultDelfoiUserRole(), user);
        }
        else {
          user = userEmail.getUser();
        }
        
        // Ensure panel membership 
        
        PanelUser panelUser = panelUserDAO.findByPanelAndUserAndStamp(panelInvitation.getPanel(), user, panelInvitation.getPanel().getCurrentStamp());
        if (panelUser == null) {
          panelUser = panelUserDAO.create(panelInvitation.getPanel(), user, panelInvitation.getRole(), PanelUserJoinType.INVITED, panelInvitation.getPanel().getCurrentStamp(), user);
        }
        
        // Ensure user is logged in
        
        RequestUtils.loginUser(pageRequestContext, user);
        
        // TODO if user has no password or external authentication, add a welcome message 
        // TODO SMVC refactoring; addMessage + setRedirectURL does not compute
        
        // Redirect to the invitation target
        
        String redirectUrl = pageRequestContext.getRequest().getContextPath() + "/" + panel.getRootFolder().getUrlName();
        if (query != null) {
          redirectUrl += "/" + query.getUrlName();
        }
        pageRequestContext.setRedirectURL(redirectUrl);
      }
    }
    else {

      // Invitation was rejected
      
      panelInvitationDAO.updateState(panelInvitation, PanelInvitationState.DECLINED, null);
      UserEmail userEmail = userEmailDAO.findByAddress(panelInvitation.getEmail());
      if (userEmail != null) {
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
