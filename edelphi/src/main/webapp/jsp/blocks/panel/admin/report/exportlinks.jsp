<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<div class="exportLinks">
  <c:choose>
    <c:when test="${queryPageId gt 0}">
      <h4>
        <fmt:message key="panel.admin.report.options.downloadOrExportExportContentsTitle" />
      </h4>
      <a href="${pageContext.request.contextPath}/queries/exportreportpage.binary?format=PDF&amp;serializedContext=${serializedContext}&amp;queryPageId=${queryPageId}&amp;stampId=${stampId}"
        class="modalPopupLinkContainer exportPDF"><fmt:message key="panel.admin.report.options.downloadOrExportExportContentsPDF" /></a>
      <a href="${pageContext.request.contextPath}/queries/exportreportpage.binary?format=GOOGLE_DOCUMENT&amp;serializedContext=${serializedContext}&amp;queryPageId=${queryPageId}&amp;stampId=${stampId}"
        class="modalPopupLinkContainer exportGoogleDrive" target="_blank"><fmt:message key="panel.admin.report.options.downloadOrExportExportContentsGoogleDocument" /></a>
      <h4>
        <fmt:message key="panel.admin.report.options.downloadOrExportExportChartsTitle" />
      </h4>
      <a href="${pageContext.request.contextPath}/queries/exportreportpage.binary?format=PNG_ZIP&amp;serializedContext=${serializedContext}&amp;queryPageId=${queryPageId}&amp;stampId=${stampId}"
        class="modalPopupLinkContainer exportPDF"><fmt:message key="panel.admin.report.options.downloadOrExportExportChartsPNG" /></a>
      <a href="${pageContext.request.contextPath}/queries/exportreportpage.binary?format=SVG_ZIP&amp;serializedContext=${serializedContext}&amp;queryPageId=${queryPageId}&amp;stampId=${stampId}"
        class="modalPopupLinkContainer exportPDF"><fmt:message key="panel.admin.report.options.downloadOrExportExportChartsSVG" /></a>
      <a href="${pageContext.request.contextPath}/queries/exportreportpage.binary?format=GOOGLE_IMAGES&amp;serializedContext=${serializedContext}&amp;queryPageId=${queryPageId}&amp;stampId=${stampId}"
        class="modalPopupLinkContainer exportGoogleDrive" target="_blank"><fmt:message key="panel.admin.report.options.downloadOrExportExportChartsGoogle" /></a>
      <h4>
        <fmt:message key="panel.admin.report.options.downloadOrExportExportDataTitle" />
      </h4>
      <a href="${pageContext.request.contextPath}/queries/exportpagedata.binary?queryPageId=${queryPageId}&amp;serializedContext=${serializedContext}&amp;stampId=${stampId}&amp;format=CSV"
        class="modalPopupLinkContainer exportCSV"><fmt:message key="panel.admin.report.options.downloadOrExportExportDataCSV" /></a>
      <a href="${pageContext.request.contextPath}/queries/exportpagedata.binary?queryPageId=${queryPageId}&amp;serializedContext=${serializedContext}&amp;stampId=${stampId}&amp;format=GOOGLE_SPREADSHEET"
        class="modalPopupLinkContainer exportGoogle" target="_blank"><fmt:message key="panel.admin.report.options.downloadOrExportExportDataGoogleSpreadsheet" /></a>
    </c:when>
    <c:otherwise>
      <h4>
        <fmt:message key="panel.admin.report.options.downloadOrExportExportContentsTitle" />
      </h4>
      <a href="${pageContext.request.contextPath}/queries/exportreport.binary?format=PDF&amp;serializedContext=${serializedContext}&amp;queryId=${queryId}&amp;stampId=${stampId}"
        class="modalPopupLinkContainer exportPDF"><fmt:message key="panel.admin.report.options.downloadOrExportExportContentsPDF" /></a>
      <a href="${pageContext.request.contextPath}/queries/exportreport.binary?format=GOOGLE_DOCUMENT&amp;serializedContext=${serializedContext}&amp;queryId=${queryId}&amp;stampId=${stampId}"
        class="modalPopupLinkContainer exportGoogleDrive" target="_blank"><fmt:message key="panel.admin.report.options.downloadOrExportExportContentsGoogleDocument" /></a>
      <h4>
        <fmt:message key="panel.admin.report.options.downloadOrExportExportChartsTitle" />
      </h4>
      <a href="${pageContext.request.contextPath}/queries/exportreport.binary?format=PNG_ZIP&amp;serializedContext=${serializedContext}&amp;queryId=${queryId}&amp;stampId=${stampId}"
        class="modalPopupLinkContainer exportPDF"><fmt:message key="panel.admin.report.options.downloadOrExportExportChartsPNG" /></a>
      <a href="${pageContext.request.contextPath}/queries/exportreport.binary?format=SVG_ZIP&amp;serializedContext=${serializedContext}&amp;queryId=${queryId}&amp;stampId=${stampId}"
        class="modalPopupLinkContainer exportPDF"><fmt:message key="panel.admin.report.options.downloadOrExportExportChartsSVG" /></a>
      <a href="${pageContext.request.contextPath}/queries/exportreport.binary?format=GOOGLE_IMAGES&amp;serializedContext=${serializedContext}&amp;queryId=${queryId}&amp;stampId=${stampId}"
        class="modalPopupLinkContainer exportGoogleDrive" target="_blank"><fmt:message key="panel.admin.report.options.downloadOrExportExportChartsGoogle" /></a>
      <h4>
        <fmt:message key="panel.admin.report.options.downloadOrExportExportDataTitle" />
      </h4>
      <a href="${pageContext.request.contextPath}/queries/exportdata.binary?panelId=${panelId}&amp;serializedContext=${serializedContext}&amp;queryId=${queryId}&amp;stampId=${stampId}&amp;format=CSV"
        class="modalPopupLinkContainer exportCSV"><fmt:message key="panel.admin.report.options.downloadOrExportExportDataCSV" /></a>
      <a
        href="${pageContext.request.contextPath}/queries/exportdata.binary?panelId=${panelId}&amp;serializedContext=${serializedContext}&amp;queryId=${queryId}&amp;stampId=${stampId}&amp;format=GOOGLE_SPREADSHEET"
        class="modalPopupLinkContainer exportGoogle" target="_blank"><fmt:message key="panel.admin.report.options.downloadOrExportExportDataGoogleSpreadsheet" /></a>
    </c:otherwise>
  </c:choose>
</div>