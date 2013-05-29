package fi.internetix.edelphi.pages.panel;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.resources.FolderDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ManagePanelFolderPageController extends PanelPageController {

  public ManagePanelFolderPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    FolderDAO folderDAO = new FolderDAO();

    if (!pageRequestContext.getBoolean("newFolder")) {
      Long folderId = pageRequestContext.getLong("folderId");
      Folder folder = folderDAO.findById(folderId);
      
      pageRequestContext.getRequest().setAttribute("folder", folder);
      pageRequestContext.getRequest().setAttribute("folderId", folder.getId());

    } else {
      pageRequestContext.getRequest().setAttribute("folderId", "NEW");
    }
      
    ActionUtils.includeRoleAccessList(pageRequestContext);
    pageRequestContext.setIncludeJSP("/jsp/panels/managepanelfolder.jsp");
  }
  
}
