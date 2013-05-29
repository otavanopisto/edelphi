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
      <fmt:message key="panel.block.document.title">
        <fmt:param>${name}</fmt:param>
      </fmt:message>
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <c:if test="${styleSheet != null}">
      <style type="text/css">${styleSheet}</style>
    </c:if>
  </head>
  <body class="panel index">
  
    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="index" name="activeTrail"/>
      <jsp:param value="${name}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}${fullPath}" name="breadcrumbPageUrl"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
        <c:if test="${not empty title}">
          <h2 class="documentPageTitle">${title}</h2>
        </c:if>

        <c:if test="${not empty content}">
          <div class="documentContentContainer">${content}</div>
        </c:if>
        
        <div class="documentNavigationContainer">
          <c:choose>
            <c:when test="${type eq 'LOCAL_DOCUMENT'}">
              <ed:include page="/jsp/fragments/contextual_link.jsp">
                <ed:param name="href" value="?page=0" />
                <ed:param name="action" value="documentFirstPage" />
                <ed:param name="label" value="panel.block.document.firstPageLink" />
                <ed:param name="disabled" value="${page > 0 ? 'false' : 'true'}"/>
              </ed:include>
  
              <ed:include page="/jsp/fragments/contextual_link.jsp">
                <ed:param name="href" value="?page=${page - 1}" />
                <ed:param name="action" value="documentPreviousPage" />
                <ed:param name="label" value="panel.block.document.previousPageLink" />
                <ed:param name="disabled" value="${page > 0 ? 'false' : 'true'}"/>
              </ed:include>
  
              <ed:include page="/jsp/fragments/contextual_link.jsp">
                <ed:param name="href" value="?page=${page + 1}" />
                <ed:param name="action" value="documentNextPage" />
                <ed:param name="label" value="panel.block.document.nextPageLink" />
                <ed:param name="disabled" value="${page < pageCount - 1 ? 'false' : 'true'}"/>
              </ed:include>
  
              <ed:include page="/jsp/fragments/contextual_link.jsp">
                <ed:param name="href" value="?page=${pageCount - 1}" />
                <ed:param name="action" value="documentLastPage" />
                <ed:param name="label" value="panel.block.document.lastPageLink" />
                <ed:param name="disabled" value="${page < pageCount - 1 ? 'false' : 'true'}"/>
              </ed:include>
            </c:when>
          </c:choose>
        </div>
        
        <div class="clearBoth"></div>
      </div>
      
    </div>
  </body>
</html>