package fi.internetix.edelphi.pages.panel;

import java.util.List;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.dao.resources.LocalDocumentDAO;
import fi.internetix.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.domainmodel.resources.LocalDocument;
import fi.internetix.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ManagePanelDocumentPageController extends PanelPageController {

  public ManagePanelDocumentPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_PANEL_MATERIALS, DelfoiActionScope.PANEL);
  }

  @Override
  public void processPageRequest(PageRequestContext pageRequestContext) {
    LocalDocumentDAO localDocumentDAO = new LocalDocumentDAO();
    LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO();

    if (!pageRequestContext.getBoolean("newDocument")) {
      Long documentId = pageRequestContext.getLong("documentId");
      LocalDocument document = localDocumentDAO.findById(documentId);
      List<LocalDocumentPage> documentPages = localDocumentPageDAO.listByDocument(document);
      
      pageRequestContext.getRequest().setAttribute("document", document);
      pageRequestContext.getRequest().setAttribute("documentId", document.getId());
      pageRequestContext.getRequest().setAttribute("documentPages", documentPages);
    } else {
      pageRequestContext.getRequest().setAttribute("documentId", "NEW");
    }

    ActionUtils.includeRoleAccessList(pageRequestContext);
    pageRequestContext.setIncludeJSP("/jsp/panels/managepaneldocument.jsp");
  }
}
