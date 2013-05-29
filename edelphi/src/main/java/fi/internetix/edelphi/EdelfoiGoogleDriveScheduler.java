package fi.internetix.edelphi;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.List;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.internetix.edelphi.dao.GenericDAO;
import fi.internetix.edelphi.dao.resources.GoogleDocumentDAO;
import fi.internetix.edelphi.dao.resources.GoogleImageDAO;
import fi.internetix.edelphi.domainmodel.resources.GoogleDocument;
import fi.internetix.edelphi.domainmodel.resources.GoogleImage;
import fi.internetix.edelphi.utils.GoogleDriveUtils;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.smvc.logging.Logging;

@Singleton
public class EdelfoiGoogleDriveScheduler {

  @PersistenceContext
  private EntityManager entityManager;

  @Schedule(minute = "*/5", hour = "*", persistent = false)
  public void refreshGoogleDriveFiles() {
    GenericDAO.setEntityManager(entityManager);
    try {
    	 Drive drive = GoogleDriveUtils.getAdminService();
    	
    	 GoogleDocumentDAO googleDocumentDAO = new GoogleDocumentDAO();
    	 List<GoogleDocument> googleDocuments = googleDocumentDAO.listByArchivedOrderByLastSynchronizedAsc(Boolean.FALSE, 0, 5);
    	 for (GoogleDocument googleDocument : googleDocuments) {
    		 try {
     			 File file = GoogleDriveUtils.getFile(drive, googleDocument.getResourceId());
    			 if (file.getLabels().getTrashed()) {
    				 // User has trashed the file from Google Drive, so archiving it.
    				 googleDocumentDAO.archive(googleDocument);
    			 } else {
      			 if (!googleDocument.getName().equals(file.getTitle())) {
        			 String urlName = ResourceUtils.getUniqueUrlName(file.getTitle(), googleDocument.getParentFolder());
        			 googleDocumentDAO.updateName(googleDocument, file.getTitle(), urlName);
      			 }
      			 
      			 googleDocumentDAO.updateLastModified(googleDocument, new Date(file.getModifiedDate().getValue()));
      			 googleDocumentDAO.updateLastSynchronized(googleDocument, new Date(System.currentTimeMillis()));
    			 }
    		 } catch (GoogleJsonResponseException e) {
    			 if (e.getStatusCode() == 404) {
    				 // User has removed the file from Google Drive or permission to it, so archiving it.
    				 googleDocumentDAO.archive(googleDocument);
    			 } else {
      			 Logging.logException("GoogleDocument synchronization failed", e);
    			 }
    		 } catch (Exception e) {
    			 Logging.logException("GoogleDocument synchronization failed", e);
    		 }
    	 }

    	 GoogleImageDAO googleImageDAO = new GoogleImageDAO();
    	 List<GoogleImage> googleImages = googleImageDAO.listByArchivedOrderByLastSynchronizedAsc(Boolean.FALSE, 0, 5);
    	 for (GoogleImage googleImage : googleImages) {
    		 try {
    			 File file = GoogleDriveUtils.getFile(drive, googleImage.getResourceId());
    			 if (file.getLabels().getTrashed()) {
    				 // User has trashed the file from Google Drive, so archiving it.
    				 googleImageDAO.archive(googleImage);
    			 } else {
      			 if (!googleImage.getName().equals(file.getTitle())) {
        			 String urlName = ResourceUtils.getUniqueUrlName(file.getTitle(), googleImage.getParentFolder());
        			 googleImageDAO.updateName(googleImage, file.getTitle(), urlName);
      			 }
      			 
      			 googleImageDAO.updateLastModified(googleImage, new Date(file.getModifiedDate().getValue()));
      			 googleImageDAO.updateLastSynchronized(googleImage, new Date(System.currentTimeMillis()));
    			 }
    		 } catch (GoogleJsonResponseException e) {
    			 if (e.getStatusCode() == 404) {
    				 // User has removed the file from Google Drive or permission to it, so archiving it.
    				 googleImageDAO.archive(googleImage);
    			 } else {
      			 Logging.logException("GoogleImage synchronization failed", e);
    			 }
    		 } catch (Exception e) {
    			 Logging.logException("GoogleImage synchronization failed", e);
    		 }
    	 }
    } catch (GeneralSecurityException e1) {
    	Logging.logException("Failed to obtain Google drive service", e1);
		} catch (IOException e1) {
    	Logging.logException("Failed to obtain Google drive service", e1);
		} finally {
      GenericDAO.setEntityManager(null);
    }
  }
  
}
