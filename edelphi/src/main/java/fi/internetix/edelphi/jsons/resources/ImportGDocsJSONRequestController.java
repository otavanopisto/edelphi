package fi.internetix.edelphi.jsons.resources;

import java.util.Date;
import java.util.Locale;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.resources.FolderDAO;
import fi.internetix.edelphi.dao.resources.GoogleDocumentDAO;
import fi.internetix.edelphi.dao.resources.GoogleImageDAO;
import fi.internetix.edelphi.dao.users.UserDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.resources.Folder;
import fi.internetix.edelphi.domainmodel.users.User;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.jsons.JSONController;
import fi.internetix.edelphi.utils.GoogleDriveUtils;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.edelphi.utils.SystemUtils;
import fi.internetix.smvc.AccessDeniedException;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.JSONRequestContext;

public class ImportGDocsJSONRequestController extends JSONController {

  public ImportGDocsJSONRequestController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void process(JSONRequestContext jsonRequestContext) {
    FolderDAO folderDAO = new FolderDAO();
    UserDAO userDAO = new UserDAO();
    GoogleDocumentDAO googleDocumentDAO = new GoogleDocumentDAO();
    GoogleImageDAO googleImageDAO = new GoogleImageDAO();
    
    Drive drive = GoogleDriveUtils.getAuthenticatedService(jsonRequestContext);
    if (drive == null) {
      throw new AccessDeniedException(jsonRequestContext.getRequest().getLocale());
    }
    
    Long parentFolderId = jsonRequestContext.getLong("parentFolderId");
    String[] selectedGDocs = jsonRequestContext.getStrings("selectedgdoc");
    
    try {
      User loggedUser = userDAO.findById(jsonRequestContext.getLoggedUserId());
      Folder parentFolder = folderDAO.findById(parentFolderId);

      for (String resourceId : selectedGDocs) {
      	String googleDriveAccountId = SystemUtils.getSettingValue("googleDrive.accountUser");

        GoogleDriveUtils.insertUserPermission(drive, resourceId, googleDriveAccountId, "reader");
        File file = GoogleDriveUtils.getFile(drive, resourceId);
        boolean isImage = file.getImageMediaMetadata() != null;
        Date created = new Date(file.getCreatedDate().getValue());
        Date lastModified = new Date(file.getModifiedDate().getValue());

        String name = file.getTitle();
        String urlName = ResourceUtils.getUniqueUrlName(name, parentFolder);
        
        Integer indexNumber = ResourceUtils.getNextIndexNumber(parentFolder);
        if (isImage) {
          googleImageDAO.create(name, urlName, parentFolder, resourceId, indexNumber, new Date(), loggedUser, created, loggedUser, lastModified);
        } else {
          googleDocumentDAO.create(name, urlName, parentFolder, resourceId, indexNumber, new Date(), loggedUser, created, loggedUser, lastModified);
        }
      }
    }
    catch (Exception e) {
      Messages messages = Messages.getInstance();
      Locale locale = jsonRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
    }
  }

}
