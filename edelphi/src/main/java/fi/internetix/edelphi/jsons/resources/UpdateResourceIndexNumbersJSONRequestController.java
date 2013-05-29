package fi.internetix.edelphi.jsons.resources;

import java.util.StringTokenizer;

import net.sf.json.JSONObject;
import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.resources.FolderDAO;
import fi.internetix.edelphi.dao.resources.ResourceDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.Resource;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class UpdateResourceIndexNumbersJSONRequestController extends JSONController {
  
  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    setAccessAction(DelfoiActionName.MANAGE_DELFOI_MATERIALS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FolderDAO folderDAO = new FolderDAO();

    JSONObject folderMap = JSONObject.fromObject(jsonRequestContext.getString("folderOrder"));
    Long parentFolderId = jsonRequestContext.getLong("parentFolderId");

    User loggedUser = RequestUtils.getUser(jsonRequestContext);

    Folder folder = folderDAO.findById(parentFolderId);
    
    handleFolder(folder, folderMap, loggedUser);
  }

  private void handleFolder(Folder folder, JSONObject folderMap, User loggedUser) {
    ResourceDAO resourceDAO = new ResourceDAO();
    StringTokenizer tokx = new StringTokenizer(folderMap.getString(folder.getId().toString()), ",");
    
    int i = 0;
    while (tokx.hasMoreTokens()) {
      Long resourceId = Long.parseLong(tokx.nextToken());
      
      Resource resource = resourceDAO.findById(resourceId);
      resourceDAO.updateResourceIndexNumber(resource, i, loggedUser);
      
      if (!resource.getParentFolder().getId().equals(folder.getId()))
        resourceDAO.updateParentFolder(resource, folder, loggedUser);
      
      if (resource instanceof Folder) {
        handleFolder((Folder) resource, folderMap, loggedUser);
      }
      
      i++;
    }
  }
}
