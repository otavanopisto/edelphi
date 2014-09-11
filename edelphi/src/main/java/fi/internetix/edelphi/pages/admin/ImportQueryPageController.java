package fi.internetix.edelphi.pages.admin;

import org.apache.commons.fileupload.FileItem;

import fi.internetix.edelphi.DelfoiActionName;
import fi.internetix.edelphi.domainmodel.actions.DelfoiActionScope;
import fi.internetix.edelphi.pages.PageController;
import fi.internetix.edelphi.tools.DataImporter;
import fi.internetix.smvc.controllers.PageRequestContext;

public class ImportQueryPageController extends PageController {

  public ImportQueryPageController() {
    super();
    setAccessAction(DelfoiActionName.MANAGE_SYSTEM_SETTINGS, DelfoiActionScope.DELFOI);
  }

  @Override
  public void process(PageRequestContext pageRequestContext) {
    String method = pageRequestContext.getRequest().getMethod();
    if ("POST".equals(method)) {

      try {
      
        pageRequestContext.getResponse().setContentType("text/plain");

        Long queryId = pageRequestContext.getLong("queryId");
        FileItem emailMappingFile = pageRequestContext.getFile("emailMappingFile");
        FileItem fieldMappingFile = pageRequestContext.getFile("fieldMappingFile");
        FileItem commentMappingFile = pageRequestContext.getFile("commentMappingFile");
        FileItem queryDataFile = pageRequestContext.getFile("queryDataFile");
        FileItem matrixMappingFile = pageRequestContext.getFile("matrixMappingFile");
        String datePattern = pageRequestContext.getString("datePattern");
        String createdParameter = pageRequestContext.getString("createdParameter");
        String modifiedParameter = pageRequestContext.getString("modifiedParameter");

        DataImporter dataImporter = new DataImporter();
        dataImporter.setQueryId(queryId);
        dataImporter.setQueryDataFile(queryDataFile);
        dataImporter.setDelimiter(';');
        dataImporter.setDatePattern(datePattern);
        dataImporter.setCreatedParameter(createdParameter);
        dataImporter.setModifiedParameter(modifiedParameter);
        dataImporter.setEmailMappingFile(emailMappingFile);
        dataImporter.setFieldMappingFile(fieldMappingFile);
        dataImporter.setCommentMappingFile(commentMappingFile);
        dataImporter.setMatrixMappingFile(matrixMappingFile);

        dataImporter.doImport();

      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      pageRequestContext.setIncludeJSP("/jsp/pages/admin/importquery.jsp");
    }
  }

}
