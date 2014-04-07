package fi.internetix.edelphi.binaries.queries;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.binaries.BinaryController;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.querydata.QueryReplyDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querydata.QueryReply;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReplyFilter;
import fi.internetix.edelphi.pages.panel.admin.report.util.QueryReportChartContext;
import fi.internetix.edelphi.utils.GoogleDriveUtils;
import fi.internetix.edelphi.utils.QueryDataUtils;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.edelphi.utils.QueryDataUtils.ReplierExportStrategy;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.BinaryRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class QueryPageDataExportBinaryController extends BinaryController {

  @Override
  public void process(BinaryRequestContext requestContext) {
    Long queryPageId = requestContext.getLong("queryPageId");
    Long stampId = requestContext.getLong("stampId");
    String replierExportStrategyParam = requestContext.getString("replierExportStrategy");

    QueryReportChartContext reportContext = null;
    String serializedContext = requestContext.getString("serializedContext");
    if (serializedContext != null) {
      try {
        ObjectMapper om = new ObjectMapper();
        byte[] serializedData = Base64.decodeBase64(serializedContext);
        String stringifiedData = new String(serializedData, "UTF-8");
        reportContext = om.readValue(stringifiedData, QueryReportChartContext.class); 
      }
      catch (Exception e) {
      }
    }

    QueryPageDAO queryPageDAO = new QueryPageDAO();
    QueryReplyDAO queryReplyDAO = new QueryReplyDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();
    QueryPage queryPage = queryPageDAO.findById(queryPageId);
    PanelStamp panelStamp = panelStampDAO.findById(stampId);
    ExportFormat format = ExportFormat.valueOf(requestContext.getString("format")); 
    ReplierExportStrategy replierExportStrategy = StringUtils.isNotBlank(replierExportStrategyParam) ? ReplierExportStrategy.valueOf(replierExportStrategyParam) : ReplierExportStrategy.NONE;

    // Query replies, possibly filtered
    
    List<QueryReply> replies = queryReplyDAO.listByQueryAndStampAndArchived(queryPage.getQuerySection().getQuery(), panelStamp, Boolean.FALSE);
    List<QueryReplyFilter> filters = reportContext == null ? null : QueryReplyFilter.parseFilters(reportContext.getFilters());
    if (filters != null) {
      for (QueryReplyFilter filter : filters) {
        replies = filter.filterList(replies);
      }
    }
    
    switch (format) {
      case CSV:
        exportCsv(requestContext, replierExportStrategy, replies, queryPage, panelStamp);
      break;
      case GOOGLE_SPREADSHEET:
        exportGoogleSpreadsheet(requestContext, replierExportStrategy, replies, queryPage, panelStamp);
      break;
    }
  }
  
  private void exportCsv(BinaryRequestContext requestContext, ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, QueryPage queryPage, PanelStamp panelStamp) {
    try {
      byte[] csvData = QueryDataUtils.exportQueryPageDataAsCsv(requestContext.getRequest().getLocale(), replierExportStrategy, replies, queryPage, panelStamp);
      requestContext.setResponseContent(csvData, "text/csv");
      requestContext.setFileName(ResourceUtils.getUrlName(queryPage.getTitle()) + ".csv");
    } catch (IOException e) {
      Messages messages = Messages.getInstance();
      Locale locale = requestContext.getRequest().getLocale();
      throw new SmvcRuntimeException(EdelfoiStatusCode.QUERYDATAEXPORTFAILURE, messages.getText(locale, "exception.1014.queryDataExportFailure"), e);
    }
     
  }
  
  private void exportGoogleSpreadsheet(RequestContext requestContext, ReplierExportStrategy replierExportStrategy, List<QueryReply> replies, QueryPage queryPage, PanelStamp panelStamp) {
		Drive drive = GoogleDriveUtils.getAuthenticatedService(requestContext);
		if (drive != null) {
  		try {
  		  String title = queryPage.getQuerySection().getQuery().getName() + " - " + queryPage.getTitle();
  			byte[] csvData = QueryDataUtils.exportQueryPageDataAsCsv(requestContext.getRequest().getLocale(), replierExportStrategy, replies, queryPage, panelStamp);
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
    CSV,
    GOOGLE_SPREADSHEET
  }
}
