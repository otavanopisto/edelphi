package fi.internetix.edelphi.jsons.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.search.SearchResult;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class SearchUsersJSONRequestController extends JSONController {

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    
    // Search string preparation
    
    String text = jsonRequestContext.getLowercaseString("text");
    if (!StringUtils.isBlank(text)) {
      text = text.replace("*", "");
    }
    
    // Actual search
    
    List<Map<String, Object>> jsonResults = new ArrayList<Map<String, Object>>();
    if (!StringUtils.isBlank(text)) {
      UserDAO userDAO = new UserDAO();
      SearchResult<User> results = text.indexOf(" ") > 0 ? userDAO.searchByFullName(10, 0, text) : userDAO.searchByNameOrEmail(10, 0, text);
      List<User> users = results.getResults();
      for (User user : users) {
        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("id", user.getId());
        userInfo.put("firstName", user.getFirstName());
        userInfo.put("lastName", user.getLastName());
        userInfo.put("email", user.getDefaultEmail() == null ? null : user.getDefaultEmail().getAddress());
        jsonResults.add(userInfo);
      }
    }
    jsonRequestContext.addResponseParameter("results", jsonResults);
  }

}
