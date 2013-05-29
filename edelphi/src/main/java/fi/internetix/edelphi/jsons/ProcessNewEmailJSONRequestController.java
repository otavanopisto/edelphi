package fi.internetix.edelphi.jsons;

import fi.internetix.edelphi.dao.users.DelfoiUserDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.users.DelfoiUserRole;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.AuthUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class ProcessNewEmailJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    Boolean linkEmail = jsonRequestContext.getBoolean("link");
    String email = jsonRequestContext.getString("email");
    if (linkEmail) {
      if (email != null) {
        UserEmailDAO userEmailDAO = new UserEmailDAO();
        UserEmail userEmail = userEmailDAO.findByAddress(email);
        if (userEmail == null) {
          User user = RequestUtils.getUser(jsonRequestContext);
          if (user != null) {
            UserDAO userDAO = new UserDAO();
            userEmail = userEmailDAO.create(user, email);
            userDAO.addUserEmail(user, userEmail, user.getDefaultEmail() == null, user);
          }
        }
      }
    }
    else {
      UserDAO userDAO = new UserDAO();
      UserEmailDAO userEmailDAO = new UserEmailDAO();
      DelfoiUserDAO delfoiUserDAO = new DelfoiUserDAO();
      UserEmail userEmail = userEmailDAO.findByAddress(email);
      if (userEmail == null) {
        User user = userDAO.create(null, null, null, null);
        userEmail = userEmailDAO.create(user, email);
        userDAO.addUserEmail(user, userEmail, true, user);
        Delfoi delfoi = RequestUtils.getDefaults(jsonRequestContext).getDelfoi();
        DelfoiUserRole delfoiUserRole = RequestUtils.getDefaults(jsonRequestContext).getDefaultDelfoiUserRole();
        delfoiUserDAO.create(delfoi, user, delfoiUserRole, user);
        RequestUtils.loginUser(jsonRequestContext, user);
      }
    }
    String baseURL = RequestUtils.getBaseUrl(jsonRequestContext.getRequest());
    String loginRedirectUrl = AuthUtils.retrieveRedirectUrl(jsonRequestContext);
    String redirectUrl = loginRedirectUrl != null ? loginRedirectUrl : baseURL + "/index.page";
    jsonRequestContext.setRedirectURL(redirectUrl);
  }
  
}
