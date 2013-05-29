package fi.internetix.edelphi.binaries.resources;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.binaries.BinaryController;
import fi.internetix.edelphi.dao.resources.DocumentDAO;
import fi.internetix.edelphi.domainmodel.resources.Document;
import fi.internetix.edelphi.domainmodel.resources.GoogleDocument;
import fi.internetix.edelphi.domainmodel.resources.LocalDocument;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.GoogleDriveUtils;
import fi.internetix.edelphi.utils.GoogleDriveUtils.DownloadResponse;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.BinaryRequestContext;

public class ViewDocumentBinaryController extends BinaryController {

  @Override
  public void process(BinaryRequestContext binaryRequestContext) {
    DocumentDAO documentDAO = new DocumentDAO();

    Long documentId = binaryRequestContext.getLong("documentId");

    Document document = documentDAO.findById(documentId);
    if (document instanceof LocalDocument) {
      String baseUrl = RequestUtils.getBaseUrl(binaryRequestContext.getRequest());
      binaryRequestContext.setRedirectURL(baseUrl + "/resources/viewdocumentpage.binary?documentId=" + documentId + "&pageNumber=0");
    } else if (document instanceof GoogleDocument) {
      try {
        handleGoogleDocument((GoogleDocument) document, binaryRequestContext);
      }
      catch (Exception e) {
        Messages messages = Messages.getInstance();
        Locale locale = binaryRequestContext.getRequest().getLocale();
        throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
      }
    }
  }

  private void handleGoogleDocument(GoogleDocument googleDocument, BinaryRequestContext binaryRequestContext) throws IOException, GeneralSecurityException {
  	Drive drive = GoogleDriveUtils.getAdminService();
  	if (drive != null) {
    	try {
    		byte[] outputData = null;
    		String outputMime = null;
    		String outputFileName = null;
    		
  			File file = GoogleDriveUtils.getFile(drive, googleDocument.getResourceId());
  			String mimeType = file.getMimeType();
  			if ("application/vnd.google-apps.presentation".equals(mimeType)) {
  				DownloadResponse response = GoogleDriveUtils.exportFile(drive, file, "application/pdf");
  				outputData = response.getData();
  				outputMime = response.getMimeType();
  				outputFileName = ResourceUtils.getUrlName(file.getTitle()) + ".pdf";
  			} else {
  				if (file.getDownloadUrl() != null) {
  				  DownloadResponse response = GoogleDriveUtils.downloadFile(drive, file);
  					outputData = response.getData();
  					outputMime = response.getMimeType();
  					outputFileName = ResourceUtils.getUrlName(file.getTitle());
  				} else {
  					throw new IOException("Don't know how to handle GoogleDocument #" + googleDocument.getId());
  				}
  			}
  			
  			if (outputData != null && outputMime != null) {
    			binaryRequestContext.setResponseContent(outputData, outputMime);
    			if (StringUtils.isNotBlank(outputFileName)) {
    				binaryRequestContext.setFileName(outputFileName);
    			}
  			} 
  			
  		} catch (IOException e) {
        Messages messages = Messages.getInstance();
        Locale locale = binaryRequestContext.getRequest().getLocale();
        throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
  		}
  	}
  }
}
