package fi.internetix.edelphi.jsons.users;

import fi.internetix.edelphi.dao.users.UserEmailDAO;
import fi.internetix.edelphi.domainmodel.users.UserEmail;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class FindUserByEmailJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    UserEmailDAO userEmailDAO = new UserEmailDAO();
    UserEmail userEmail = userEmailDAO.findByAddress(jsonRequestContext.getString("email"));
    if (userEmail != null) {
      jsonRequestContext.addResponseParameter("userId", userEmail.getUser().getId());
      jsonRequestContext.addResponseParameter("userEmailId", userEmail.getId());
    }
  }

}
