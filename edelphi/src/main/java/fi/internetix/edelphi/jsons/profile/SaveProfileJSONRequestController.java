package fi.internetix.edelphi.jsons.profile;

import java.util.List;
import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class SaveProfileJSONRequestController extends JSONController {

  public SaveProfileJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_USER_PROFILE, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserDAO userDAO = new UserDAO();
    UserEmailDAO userEmailDAO = new UserEmailDAO();

    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();

    User user = userDAO.findById(jsonRequestContext.getLong("userId"));
    User loggedUser = RequestUtils.getUser(jsonRequestContext);
    
    // Verify that the logged user is the same as the user being edited.
    if (!loggedUser.getId().equals(user.getId())) {
      throw new AccessDeniedException(jsonRequestContext.getRequest().getLocale());
    }
    
    String firstName = jsonRequestContext.getString("firstName");
    String lastName = jsonRequestContext.getString("lastName");
    String nickname = jsonRequestContext.getString("nickname");
    String email = jsonRequestContext.getLowercaseString("email");
    
    // Update user's basic information
    
    userDAO.update(user, firstName, lastName, nickname, loggedUser);
    
    // Add, edit, or delete user e-mail address
    
    Long userEmailId = jsonRequestContext.getLong("emailId");
    UserEmail userEmail = userEmailId == null ? null : userEmailDAO.findById(userEmailId); 
    if (email == null && userEmail != null) {
      userDAO.updateDefaultEmail(user, null, loggedUser);
      userEmailDAO.delete(userEmail);
      List<UserEmail> userEmails = userEmailDAO.listByUser(user);
      if (!userEmails.isEmpty()) {
        userDAO.updateDefaultEmail(user, userEmails.get(0), loggedUser);
      }
    }
    else if (email != null && userEmail != null && !email.equals(userEmail.getAddress())) {
      userEmailDAO.updateAddress(userEmail, email);
    }
    else if (email != null && userEmail == null) {
      userEmail = userEmailDAO.create(user, email);
      userDAO.updateDefaultEmail(user, userEmail, loggedUser);
    }

    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "profile.block.savedMessage"));
  }

}
