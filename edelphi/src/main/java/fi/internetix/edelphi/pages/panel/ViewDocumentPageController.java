package fi.internetix.edelphi.pages.panel;

import java.util.Locale;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.dao.resources.DocumentDAO;
import fi.internetix.edelphi.dao.resources.LocalDocumentDAO;
import fi.internetix.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.panels.Panel;
import fi.internetix.edelphi.domainmodel.resources.Document;
import fi.internetix.edelphi.domainmodel.resources.GoogleDocument;
import fi.internetix.edelphi.domainmodel.resources.LocalDocument;
import fi.internetix.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.GoogleDriveUtils;
import fi.internetix.edelphi.utils.GoogleDriveUtils.DownloadResponse;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.smvc.PageNotFoundException;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ViewDocumentPageController extends PanelPageController {

	public ViewDocumentPageController() {
		super();
		setAccessAction(DelfoiActionName.ACCESS_PANEL, DelfoiActionScope.PANEL);
	}

	@Override
	public void processPageRequest(PageRequestContext pageRequestContext) {
		// TODO: If query is hidden only users with manage material rights should be able to enter
		DocumentDAO documentDAO = new DocumentDAO();
		LocalDocumentDAO localDocumentDAO = new LocalDocumentDAO();
		LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO();

		Panel panel = RequestUtils.getPanel(pageRequestContext);
		if (panel == null) {
			throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
		}

		Long documentId = pageRequestContext.getLong("documentId");
		Document document = documentDAO.findById(documentId);
		if (document == null) {
			throw new PageNotFoundException(pageRequestContext.getRequest().getLocale());
		}
		Integer page = pageRequestContext.getInteger("page");
		if (page == null) {
			page = 0;
		}

		if (document instanceof LocalDocument) {
			LocalDocument localDocument = localDocumentDAO.findById(documentId);
			LocalDocumentPage localDocumentPage = localDocumentPageDAO.findByDocumentAndPageNumber(localDocument, page);
			Long pageCount = localDocumentDAO.countByDocument(localDocument);
			pageRequestContext.getRequest().setAttribute("title", localDocumentPage.getTitle());
			pageRequestContext.getRequest().setAttribute("content", localDocumentPage.getContent());
			pageRequestContext.getRequest().setAttribute("page", page);
			pageRequestContext.getRequest().setAttribute("pageCount", pageCount);
		} else if (document instanceof GoogleDocument) {
			try {
				GoogleDocument googleDocument = (GoogleDocument) document;

				Drive drive = GoogleDriveUtils.getAdminService();
				File file = GoogleDriveUtils.getFile(drive, googleDocument.getResourceId());
				if ("application/vnd.google-apps.document".equals(file.getMimeType())) {
			    DownloadResponse response = GoogleDriveUtils.exportFile(drive, file, "text/html");
					String content = GoogleDriveUtils.extractGoogleDocumentContent(response.getData());
					String styleSheet = GoogleDriveUtils.extractGoogleDocumentStyleSheet(response.getData());
					pageRequestContext.getRequest().setAttribute("content", content);
					pageRequestContext.getRequest().setAttribute("styleSheet", styleSheet);
				} else if ("application/vnd.google-apps.spreadsheet".equals(file.getMimeType())) {
			    DownloadResponse response = GoogleDriveUtils.exportSpreadsheet(drive, file);
					String content = GoogleDriveUtils.extractGoogleDocumentContent(response.getData());
					String styleSheet = GoogleDriveUtils.extractGoogleDocumentStyleSheet(response.getData());
					pageRequestContext.getRequest().setAttribute("content", content);
					pageRequestContext.getRequest().setAttribute("styleSheet", styleSheet);
				} else {
					pageRequestContext.setRedirectURL(pageRequestContext.getRequest().getContextPath() + "/resources/viewdocument.binary?documentId=" + documentId);
				}
			} catch (Exception e) {
				Messages messages = Messages.getInstance();
				Locale locale = pageRequestContext.getRequest().getLocale();
				throw new SmvcRuntimeException(EdelfoiStatusCode.GOOGLE_DOCS_FAILURE, messages.getText(locale, "exception.1012.googleDocsFailure"), e);
			}
		}

		pageRequestContext.getRequest().setAttribute("panel", panel);
		pageRequestContext.getRequest().setAttribute("type", document.getType());
		pageRequestContext.getRequest().setAttribute("name", document.getName());
		pageRequestContext.getRequest().setAttribute("fullPath", document.getFullPath());

		pageRequestContext.setIncludeJSP("/jsp/pages/panel/viewdocument.jsp");
	}

}
