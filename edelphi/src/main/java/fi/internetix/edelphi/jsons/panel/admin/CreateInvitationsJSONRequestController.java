package fi.internetix.edelphi.jsons.panel.admin;

import java.util.Locale;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.base.EmailMessageDAO;
import fi.internetix.edelphi.dao.panels.PanelInvitationDAO;
import fi.internetix.edelphi.dao.panels.PanelUserDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.dao.users.DelfoiUserDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.dao.users.UserPasswordDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.base.DelfoiUser;
import fi.internetix.edelphi.domainmodel.base.EmailMessage;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitation;
import fi.internetix.edelphi.domainmodel.panels.PanelInvitationState;
import fi.internetix.edelphi.domainmodel.panels.PanelUser;
import fi.internetix.edelphi.domainmodel.panels.PanelUserJoinType;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.domainmodel.users.DelfoiUserRole;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class CreateInvitationsJSONRequestController extends JSONController {

  public CreateInvitationsJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_INVITATIONS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Panel panel = RequestUtils.getPanel(jsonRequestContext);
    if (panel == null) {
      throw new PageNotFoundException(jsonRequestContext.getRequest().getLocale());
    }

    UserDAO userDAO = new UserDAO();
    QueryDAO queryDAO = new QueryDAO();
    PanelUserDAO panelUserDAO = new PanelUserDAO();
    EmailMessageDAO emailMessageDAO = new EmailMessageDAO();
    PanelInvitationDAO panelInvitationDAO = new PanelInvitationDAO();

    User creator = userDAO.findById(jsonRequestContext.getLoggedUserId());

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    String invitationMessage = jsonRequestContext.getString("invitationMessage");
    String panelNameReplace = messages.getText(locale, "panel.admin.inviteUsers.panelNameReplace");
    String acceptReplace = messages.getText(locale, "panel.admin.inviteUsers.acceptReplace");
    String declineReplace = messages.getText(locale, "panel.admin.inviteUsers.declineReplace");
    String senderReplace = messages.getText(locale, "panel.admin.inviteUsers.senderReplace");
    if (invitationMessage == null || invitationMessage.indexOf(acceptReplace) == -1) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.INVALID_MAIL_TEMPLATE, messages.getText(locale, "exception.1000.noAcceptLink"));
    }

    boolean inviteUsers = jsonRequestContext.getInteger("addUsers") == null;
    Long queryId = jsonRequestContext.getLong("queryId");
    Query query = queryId == null ? null : queryDAO.findById(queryId);

    int passwordGenerationCount = 0;
    String generatedPassword = null;
    int invitationsHandled = 0;
    if (inviteUsers) {

      // Create invitations and mail them to users

      String baseUrl = RequestUtils.getBaseUrl(jsonRequestContext.getRequest());
      int invitationCount = jsonRequestContext.getInteger("invitationCount").intValue();
      for (int i = 0; i < invitationCount; i++) {
        String email = jsonRequestContext.getString("inviteUser." + i + ".email");
        PanelInvitation invitation = query == null ? panelInvitationDAO.findByPanelAndEmail(panel, email) : panelInvitationDAO.findByPanelAndQueryAndEmail(panel, query, email);

        boolean createInvitation = true;
        if (invitation != null && invitation.getState() == PanelInvitationState.DECLINED) {
          createInvitation = false;
          UserEmailDAO userEmailDAO = new UserEmailDAO();
          UserEmail userEmail = userEmailDAO.findByAddress(email);
          String personName = userEmail == null ? email : userEmail.getUser().getFullName(false, true);
          jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.inviteUsers.userDeclinedAlready", new String[] { personName }));
        }

        if (createInvitation) {
          String invitationHash = invitation == null ? UUID.randomUUID().toString() : invitation.getHash();

          String mailSubject = messages.getText(locale, "panel.admin.inviteUsers.mailSubject");
          int index = mailSubject.indexOf(panelNameReplace);
          if (index >= 0) {
            mailSubject = mailSubject.replace(panelNameReplace, panel.getName());
          }
          String mailContent = invitationMessage;
          index = mailContent.indexOf(panelNameReplace);
          if (index >= 0) {
            mailContent = mailContent.replace(panelNameReplace, panel.getName());
          }
          index = mailContent.indexOf(acceptReplace);
          if (index >= 0) {
            mailContent = mailContent.replace(acceptReplace, baseUrl + "/joinpanel.page?panelId=" + panel.getId() + "&hash=" + invitationHash + "&join=1");
          }
          index = mailContent.indexOf(declineReplace);
          if (index >= 0) {
            mailContent = mailContent.replace(declineReplace, baseUrl + "/joinpanel.page?panelId=" + panel.getId() + "&hash=" + invitationHash + "&join=0");
          }
          index = mailContent.indexOf(senderReplace);
          if (index >= 0) {
            mailContent = mailContent.replace(senderReplace, creator.getFullName(false, false));
          }
          
          if (invitation == null) {
            EmailMessage emailMessage = emailMessageDAO.create(creator.getDefaultEmailAsString(), email, mailSubject, mailContent, creator);
            invitation = panelInvitationDAO.create(panel, query, email, invitationHash, panel.getDefaultPanelUserRole(), PanelInvitationState.IN_QUEUE,
                emailMessage, creator);
          }
          else {
            EmailMessage emailMessage = invitation.getEmailMessage();
            if (emailMessage == null) {
              emailMessage = emailMessageDAO.create(creator.getDefaultEmailAsString(), email, mailSubject, mailContent, creator);
            }
            else {
              emailMessageDAO.update(emailMessage, creator.getDefaultEmailAsString(), email, mailSubject, mailContent, creator);
            }
            invitation = panelInvitationDAO.updateState(invitation, PanelInvitationState.IN_QUEUE, creator);
          }
          invitationsHandled++;
        }
      }
    }
    else {

      // Add the users to the panel directly

      int invitationCount = jsonRequestContext.getInteger("invitationCount").intValue();
      for (int i = 0; i < invitationCount; i++) {
        Long userId = jsonRequestContext.getLong("inviteUser." + i + ".id");
        String firstName = jsonRequestContext.getString("inviteUser." + i + ".firstName");
        String lastName = jsonRequestContext.getString("inviteUser." + i + ".lastName");
        String email = jsonRequestContext.getString("inviteUser." + i + ".email");

        User user = null;
        if (userId == null) {
          // create a new user with the given name and email address
          UserEmailDAO userEmailDAO = new UserEmailDAO();
          UserEmail userEmail = userEmailDAO.findByAddress(email);
          user = userEmail == null ? null : userEmail.getUser();
          if (user == null) {
            user = userDAO.create(firstName, lastName, null, creator);
            userEmail = userEmailDAO.create(user, email);
            userDAO.addUserEmail(user, userEmail, true, creator);
            if (passwordGenerationCount == 0) {
              generatedPassword = RandomStringUtils.randomAlphanumeric(8);
            }
            UserPasswordDAO userPasswordDAO = new UserPasswordDAO();
            userPasswordDAO.create(user, RequestUtils.md5EncodeString(generatedPassword));
            passwordGenerationCount++;
          }
          DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
          DelfoiUser delfoiUser = delfoiUserDAO.findByDelfoiAndUser(RequestUtils.getDefaults(jsonRequestContext).getDelfoi(), user);
          if (delfoiUser == null) {
            Delfoi delfoi = RequestUtils.getDefaults(jsonRequestContext).getDelfoi();
            DelfoiUserRole delfoiUserRole = RequestUtils.getDefaults(jsonRequestContext).getDefaultDelfoiUserRole();
            delfoiUserDAO.create(delfoi, user, delfoiUserRole, creator);
          }
          userId = user.getId();
        }
        user = userDAO.findById(userId);

        PanelUser panelUser = panelUserDAO.findByPanelAndUserAndStamp(panel, user, panel.getCurrentStamp());
        if (panelUser == null) {
          panelUser = panelUserDAO.create(panel, user, panel.getDefaultPanelUserRole(), PanelUserJoinType.ADDED, panel.getCurrentStamp(), creator);
          invitationsHandled++;
        }
        else {
          jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.inviteUsers.userExists", new String[] { user.getFullName() }));
        }
        PanelInvitation panelInvitation = panelInvitationDAO.findByPanelAndEmail(panel, email);
        if (panelInvitation != null) {
          panelInvitationDAO.delete(panelInvitation);
        }
      }
    }

    if (inviteUsers) {
      if (invitationsHandled == 0) {
        jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.inviteUsers.noInvitationsSent"));
      }
      else if (invitationsHandled == 1) {
        jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panel.admin.inviteUsers.invitationSent"));
      }
      else {
        jsonRequestContext.addMessage(Severity.OK,
            messages.getText(locale, "panel.admin.inviteUsers.invitationsSent", new String[] { invitationsHandled + "" }));
      }
    }
    else {
      if (invitationsHandled == 0) {
        jsonRequestContext.addMessage(Severity.WARNING, messages.getText(locale, "panel.admin.inviteUsers.noUsersAdded"));
      }
      else if (invitationsHandled == 1) {
        jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panel.admin.inviteUsers.userAdded"));
      }
      else {
        jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panel.admin.inviteUsers.usersAdded", new String[] { invitationsHandled + "" }));
      }
      if (passwordGenerationCount > 0) {
        jsonRequestContext.addMessage(Severity.WARNING,
            messages.getText(locale, "panel.admin.inviteUsers.passwordGenerated", new String[] { passwordGenerationCount + "", generatedPassword }));
      }
    }
  }

}
