<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <c:choose>
        <c:when test="${statusCode == 0}">
          <fmt:message key="information.pageTitle"/>
        </c:when>
        <c:when test="${statusCode == 102}">
          <fmt:message key="exception.pageNotFound.title"/>
        </c:when>
        <c:when test="${statusCode == 101}">
          <fmt:message key="exception.unauthorized.title"/>
        </c:when>
        <c:otherwise>
          <fmt:message key="exception.otherError.title"/>
        </c:otherwise>
      </c:choose>
    </title>
    <jsp:include page="/jsp/templates/index_head.jsp">
      <jsp:param name="skipErrorProcessing" value="true"/>
    </jsp:include>
  </head>
  <body class="environment error">
  
    <jsp:include page="/jsp/templates/error_header.jsp">
      <jsp:param value="index" name="activeTrail"/>
    </jsp:include>
    
    <div class="GUI_pageWrapper">

      <div class="GUI_pageContainer">
      
        <c:choose>
          <c:when test="${statusCode == 0}">
            <!-- Generic Ghost Informant from Future -->
            <div class="errorPageContentWrapper">
              <div class="errorPageIconContainer ghostInformantIcon"></div>
              <div class="errorPageDescriptionContainer">
                <c:forEach var="message" items="${messages}">
                  <div>${message.message}</div>
                </c:forEach>
              </div>
              <div class="clearBoth"></div>
              <c:if test="${!empty panel}">
                <div class="errorPageGoToPanelFrontPageLinkContainer"><a href="${pageContext.request.contextPath}${panel.fullPath}"><fmt:message key="generic.errorPage.goToPanelFrontPageLabel" /></a></div>
              </c:if>
              <div class="errorPageGoToFrontPageLinkContainer"><a href="${pageContext.request.contextPath}/index.page"><fmt:message key="generic.errorPage.goToFrontPageLabel" /></a></div>
            </div>
          
          </c:when>
          <c:when test="${statusCode == 102}">
            <!-- Page Not Found -->
            <div class="errorPageContentWrapper">
              <div class="errorPageTitleContainer pageNotFound">
                <fmt:message key="exception.pageNotFound.title"/>
              </div>
              <div class="errorPageIconContainer pageNotFoundIcon"></div>
              <div class="errorPageDescriptionContainer">
                <fmt:message key="exception.pageNotFound.description"/>
              </div>
              <div class="clearBoth"></div>
              <c:if test="${!empty panel}">
                <div class="errorPageGoToPanelFrontPageLinkContainer"><a href="${pageContext.request.contextPath}${panel.fullPath}"><fmt:message key="generic.errorPage.goToPanelFrontPageLabel" /></a></div>
              </c:if>
              <div class="errorPageGoToFrontPageLinkContainer"><a href="${pageContext.request.contextPath}/index.page"><fmt:message key="generic.errorPage.goToFrontPageLabel" /></a></div>
            </div>
          </c:when>
          <c:when test="${statusCode == 101}">
            <!-- Not Authorized -->
            <div class="errorPageContentWrapper">
              <div class="errorPageTitleContainer notAuthorized">
                <fmt:message key="exception.unauthorized.title"/>
              </div>
              <div class="errorPageIconContainer notAuthorizedIcon"></div>
              <div class="errorPageDescriptionContainer">
                <fmt:message key="exception.unauthorized.description"/>
              </div>
              <div class="clearBoth"></div>
              <c:if test="${!empty panel}">
                <div class="errorPageGoToPanelFrontPageLinkContainer"><a href="${pageContext.request.contextPath}${panel.fullPath}"><fmt:message key="generic.errorPage.goToPanelFrontPageLabel" /></a></div>
              </c:if>
              <div class="errorPageGoToFrontPageLinkContainer"><a href="${pageContext.request.contextPath}/index.page"><fmt:message key="generic.errorPage.goToFrontPageLabel" /></a></div>
            </div>
          </c:when>
          <c:otherwise>
            <!-- Internal Server Error a.k.a Huth Is An Fire And Cows Are Running Loose ... poor Clementine -->
            <div class="errorPageContentWrapper">
              <div class="errorPageTitleContainer internalServerError">
                <fmt:message key="exception.otherError.title"/>
              </div>
              <div class="errorPageIconContainer internalServerErrorIcon"></div>
              <div class="errorPageDescriptionContainer">
                <c:forEach var="message" items="${messages}">
                  <div>${message.message}</div>
                </c:forEach>
              </div>
              <div class="clearBoth"></div>
              <c:if test="${!empty panel}">
                <div class="errorPageGoToPanelFrontPageLinkContainer"><a href="${pageContext.request.contextPath}${panel.fullPath}"><fmt:message key="generic.errorPage.goToPanelFrontPageLabel" /></a></div>
              </c:if>
              <div class="errorPageGoToFrontPageLinkContainer"><a href="${pageContext.request.contextPath}/index.page"><fmt:message key="generic.errorPage.goToFrontPageLabel" /></a></div>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
      
    </div>

  </body>
</html>