<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="joinPanel.page.pageTitle" /></title>
<jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
<jsp:include page="/jsp/supports/jshash_support.jsp"></jsp:include>
<script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/joinpanelblockcontroller.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/loginblockcontroller.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/registerblockcontroller.js"></script>
</head>
<body class="environment login">

  <jsp:include page="/jsp/templates/error_header.jsp">
    <jsp:param value="index" name="activeTrail" />
  </jsp:include>

  <div class="GUI_pageWrapper">

    <div class="GUI_pageContainer">

      <c:choose>

        <c:when test="${confirmLinking eq true}">
          <div class="errorPageContentWrapper">
            <div class="errorPageTitleContainer notLoggedIn">
              <c:choose>
                <c:when test="${!empty query}">
                  <fmt:message key="joinPanel.page.welcomeToQueryTitle">
                    <fmt:param>${query.name}</fmt:param>
                  </fmt:message>
                </c:when>
                <c:otherwise>
                  <fmt:message key="joinPanel.page.welcomeToPanelTitle">
                    <fmt:param>${panel.name}</fmt:param>
                  </fmt:message>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="errorPageIconContainer notLoggedInIcon"></div>
            <div class="errorPageDescriptionContainer">
              <p>
                <fmt:message key="joinPanel.page.linkAccountText">
                  <fmt:param>${currentUserMail}</fmt:param>
                  <fmt:param>${invitationUserMail}</fmt:param>
                </fmt:message>
              </p>
              <p>
                <a href="#" id="linkAccountLink">
                  <fmt:message key="joinPanel.page.linkAccountLinkText">
                    <fmt:param>${invitationUserMail}</fmt:param>
                  </fmt:message>
                </a>
              </p>
              <p>
                <a href="#" id="createAccountLink">
                  <fmt:message key="joinPanel.page.createAccountLinkText">
                    <fmt:param>${invitationUserMail}</fmt:param>
                  </fmt:message>
                </a>
              </p>
            </div>
          </div>
        </c:when>

        <c:when test="${dualAccount eq true}">
          <div class="errorPageContentWrapper">
            <div class="errorPageTitleContainer notLoggedIn">
              <c:choose>
                <c:when test="${!empty query}">
                  <fmt:message key="joinPanel.page.welcomeToQueryTitle">
                    <fmt:param>${query.name}</fmt:param>
                  </fmt:message>
                </c:when>
                <c:otherwise>
                  <fmt:message key="joinPanel.page.welcomeToPanelTitle">
                    <fmt:param>${panel.name}</fmt:param>
                  </fmt:message>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="errorPageIconContainer notLoggedInIcon"></div>
            <div class="errorPageDescriptionContainer">
              <fmt:message key="joinPanel.page.dualAccountText">
                <fmt:param>${currentUserMail}</fmt:param>
                <fmt:param>${invitationUserMail}</fmt:param>
              </fmt:message>
            </div>
            <div class="clearBoth"></div>
            <div class="errorPageLoginRegisterContainer">
              <jsp:include page="/jsp/blocks/index/login.jsp"></jsp:include>
              <jsp:include page="/jsp/blocks/index/register.jsp">
                <jsp:param name="skipEmailVerification" value="true" />
                <jsp:param name="email" value="${invitationUserMail}" />
              </jsp:include>
            </div>
            <div class="clearBoth"></div>
          </div>
        </c:when>

        <c:otherwise>
          <div class="errorPageContentWrapper">
            <div class="errorPageTitleContainer notLoggedIn">
              <c:choose>
                <c:when test="${!empty query}">
                  <fmt:message key="joinPanel.page.welcomeToQueryTitle">
                    <fmt:param>${query.name}</fmt:param>
                  </fmt:message>
                </c:when>
                <c:otherwise>
                  <fmt:message key="joinPanel.page.welcomeToPanelTitle">
                    <fmt:param>${panel.name}</fmt:param>
                  </fmt:message>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="errorPageIconContainer notLoggedInIcon"></div>
            <div class="errorPageDescriptionContainer">
              <c:choose>
                <c:when test="${!empty query}">
                  <fmt:message key="joinPanel.page.welcomeToQueryText">
                    <fmt:param>${invitationUserMail}</fmt:param>
                    <fmt:param>${query.name}</fmt:param>
                    <fmt:param>${panel.name}</fmt:param>
                  </fmt:message>
                </c:when>
                <c:otherwise>
                  <fmt:message key="joinPanel.page.welcomeToPanelText">
                    <fmt:param>${invitationUserMail}</fmt:param>
                    <fmt:param>${panel.name}</fmt:param>
                  </fmt:message>
                </c:otherwise>
              </c:choose>
            </div>
            <div class="clearBoth"></div>
            <div class="errorPageLoginRegisterContainer">
              <jsp:include page="/jsp/blocks/index/login.jsp"></jsp:include>
              <jsp:include page="/jsp/blocks/index/register.jsp">
                <jsp:param name="skipEmailVerification" value="true" />
                <jsp:param name="email" value="${invitationUserMail}" />
              </jsp:include>
            </div>
            <div class="clearBoth"></div>
          </div>
        </c:otherwise>

      </c:choose>
    </div>

  </div>
</body>
</html>