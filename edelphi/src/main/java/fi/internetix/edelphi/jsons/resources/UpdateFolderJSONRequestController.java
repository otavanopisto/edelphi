package fi.internetix.edelphi.jsons.resources;

import java.util.Locale;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.resources.FolderDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class UpdateFolderJSONRequestController extends JSONController {

  public UpdateFolderJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FolderDAO folderDAO = new FolderDAO();
    UserDAO userDAO = new UserDAO();

    Long folderId = jsonRequestContext.getLong("folderId");
    String name = jsonRequestContext.getString("name");
    String urlName = ResourceUtils.getUrlName(name);

    Folder folder = folderDAO.findById(folderId);
    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    
    if (ResourceUtils.isUrlNameAvailable(urlName, folder.getParentFolder(), folder)) {
      folderDAO.updateName(folder, name, urlName, loggedUser);
      jsonRequestContext.addResponseParameter("folderId", folder.getId());
    }
    else {
      Messages messages = Messages.getInstance();
      Locale locale = jsonRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_RESOURCE_NAME, messages.getText(locale, "exception.1005.resourceNameInUse"));
    }
    
  }
}
