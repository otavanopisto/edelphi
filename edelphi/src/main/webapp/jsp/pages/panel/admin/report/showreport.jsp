<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="panel.viewPanel.pageTitle">
        <fmt:param>${panel.name}</fmt:param>
      </fmt:message>
    </title>
    <link type="text/css" href="${pageContext.request.contextPath}/_themes/${theme}/css/theme.css" rel="stylesheet"/>
    <link type="text/css" href="${pageContext.request.contextPath}/_themes/${theme}/css/report_overrides.css" rel="stylesheet"/>
  </head>
  
  <body class="queryReport">
    <ed:include page="/jsp/blocks/panel_admin_report/query_pages.jsp">
      <ed:param name="reportPageDatas" value="${reportPageDatas}"/>
      <ed:param name="reportChartWidth" value="690"/>
      <ed:param name="reportChartHeight" value="419"/>
      <ed:param name="reportChartFormat" value="${chartFormat}"/>
    </ed:include>
  </body>
</html>