<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

  <div class="GUI_header_outer_wrapper">
    <div class="GUI_header_inner_wrapper">
      <div class="GUI_header">
      
        <div class="GUI_panel_header_text_wrapper"><a href="${pageContext.request.contextPath}/">eDelfoi.fi</a> <span class="GUI_panel_header_text_panelname"> - ${panel.name}</span></div>
      
        <div class="GUI_header_user_container">
          <c:if test="${loggedUserId gt 0}">
            <c:if test="${loggedUserHasPicture}">
              <c:set var="userPictureStyle" value="background-image:url('${pageContext.request.contextPath}/user/picture.binary?userId=${loggedUserId}');"/>
            </c:if>
            <div class="headerUserImage" style="${userPictureStyle}"></div>
            <div class="headerUserInfo">
              <div class="headerWelcomeText">
                <fmt:message key="generic.header.userInfo.welcomeText" /> <span class="headerUserName">${loggedUserFullName}</span>
              </div>
            
              <div class="headerUserLink"><a href="${pageContext.request.contextPath}/panel/profile.page?panelId=${panel.id}"><fmt:message key="generic.header.userInfo.userProfileLink" /></a></div>
              <div class="headerUserLink"><a href="${pageContext.request.contextPath}/logout.page"><fmt:message key="generic.header.userInfo.logoutLink" /></a></div>
            </div>
          </c:if>
        </div>

        <div class="headerLocaleContainer">
          <c:choose>
            <c:when test="${pageContext.request.locale.language eq 'fi'}">
              <a class="headerLocaleLink localeSelected" href="#" onclick="setLocale('fi_FI');">Suomeksi</a><a class="headerLocaleLink" href="#" onclick="setLocale('en_US');">In English</a>
            </c:when>
            <c:otherwise>
              <a class="headerLocaleLink" href="#" onclick="setLocale('fi_FI');">Suomeksi</a><a class="headerLocaleLink localeSelected" href="#" onclick="setLocale('en_US');">In English</a>
            </c:otherwise>
          </c:choose>
        </div>
    
        <div class="GUI_navigation">
    
          <ul class="menu">
            <c:choose>
              <c:when test="${(empty param.activeTrail) or (param.activeTrail eq 'index')}">
                <li class="menuItem activeTrail"><a href="${pageContext.request.contextPath}${panel.fullPath}"><fmt:message key="panel.menu.indexLabel"/></a></li>
              </c:when>
              <c:otherwise>
                <li class="menuItem"><a href="${pageContext.request.contextPath}${panel.fullPath}"><fmt:message key="panel.menu.indexLabel"/></a></li>
              </c:otherwise>
            </c:choose>

            <c:if test="${panelActions['MANAGE_PANEL']}">
              <c:choose>
                <c:when test="${param.activeTrail eq 'management'}">
                  <li class="menuItem activeTrail"><a href="${pageContext.request.contextPath}/panel/admin/dashboard.page?panelId=${panel.id}"><fmt:message key="panel.menu.managementLabel"/></a></li>
                </c:when>
                <c:otherwise>
                  <li class="menuItem"><a href="${pageContext.request.contextPath}/panel/admin/dashboard.page?panelId=${panel.id}"><fmt:message key="panel.menu.managementLabel"/></a></li>
                </c:otherwise>
              </c:choose>
            </c:if>

            <c:choose>
              <c:when test="${param.activeTrail eq 'reportIssue'}">
                <li class="menuItem activeTrail"><a href="${pageContext.request.contextPath}/panel/reportissue.page?panelId=${panel.id}"><fmt:message key="panel.menu.reportIssueLabel"/></a></li>
              </c:when>
              <c:otherwise>
                <li class="menuItem"><a href="${pageContext.request.contextPath}/panel/reportissue.page?panelId=${panel.id}"><fmt:message key="panel.menu.reportIssueLabel"/></a></li>
              </c:otherwise>
            </c:choose>
          </ul>
    
        </div>
        
      </div>
      
      <div class="GUI_header_shadow_wrapper">
        <div class="GUI_header_shadow">
       
        </div>
      </div>
      
      <div class="GUI_breadcrumbWrapper">
      
	      <div class="GUI_breadcrumb">
	        <c:choose>
	          <c:when test="${param.activeTrail eq 'management'}">
	            <jsp:include page="/jsp/templates/paneladmin_breadcrumb.jsp">
	              <jsp:param value="${param.breadcrumbPageTitle}" name="breadcrumbPageTitle"/>
	              <jsp:param value="${param.breadcrumbPageUrl}" name="breadcrumbPageUrl"/>
	            </jsp:include>
	          </c:when>
	          <c:otherwise>
	            <jsp:include page="/jsp/templates/panel_breadcrumb.jsp">
	              <jsp:param value="${param.breadcrumbPageTitle}" name="breadcrumbPageTitle"/>
	              <jsp:param value="${param.breadcrumbPageUrl}" name="breadcrumbPageUrl"/>
	            </jsp:include>
	          </c:otherwise>
	        </c:choose>      
	      </div>
      
      </div>
    </div>
      
  </div>

