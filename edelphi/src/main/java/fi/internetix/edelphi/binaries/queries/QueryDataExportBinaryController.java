package fi.internetix.edelphi.binaries.queries;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.binaries.BinaryController;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.resources.QueryDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.resources.Query;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.utils.ActionUtils;
import fi.internetix.edelphi.utils.GoogleDriveUtils;
import fi.internetix.edelphi.utils.QueryDataUtils;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.edelphi.utils.QueryDataUtils.ReplierExportStrategy;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.BinaryRequestContext;

public class QueryDataExportBinaryController extends BinaryController {

  @Override
  public void process(BinaryRequestContext requestContext) {
    Long queryId = requestContext.getLong("queryId");
    Long stampId = requestContext.getLong("stampId");
    String replierExportStrategyParam = requestContext.getString("replierExportStrategy");

    QueryDAO queryDAO = new QueryDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    Query query = queryDAO.findById(queryId);
    PanelStamp panelStamp = panelStampDAO.findById(stampId);
    ExportFormat format = ExportFormat.valueOf(requestContext.getString("format"));
    // Default replier export strategy is user id hash
    ReplierExportStrategy replierExportStrategy = StringUtils.isNotBlank(replierExportStrategyParam) && ActionUtils.isSuperUser(requestContext)
        ? ReplierExportStrategy.valueOf(replierExportStrategyParam)
        : ReplierExportStrategy.HASH;

    switch (format) {
    case CSV:
      exportCsv(requestContext, replierExportStrategy, query, panelStamp);
      break;
    case GOOGLE_SPREADSHEET:
      exportGoogleSpreadsheet(requestContext, replierExportStrategy, query, panelStamp);
      break;
    }
  }

  private void exportCsv(BinaryRequestContext requestContext, ReplierExportStrategy replierExportStrategy, Query query, PanelStamp panelStamp) {
    try {
      byte[] csvData = QueryDataUtils.exportQueryDataAsCSV(requestContext.getRequest().getLocale(), replierExportStrategy, query, panelStamp);
      requestContext.setResponseContent(csvData, "text/csv");
      requestContext.setFileName(ResourceUtils.getUrlName(query.getName()) + ".csv");
    } catch (IOException e) {
      Messages messages = Messages.getInstance();
      Locale locale = requestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.QUERYDATAEXPORTFAILURE, messages.getText(locale, "exception.1014.queryDataExportFailure"), e);
    }

  }

  private void exportGoogleSpreadsheet(BinaryRequestContext requestContext, ReplierExportStrategy replierExportStrategy, Query query, PanelStamp panelStamp) {
    Drive drive = GoogleDriveUtils.getAuthenticatedService(requestContext);
    if (drive != null) {
      try {
        byte[] csvData = QueryDataUtils.exportQueryDataAsCSV(requestContext.getRequest().getLocale(), replierExportStrategy, query, panelStamp);
        String title = query.getName();
        File file = GoogleDriveUtils.insertFile(drive, title, "", null, "text/csv", csvData, true, 3);
        requestContext.setRedirectURL(file.getAlternateLink());
      } catch (Exception e) {
        Messages messages = Messages.getInstance();
        Locale locale = requestContext.getRequest().getLocale();
        throw new SmvcRuntimeException(EdelfoiStatusCode.QUERYDATAEXPORTFAILURE, messages.getText(locale, "exception.1014.queryDataExportFailure"), e);
      }
    }
  }

  private enum ExportFormat {
    CSV, GOOGLE_SPREADSHEET
  }
}
