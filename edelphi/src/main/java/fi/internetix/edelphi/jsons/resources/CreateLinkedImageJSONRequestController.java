package fi.internetix.edelphi.jsons.resources;

import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.resources.FolderDAO;
import fi.internetix.edelphi.dao.resources.LinkedImageDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class CreateLinkedImageJSONRequestController extends JSONController {

  public CreateLinkedImageJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FolderDAO folderDAO = new FolderDAO();
    UserDAO userDAO = new UserDAO();
    LinkedImageDAO linkedImageDAO = new LinkedImageDAO();
    
    String name = jsonRequestContext.getString("name");
    String urlName = ResourceUtils.getUrlName(name);
    String url = jsonRequestContext.getString("url");
    Long parentFolderId = jsonRequestContext.getLong("parentFolderId");
    
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    Folder parentFolder = folderDAO.findById(parentFolderId);
    
    Integer indexNumber = ResourceUtils.getNextIndexNumber(parentFolder);
    if (ResourceUtils.isUrlNameAvailable(urlName, parentFolder)) {
      linkedImageDAO.create(name, urlName, url, parentFolder, loggedUser, indexNumber);
    
//      String redirectURL = jsonRequestContext.getRequest().getContextPath() + "/panels/viewpanel.page?panelId=" + panel.getId();
//      jsonRequestContext.setRedirectURL(redirectURL);
    }
    else {
      Messages messages = Messages.getInstance();
      Locale locale = jsonRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_RESOURCE_NAME, messages.getText(locale, "exception.1005.resourceNameInUse"));
    }
  }
  
}
