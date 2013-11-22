package fi.internetix.edelphi.binaries.resources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.Locale;

import javax.servlet.ServletOutputStream;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.binaries.BinaryController;
import fi.internetix.edelphi.dao.resources.ImageDAO;
import fi.internetix.edelphi.domainmodel.resources.GoogleImage;
import fi.internetix.edelphi.domainmodel.resources.Image;
import fi.internetix.edelphi.domainmodel.resources.LinkedImage;
import fi.internetix.edelphi.domainmodel.resources.LocalImage;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.GoogleDriveUtils;
import fi.internetix.edelphi.utils.GoogleDriveUtils.DownloadResponse;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.BinaryRequestContext;

public class ViewImageBinaryRequestController extends BinaryController {

  @Override
  public void process(BinaryRequestContext binaryRequestContext) {
    ImageDAO imageDAO = new ImageDAO();

    Long imageId = binaryRequestContext.getLong("imageId");

    Image image = imageDAO.findById(imageId);

    if (image != null) {
      if (image instanceof LinkedImage) {
        handleLinkedImage(binaryRequestContext, (LinkedImage) image);
      } else if (image instanceof LocalImage) {
        handleLocalImage(binaryRequestContext, (LocalImage) image);
      } else if (image instanceof GoogleImage) {
        handleGoogleImage(binaryRequestContext, (GoogleImage) image);
      }
    } else
      throw new RuntimeException("image not found");
  }

  private void handleLocalImage(BinaryRequestContext binaryRequestContext, LocalImage image) {
    binaryRequestContext.setResponseContent(image.getData(), image.getContentType());
  }

  private void handleLinkedImage(BinaryRequestContext binaryRequestContext, LinkedImage image) {
    try {
      URL imageUrl = new URL(image.getUrl());
      URLConnection uc = imageUrl.openConnection();
      binaryRequestContext.getResponse().setContentType(uc.getContentType());

      InputStream in = uc.getInputStream();
      ServletOutputStream out = binaryRequestContext.getResponse().getOutputStream();

      byte[] buf = new byte[1024];
      int len;

      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private void handleGoogleImage(BinaryRequestContext binaryRequestContext, GoogleImage googleImage) {
  	try {
    	Drive drive = GoogleDriveUtils.getAdminService();
			File file = GoogleDriveUtils.getFile(drive, googleImage.getResourceId());
			if (file.getExportLinks() != null && file.getExportLinks().containsKey("image/png")) {
				DownloadResponse response = GoogleDriveUtils.exportFile(drive, file, "image/png");
				binaryRequestContext.setResponseContent(response.getData(), response.getMimeType());
			} else {
				DownloadResponse response = GoogleDriveUtils.downloadFile(drive, file);
				binaryRequestContext.setResponseContent(response.getData(), response.getMimeType());
			} 
		} catch (IOException e) {
      Messages messages = Messages.getInstance();
      Locale locale = binaryRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
		} catch (GeneralSecurityException e) {
      Messages messages = Messages.getInstance();
      Locale locale = binaryRequestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
		}
  }
}
