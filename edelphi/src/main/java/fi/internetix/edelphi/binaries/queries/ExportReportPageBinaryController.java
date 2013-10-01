package fi.internetix.edelphi.binaries.queries;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.internetix.edelphi.EdelfoiStatusCode;
import fi.internetix.edelphi.binaries.BinaryController;
import fi.internetix.edelphi.dao.panels.PanelStampDAO;
import fi.internetix.edelphi.dao.querylayout.QueryPageDAO;
import fi.internetix.edelphi.domainmodel.panels.PanelStamp;
import fi.internetix.edelphi.domainmodel.querylayout.QueryPage;
import fi.internetix.edelphi.utils.GoogleDriveUtils;
import fi.internetix.edelphi.utils.ReportUtils;
import fi.internetix.edelphi.utils.RequestUtils;
import fi.internetix.edelphi.utils.ResourceUtils;
import fi.internetix.edelphi.utils.StreamUtils;
import fi.internetix.edelphi.utils.SystemUtils;
import fi.internetix.smvc.SmvcRuntimeException;
import fi.internetix.smvc.controllers.BinaryRequestContext;
import fi.internetix.smvc.controllers.RequestContext;

public class ExportReportPageBinaryController extends BinaryController {

  @Override
  public void process(BinaryRequestContext requestContext) {
    Long queryPageId = requestContext.getLong("queryPageId");
    Long stampId = requestContext.getLong("stampId");

    // By default the whole query data is being output
    Boolean isFiltered = requestContext.getBoolean("useFilters");
    isFiltered = isFiltered != null ? isFiltered : Boolean.FALSE;
    
    QueryPageDAO queryPageDAO = new QueryPageDAO();
    PanelStampDAO panelStampDAO = new PanelStampDAO();

    QueryPage queryPage = queryPageDAO.findById(queryPageId);
    PanelStamp panelStamp = panelStampDAO.findById(stampId);
    ExportFormat format = ExportFormat.valueOf(requestContext.getString("format"));

    switch (format) {
    case GOOGLE_DOCUMENT:
      exportGoogleDocument(requestContext, queryPage, panelStamp, false, isFiltered);
      break;
    case GOOGLE_IMAGES:
      exportGoogleDocument(requestContext, queryPage, panelStamp, true, isFiltered);
      break;
    case PDF:
      exportPdf(requestContext, queryPage, panelStamp, isFiltered);
      break;
    case PNG_ZIP:
      exportChartZip(requestContext, "PNG", queryPage, panelStamp, isFiltered);
      break;
    case SVG_ZIP:
      exportChartZip(requestContext, "SVG", queryPage, panelStamp, isFiltered);
      break;
    }
  }

  public void exportGoogleDocument(RequestContext requestContext, QueryPage queryPage, PanelStamp panelStamp, boolean imagesOnly, Boolean useFilters) {
    Drive drive = GoogleDriveUtils.getAuthenticatedService(requestContext);
    if (drive != null) {
      try {
        String baseUrl = RequestUtils.getBaseUrl(requestContext.getRequest());
        URL url = new URL(baseUrl + "/panel/admin/report/page.page?chartFormat=SVG&pageId=" + queryPage.getId() + "&panelId=" + panelStamp.getPanel().getId() + "&useFilters=" + useFilters.toString());
        String title = queryPage.getQuerySection().getQuery().getName() + " - " + queryPage.getTitle();
        File file = ReportUtils.uploadReportToGoogleDrive(requestContext, drive, url, title, 3, imagesOnly);
        requestContext.setRedirectURL(file.getAlternateLink());
      }
      catch (IOException e) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.REPORT_GOOGLE_DRIVE_EXPORT_FAILED, "exception.1031.reportGoogleDriveExportFailed", e);
      }
      catch (ParserConfigurationException e) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.REPORT_GOOGLE_DRIVE_EXPORT_FAILED, "exception.1031.reportGoogleDriveExportFailed", e);
      }
      catch (SAXException e) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.REPORT_GOOGLE_DRIVE_EXPORT_FAILED, "exception.1031.reportGoogleDriveExportFailed", e);
      }
      catch (TransformerException e) {
        throw new SmvcRuntimeException(EdelfoiStatusCode.REPORT_GOOGLE_DRIVE_EXPORT_FAILED, "exception.1031.reportGoogleDriveExportFailed", e);
      }
    }
  }

  public void exportPdf(BinaryRequestContext requestContext, QueryPage queryPage, PanelStamp panelStamp, Boolean useFilters) {
    try {
      String baseURL = RequestUtils.getBaseUrl(requestContext.getRequest());

      URL url = new URL(baseURL + "/panel/admin/report/page.page?chartFormat=PNG&pageId=" + queryPage.getId() + "&panelId=" + panelStamp.getPanel().getId() + "&useFilters=" + useFilters.toString());
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Cookie", "JSESSIONID=" + requestContext.getRequest().getSession().getId());
      connection.setRequestProperty("Authorization", "InternalAuthorization " + SystemUtils.getSettingValue("system.internalAuthorizationHash"));
      connection.setRequestMethod("GET");
      connection.setReadTimeout(900000); // 15 minutes; gross overkill but at least eventual termination is guaranteed
      connection.connect();

      String reportHtml = StreamUtils.readStreamToString(connection.getInputStream(), "UTF-8");
      ByteArrayOutputStream tidyXHtml = new ByteArrayOutputStream();
      Tidy tidy = new Tidy();
      tidy.setInputEncoding("UTF-8");
      tidy.setOutputEncoding("UTF-8");
      tidy.setShowWarnings(true);
      tidy.setNumEntities(false);
      tidy.setXmlOut(true);
      tidy.setXHTML(true);
      tidy.parse(new StringReader(reportHtml), tidyXHtml);

      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      builderFactory.setNamespaceAware(false);
      builderFactory.setValidating(false);
      builderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
      builderFactory.setFeature("http://xml.org/sax/features/validation", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
      builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      DocumentBuilder builder = builderFactory.newDocumentBuilder();

      ByteArrayInputStream inputStream = new ByteArrayInputStream(tidyXHtml.toByteArray());
      Document doc = builder.parse(inputStream);
      ITextRenderer renderer = new ITextRenderer();

      renderer.setDocument(doc, baseURL + "/panel/admin/report/query/");
      renderer.layout();

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      renderer.createPDF(outputStream);
      outputStream.close();

      requestContext.setResponseContent(outputStream.toByteArray(), "application/pdf");
      requestContext.setFileName(ResourceUtils.getUrlName(queryPage.getTitle()) + ".pdf");

    }
    catch (Exception ex) {
      // TODO: Proper error handling
      ex.printStackTrace();
    }
  }

  public void exportChartZip(BinaryRequestContext requestContext, String imageFormat, QueryPage queryPage, PanelStamp panelStamp, Boolean useFilters) {
    try {
      String baseURL = RequestUtils.getBaseUrl(requestContext.getRequest());
      URL url = new URL(baseURL + "/panel/admin/report/page.page?chartFormat=" + imageFormat + "&pageId=" + queryPage.getId()
          + "&panelId=" + panelStamp.getPanel().getId() + "&useFilters=" + useFilters.toString());
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      try {
        ReportUtils.zipCharts(requestContext, outputStream, imageFormat, url);
      }
      finally {
        outputStream.close();
      }
      requestContext.setResponseContent(outputStream.toByteArray(), "application/zip");
      requestContext.setFileName(ResourceUtils.getUrlName(queryPage.getTitle()) + ".zip");
    }
    catch (Exception e) {
      // TODO: Proper error handling
      e.printStackTrace();
    }
  }

  private enum ExportFormat {
    PDF, GOOGLE_DOCUMENT, PNG_ZIP, SVG_ZIP, GOOGLE_IMAGES
  }
}
