<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="panelAdmin.block.queryResults.pageTitle" /></title>
<jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/comparereports.js"></script>
<link href="${pageContext.request.contextPath}/_themes/${theme}/css/comparereports.css" rel="stylesheet" />
</head>
<body class="panel_admin_compare">

  <div class="GUI_pageWrapper">

    <div class="GUI_pageContainer">

      <div class="pageTitle">
        <h1>${panel.name}</h1>
      </div>

      <div class="GUI_reportContainer">

        <form name="leftSettingsForm">
          <input type="hidden" name="panelId" value="${panel.id}" />

          <div class="GUI_selectedQueryReportWrapperLeft">
            <div class="selectedQueryReportTitle">
              <select name="queryId">
                <option value=""><fmt:message key="panel.admin.report.options.selectQueryTitle" /></option>
                <c:forEach var="query" items="${queries}">
                  <option value="${query.id}">${query.name}</option>
                </c:forEach>
              </select>
            </div>
            <div class="selectedQueryReportActions">
            
              <!-- Filters -->
            
              <div class="selectedQueryReportActions-filters">
                <div class="selectedQueryReportActions-filters-icon settingsIcon"></div>
                <div class="selectedQueryReportActions-filters-container settingsContainer">

                  <div class="reportOptionsContainer">
                    <input type="hidden" name="stampId" value="${panel.currentStamp.id}"/>
                  </div>

                  <jsp:include page="/jsp/fragments/formfield_button.jsp">
                    <jsp:param name="name" value="applySettings" />
                    <jsp:param name="labelLocale" value="panel.admin.report.options.filterTitle" />
                  </jsp:include>

                </div>
              </div>
              
              <!-- Exports -->
              
              <div class="selectedQueryReportActions-exports">
                <div class="selectedQueryReportActions-exports-icon settingsIcon"></div>
                <div class="selectedQueryReportActions-exports-container settingsContainer">
                </div>
              </div>
              
              <!-- Other options -->
              
              <div class="selectedQueryReportActions-settings">
                <div class="selectedQueryReportActions-settings-icon settingsIcon"></div>
                <div class="selectedQueryReportActions-settings-container settingsContainer">
                  <div class="otherOptionsContainer">
                    <div class="otherOptions">
                      <div class="queryOtherOptionsFilter">
                        <h3>
                          <fmt:message key="panel.admin.report.options.otherOptionsTitle" />
                        </h3>
                        <jsp:include page="/jsp/fragments/formfield_checkbox.jsp">
                          <jsp:param name="name" value="show2dAs1d" />
                          <jsp:param name="labelLocale" value="panel.admin.report.options.show2dAs1d" />
                          <jsp:param name="value" value="true" />
                        </jsp:include>
                      </div>  
                    </div>
                  </div>
                  
                  <jsp:include page="/jsp/fragments/formfield_button.jsp">
                    <jsp:param name="name" value="applySettings" />
                    <jsp:param name="labelLocale" value="panel.admin.report.options.filterTitle" />
                  </jsp:include>
                  
                </div>
              </div>
            </div>
            <div class="selectedQueryReportContainer"></div>
          </div>

        </form>

        <form name="rightSettingsForm">
          <input type="hidden" name="panelId" value="${panel.id}" />

          <div class="GUI_selectedQueryReportWrapperRight">
            <div class="selectedQueryReportTitle">
              <select name="queryId">
                <option value=""><fmt:message key="panel.admin.report.options.selectQueryTitle" /></option>
                <c:forEach var="query" items="${queries}">
                  <option value="${query.id}">${query.name}</option>
                </c:forEach>
              </select>
            </div>
            <div class="selectedQueryReportActions">
              <div class="selectedQueryReportActions-filters">
                <div class="selectedQueryReportActions-filters-icon settingsIcon"></div>
                <div class="selectedQueryReportActions-filters-container settingsContainer">

                  <div class="reportOptionsContainer">
                    <input type="hidden" name="stampId" value="${panel.currentStamp.id}"/>
                  </div>

                  <jsp:include page="/jsp/fragments/formfield_button.jsp">
                    <jsp:param name="name" value="applySettings" />
                    <jsp:param name="labelLocale" value="panel.admin.report.options.filterTitle" />
                  </jsp:include>
                </div>
              </div>
              <div class="selectedQueryReportActions-exports">
                <div class="selectedQueryReportActions-exports-icon settingsIcon"></div>
                <div class="selectedQueryReportActions-exports-container settingsContainer">
                </div>
              </div>

              <!--  Other options -->

              <div class="selectedQueryReportActions-settings">
                <div class="selectedQueryReportActions-settings-icon settingsIcon"></div>
                <div class="selectedQueryReportActions-settings-container settingsContainer">
                  <h3>
                    <fmt:message key="panel.admin.report.options.otherOptionsTitle" />
                  </h3>
                  <jsp:include page="/jsp/fragments/formfield_checkbox.jsp">
                    <jsp:param name="name" value="show2dAs1d" />
                    <jsp:param name="labelLocale" value="panel.admin.report.options.show2dAs1d" />
                    <jsp:param name="value" value="true" />
                  </jsp:include>

                  <jsp:include page="/jsp/fragments/formfield_button.jsp">
                    <jsp:param name="name" value="applySettings" />
                    <jsp:param name="labelLocale" value="panel.admin.report.options.filterTitle" />
                  </jsp:include>
                </div>
              </div>
            </div>

            <div class="selectedQueryReportContainer"></div>

          </div>

        </form>

      </div>

    </div>

  </div>

</body>
</html>