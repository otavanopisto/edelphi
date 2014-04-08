package fi.internetix.edelphi.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.xml.sax.SAXException;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import fi.internetix.edelphi.i18n.Messages;
import fi.internetix.smvc.controllers.RequestContext;
import fi.internetix.smvc.logging.Logging;

public class ReportUtils {

  public static void zipCharts(RequestContext requestContext, OutputStream outputStream, String imageFormat, URL url) throws IOException, ParserConfigurationException, SAXException,
      TransformerException {

    ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

    String hostUrl = new StringBuilder().append(url.getProtocol()).append("://").append(url.getHost()).append(':').append(url.getPort()).toString();

    // Read the url...

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("Authorization", "InternalAuthorization " + SystemUtils.getSettingValue("system.internalAuthorizationHash"));
    connection.setRequestProperty("Cookie", "JSESSIONID=" + requestContext.getRequest().getSession().getId());
    connection.setRequestMethod("GET");
    connection.setReadTimeout(900000); // 15 minutes; gross overkill but at least eventual termination is guaranteed
    connection.connect();
    InputStream is = connection.getInputStream();
    
    String html = null;
    try {
      html = StreamUtils.readStreamToString(is, "UTF-8");
    }
    finally {
      if (is != null) {
        try {
          is.close();
        }
        catch (IOException ioe) {
          Logging.logException(ioe);
        }
      }
      connection.disconnect();
    }

    // ...tidy it...

    ByteArrayOutputStream tidyXHtml = new ByteArrayOutputStream();
    Tidy tidy = new Tidy();
    tidy.setInputEncoding("UTF-8");
    tidy.setOutputEncoding("UTF-8");
    tidy.setShowWarnings(true);
    tidy.setNumEntities(false);
    tidy.setXmlOut(true);
    tidy.setXHTML(true);
    tidy.setWraplen(0);
    tidy.setQuoteNbsp(false);
    tidy.parse(new StringReader(html), tidyXHtml);

    // ...parse it into a DOM...

    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    builderFactory.setNamespaceAware(false);
    builderFactory.setValidating(false);
    builderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
    builderFactory.setFeature("http://xml.org/sax/features/validation", false);
    builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    DocumentBuilder builder = builderFactory.newDocumentBuilder();
    ByteArrayInputStream inputStream = new ByteArrayInputStream(tidyXHtml.toByteArray());
    Document document = builder.parse(inputStream);

    // ...and zip its chart images

    if ("SVG".equals(imageFormat)) {
      NodeList svgObjectList = XPathAPI.selectNodeList(document, "//object");
      for (int i = 0, l = svgObjectList.getLength(); i < l; i++) {
        Element svgObjectElement = (Element) svgObjectList.item(i);
        if ("image/svg+xml".equals(svgObjectElement.getAttribute("type"))) {
          String svgUri = svgObjectElement.getAttribute("data");
          if (StringUtils.startsWith(svgUri, "/")) {
            svgUri = hostUrl + svgUri;
          }
          
          byte[] svgContent = downloadUrlAsByteArray(svgUri);
          ZipEntry zipEntry = new ZipEntry(String.format("%03d", i + 1) + ".svg");
          zipOutputStream.putNextEntry(zipEntry);
          zipOutputStream.write(svgContent);
        }
      }
    }
    else {
      NodeList imgList = XPathAPI.selectNodeList(document, "//img");
      for (int i = 0, l = imgList.getLength(); i < l; i++) {
        Element imgElement = (Element) imgList.item(i);
        // TODO This assumes the image source is not relative
        String imgUrl = imgElement.getAttribute("src");
        if (StringUtils.startsWith(imgUrl, "/")) {
          imgUrl = hostUrl + imgUrl;
        }
        ZipEntry zipEntry = new ZipEntry(String.format("%03d", i + 1) + ".png");
        zipOutputStream.putNextEntry(zipEntry);
        zipOutputStream.write(downloadUrlAsByteArray(imgUrl));
      }
    }
    zipOutputStream.finish();
  }

  public static File uploadReportToGoogleDrive(RequestContext requestContext, Drive drive, URL url, String queryName, int retryCount, boolean imagesOnly) throws IOException,
      TransformerException, ParserConfigurationException, SAXException {
    Logging.logInfo("Exporting report into Google Drive from " + url);

    File exportTempFolder = GoogleDriveUtils.insertFolder(drive, queryName, "", null, 3);
    Set<File> tempFiles = new HashSet<File>();
    try {
      // Resolve host URL to help with embedding of styles and images

      String hostUrl = new StringBuilder().append(url.getProtocol()).append("://").append(url.getHost()).append(':').append(url.getPort()).toString();

      // First we need to fetch report as html

      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Cookie", "JSESSIONID=" + requestContext.getRequest().getSession().getId());
      connection.setRequestProperty("Authorization", "InternalAuthorization " + SystemUtils.getSettingValue("system.internalAuthorizationHash"));
      connection.setRequestMethod("GET");
      connection.setReadTimeout(900000); // 15 minutes; gross overkill but at least eventual termination is guaranteed
      connection.connect();
      InputStream is = connection.getInputStream(); 

      String reportHtml = null;
      try {
        reportHtml = StreamUtils.readStreamToString(is, "UTF-8");
      }
      finally {
        if (is != null) {
          try {
            is.close();
          }
          catch (IOException ioe) {
            Logging.logException(ioe);
          }
        }
        connection.disconnect();
      }

      // .. tidy it a bit

      ByteArrayOutputStream tidyXHtml = new ByteArrayOutputStream();
      Tidy tidy = new Tidy();
      tidy.setInputEncoding("UTF-8");
      tidy.setOutputEncoding("UTF-8");
      tidy.setShowWarnings(true);
      tidy.setNumEntities(false);
      tidy.setXmlOut(true);
      tidy.setXHTML(true);
      tidy.setWraplen(0);
      tidy.setQuoteNbsp(false);
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

      // Tidied document needs to be parsed into a document, so we can do some
      // changes into it
      Document reportDocument = builder.parse(inputStream);

      // Google Drive can not access our style sheets, so we need to embed them
      // directly into the document
      NodeList linkList = XPathAPI.selectNodeList(reportDocument, "//link");
      for (int i = 0, l = linkList.getLength(); i < l; i++) {
        Element linkElement = (Element) linkList.item(i);
        String linkRel = linkElement.getAttribute("rel");
        if (StringUtils.equalsIgnoreCase(linkRel, "stylesheet")) {
          String href = linkElement.getAttribute("href");
          Logging.logInfo("Embedding css from " + href + " into Google report");
          String cssText = CSSUtils.downloadCSS(hostUrl + href, true).replaceAll("[\n\r]", " ");

          Node parent = linkElement.getParentNode();
          Element styleElement = reportDocument.createElement("style");
          styleElement.setAttribute("type", "text/css");
          styleElement.appendChild(reportDocument.createTextNode(cssText));

          parent.replaceChild(styleElement, linkElement);
        }
      }

      // Then we need to transform SVG images into Google Drawings
      NodeList svgObjectList = XPathAPI.selectNodeList(reportDocument, "//object");
      for (int i = 0, l = svgObjectList.getLength(); i < l; i++) {
        Element svgObjectElement = (Element) svgObjectList.item(i);

        if ("image/svg+xml".equals(svgObjectElement.getAttribute("type"))) {
          String svgUri = svgObjectElement.getAttribute("data");
          
          if (StringUtils.startsWith(svgUri, "/")) {
            svgUri = hostUrl + svgUri;
          }
          
          byte[] svgContent = downloadUrlAsByteArray(svgUri);
          // Google Drive does not support SVG so we need to convert them into
          // another format.
          byte[] emfData = SVGUtils.convertSvgToEmf(svgContent);

          // Google Drive does not accept EMF but it accepts WMF, so we need to
          // tell it to handle this file as WMF instead of EMF
          String chartTitle = Messages.getInstance().getText(requestContext.getRequest().getLocale(), "panel.admin.report.googleReport.chartTitle", new Object[] { queryName, i + 1 });

          File chartFile = GoogleDriveUtils.insertFile(drive, chartTitle, "", exportTempFolder.getId(), "application/x-msmetafile", emfData, true, retryCount);
          if (chartFile != null) {
            // If file uploading was a success we publish it with the link
            GoogleDriveUtils.publishFileWithLink(drive, chartFile);

            if (!imagesOnly) {
              // After that, we replace object -tag with img -tag that points to
              // image png export url.
              String googleLink = chartFile.getExportLinks().get("image/png");
              Node parent = svgObjectElement.getParentNode();
              Element imageElement = reportDocument.createElement("img");
              imageElement.setAttribute("src", googleLink);

              parent.replaceChild(imageElement, svgObjectElement);

              tempFiles.add(chartFile);
            }

            Logging.logInfo("SVG file from " + svgUri + " uploaded into Google Drive with id: " + chartFile.getId());
          }
          else {
            Logging.logInfo("Uploading failed to Google Drive for SVG file: " + svgUri);
          }
        }
      }

      if (!imagesOnly) {

        // After document has been altered to fit the purpose, we just serialize
        // it back to html

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();
        StreamResult streamResult = new StreamResult(resultStream);
        transformer.transform(new DOMSource(reportDocument), streamResult);

        resultStream.flush();
        resultStream.close();

        // And upload the final product into Google Drive

        byte[] documentContent = resultStream.toByteArray();

        File file = GoogleDriveUtils.insertFile(drive, queryName, "", null, "text/html", documentContent, true, retryCount);

        Logging.logInfo("Report exported into Google Drive from " + url + " with id " + file.getId());

        return file;
      }
      else {
        return exportTempFolder;
      }
    }
    finally {
      if (!imagesOnly) {
        Logging.logInfo("Cleaning temporary files");

        for (File tempFile : tempFiles) {
          Logging.logInfo("Deleting export temp file " + tempFile.getId());
          GoogleDriveUtils.deleteFile(drive, tempFile);
        }

        Logging.logInfo("Deleting export temp folder " + exportTempFolder.getId());
        GoogleDriveUtils.deleteFile(drive, exportTempFolder);
      }
    }
  }

  private static byte[] downloadUrlAsByteArray(String urlString) throws IOException {
    if (StringUtils.startsWith(urlString,  "data:")) {
      int base64Index = urlString.indexOf("base64,");
      return Base64.decodeBase64(urlString.substring(base64Index + 7));
    }
    else {
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestProperty("Authorization", "InternalAuthorization " + SystemUtils.getSettingValue("system.internalAuthorizationHash"));
      connection.setRequestMethod("GET");
      connection.setReadTimeout(900000); // 15 minutes; gross overkill but at least eventual termination is guaranteed
      connection.connect();
      InputStream is = connection.getInputStream();
      byte[] urlContent = null;
      try {
        urlContent = StreamUtils.readStreamToByteArray(is); 
      }
      finally {
        if (is != null) {
          try {
            is.close();
          }
          catch (IOException ioe) {
            Logging.logException(ioe);
          }
        }
        connection.disconnect();
      }
      return urlContent;
    }
  }

}
