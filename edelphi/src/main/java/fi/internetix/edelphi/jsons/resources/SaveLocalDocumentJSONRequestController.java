package fi.internetix.edelphi.jsons.resources;

import java.util.Locale;

import org.apache.commons.lang.math.NumberUtils;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.resources.FolderDAO;
import fi.internetix.edelphi.dao.resources.LocalDocumentDAO;
import fi.internetix.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.internetix.edelphi.dao.resources.ResourceDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.base.Delfoi;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.resources.LocalDocument;
import fi.internetix.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.internetix.edelphi.domainmodel.resources.Resource;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.LoginRequiredException;
import fi.internetix.smvc.Severity;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class SaveLocalDocumentJSONRequestController extends JSONController {

  @Override
  public void authorize(RequestContext requestContext) throws LoginRequiredException, AccessDeniedException {
    ResourceDAO resourceDAO = new ResourceDAO();
    Long parentFolderId = requestContext.getLong("parentFolderId");
    
    Resource resource = resourceDAO.findById(parentFolderId);
    
    Panel resourcePanel = ResourceUtils.getResourcePanel(resource);
    
    if (resourcePanel != null) {
      authorizePanel(requestContext, resourcePanel, DelfoiActionName.MANAGE_PANEL_MATERIALS.toString());
    } else {
      Delfoi resourceDelfoi = ResourceUtils.getResourceDelfoi(resource);
      authorizeDelfoi(requestContext, resourceDelfoi, DelfoiActionName.MANAGE_DELFOI_MATERIALS.toString());
    }
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FolderDAO folderDAO = new FolderDAO();
    UserDAO userDAO = new UserDAO();
    LocalDocumentDAO localDocumentDAO = new LocalDocumentDAO();
    LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO();

    String name = jsonRequestContext.getString("name");
    String urlName = ResourceUtils.getUrlName(name);
    Long parentFolderId = jsonRequestContext.getLong("parentFolderId");
    Integer pageCount = jsonRequestContext.getInteger("pageCount");
    Long localDocumentId = jsonRequestContext.getLong("localDocumentId");

    User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
    Folder parentFolder = folderDAO.findById(parentFolderId);
    LocalDocument localDocument;
    
    Messages messages = Messages.getInstance();
    Locale locale = jsonRequestContext.getRequest().getLocale();
    
    if (localDocumentId == null) {
      if (ResourceUtils.isUrlNameAvailable(urlName, parentFolder)) {
        Integer indexNumber = ResourceUtils.getNextIndexNumber(parentFolder);
        localDocument = localDocumentDAO.create(name, urlName, parentFolder, loggedUser, indexNumber);
        
        jsonRequestContext.addResponseParameter("localDocumentId", localDocument.getId());
      }
      else {
        throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_RESOURCE_NAME, messages.getText(locale, "exception.1005.resourceNameInUse"));
      }
    } else {
      localDocument = localDocumentDAO.findById(localDocumentId);
      localDocumentDAO.updateParentFolder(localDocument, parentFolder, loggedUser);
      
      if (!localDocument.getName().equals(name)) {
        if (ResourceUtils.isUrlNameAvailable(urlName, localDocument.getParentFolder(), localDocument)) {
          localDocumentDAO.updateName(localDocument, name, urlName, loggedUser);
        } else {
          throw new SmvcRuntimeException(EdelfoiStatusCode.DUPLICATE_RESOURCE_NAME, messages.getText(locale, "exception.1005.resourceNameInUse"));
        }
      }
    }

    int pageNumber = 0;
    int newPageCount = 0;
    int removedPageCount = 0;
    
    for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
      String pagePrefix = "page." + pageIndex + ".";
      
      String pageIdParam = jsonRequestContext.getString(pagePrefix + "id");
      boolean createNewPage = jsonRequestContext.getBoolean(pagePrefix + "isNew");
      boolean deletePage = jsonRequestContext.getBoolean(pagePrefix + "isDeleted");
      Long pageId = createNewPage ? null : NumberUtils.createLong(pageIdParam);

      String pageTitle = jsonRequestContext.getString(pagePrefix + "title");
      String pageContent = jsonRequestContext.getString(pagePrefix + "content");
      
      LocalDocumentPage page;
      
      if (createNewPage) {
        // Skip page that's new and deleted
        if (deletePage) {
          jsonRequestContext.addResponseParameter("removedPage." + removedPageCount + ".id", pageIdParam);
          removedPageCount++;
          continue;
        }
        
        page = localDocumentPageDAO.create(localDocument, loggedUser, pageTitle, pageNumber, pageContent);
        jsonRequestContext.addResponseParameter("newPage." + newPageCount + ".id", page.getId());
        jsonRequestContext.addResponseParameter("newPage." + newPageCount + ".tempId", pageIdParam);
        jsonRequestContext.addResponseParameter("newPage." + newPageCount + ".number", page.getPageNumber());
        newPageCount++;
      } else {
        page = localDocumentPageDAO.findById(pageId);
        localDocumentPageDAO.updateTitle(page, loggedUser, pageTitle);
        localDocumentPageDAO.updateContent(page, loggedUser, pageContent);
        localDocumentPageDAO.updatePageNumber(page, loggedUser, pageNumber);
      }
      
      if (deletePage) {
        jsonRequestContext.addResponseParameter("removedPage." + removedPageCount + ".id", page.getId());
        removedPageCount++;

        localDocumentPageDAO.delete(page);
      }
      
      pageNumber++;
    }
    
    jsonRequestContext.addResponseParameter("newPageCount", newPageCount);
    jsonRequestContext.addResponseParameter("removedPageCount", removedPageCount);
    jsonRequestContext.addResponseParameter("localDocumentId", localDocument.getId());
    jsonRequestContext.addResponseParameter("localDocumentParentFolderId", localDocument.getParentFolder().getId());

    jsonRequestContext.addMessage(Severity.OK, messages.getText(locale, "panelAdmin.block.localDocumentEditor.savedMessage"));
  }
  
}
