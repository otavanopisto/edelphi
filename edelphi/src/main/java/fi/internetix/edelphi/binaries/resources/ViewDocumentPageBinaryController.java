package fi.internetix.edelphi.binaries.resources;

import java.io.UnsupportedEncodingException;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.binaries.BinaryController;
import fi.internetix.edelphi.dao.resources.DocumentDAO;
import fi.internetix.edelphi.dao.resources.LocalDocumentPageDAO;
import fi.internetix.edelphi.domainmodel.resources.Document;
import fi.internetix.edelphi.domainmodel.resources.LocalDocument;
import fi.internetix.edelphi.domainmodel.resources.LocalDocumentPage;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.BinaryRequestContext;

public class ViewDocumentPageBinaryController extends BinaryController {

  @Override
  public void process(BinaryRequestContext binaryRequestContext) {
    DocumentDAO documentDAO = new DocumentDAO();
    
    Long documentId = binaryRequestContext.getLong("documentId");
    Integer pageNumber = binaryRequestContext.getInteger("pageNumber");

    Document document = documentDAO.findById(documentId);
    if (document instanceof LocalDocument) {
      // TODO: test with instrumented domain model
      handleLocalDocument((LocalDocument) document, pageNumber, binaryRequestContext);
    }
  }
  
  private void handleLocalDocument(LocalDocument localDocument, Integer pageNumber, BinaryRequestContext binaryRequestContext) {
    LocalDocumentPageDAO localDocumentPageDAO = new LocalDocumentPageDAO();
    LocalDocumentPage localDocumentPage = localDocumentPageDAO.findByDocumentAndPageNumber(localDocument, pageNumber);

    try {
      binaryRequestContext.setResponseContent(localDocumentPage.getContent().getBytes("UTF-8"), "text/html");
    }
    catch (UnsupportedEncodingException e) {
      throw new SmvcRuntimeException(EdelfoiStatusCode.UNDEFINED, e.getLocalizedMessage(), e);
    }
  }
}
