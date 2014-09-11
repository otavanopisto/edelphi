<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="indexLoginBlockContent" class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param name="titleLocale" value="index.block.loginBlockTitle"/>
    <jsp:param name="helpText" value=""/>
  </jsp:include>
  
  <div id="loginBlockContent" class="blockContent">
    <c:if test="${credentialAuthCount gt 0}">
      <form id="loginForm">
        
        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="name" value="username"/>
          <jsp:param name="classes" value="required email"/>
          <jsp:param name="labelLocale" value="index.block.loginEmailLabel"/>
        </jsp:include>  
        
        <jsp:include page="/jsp/fragments/formfield_password.jsp">
          <jsp:param name="name" value="password"/>
          <jsp:param name="classes" value="required"/>
          <jsp:param name="labelLocale" value="index.block.loginPasswordLabel"/>
        </jsp:include>

        <c:choose>
          <c:when test="${credentialAuthCount eq 1}">
            <input type="hidden" name="authSource" value="${authSources[0].id}"/>
          </c:when>
          <c:otherwise>
            <c:set var="credentialAuthIds"></c:set>
            <c:forEach var="authSource" items="${authSources}" begin="0" end="${credentialAuthCount - 1}" varStatus="vs">
              <c:if test="${!vs.first}"><c:set var="credentialAuthIds" value="${credentialAuthIds},"/></c:if>
              <c:set var="credentialAuthIds" value="${credentialAuthIds}${authSource.id}"/>
            </c:forEach>
            <ed:include page="/jsp/fragments/formfield_select.jsp">
              <ed:param name="name" value="authSource"/>
              <ed:param name="classes" value="required"/>
              <ed:param name="labelLocale" value="index.block.loginSourceLabel"/>
              <ed:param name="options" value="${credentialAuthIds}"/>
              <c:forEach var="authSource" items="${authSources}" begin="0" end="${credentialAuthCount - 1}" varStatus="vs">
                <ed:param name="option.${authSource.id}" value="${authSource.name}"/>
              </c:forEach>
            </ed:include>
          </c:otherwise>
        </c:choose>
        
        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
          <jsp:param name="name" value="login"/>
          <jsp:param name="classes" value="formvalid"/>
          <jsp:param name="labelLocale" value="index.block.loginButtonLabel"/>
        </jsp:include>
        
        <c:if test="${hasInternalAuth}">
          <a id="passwordResetLink" href="#"><fmt:message key="index.block.forgotPassword"/></a>
        </c:if>
      </form>
    </c:if>     
    
    <c:if test="${authCount gt credentialAuthCount}">
      <div class="loginWithAnotherSiteContainer">
        <h3><fmt:message key="index.block.loginWithAnotherSite"/></h3>
        <ul>
          <!-- TODO this probably needs to be more generic, especially in order to support multiple sources of the same strategy -->
          <c:forEach var="authSource" items="${authSources}" begin="${credentialAuthCount}">
            <c:choose>
              <c:when test="${authSource.strategy eq 'Facebook'}">
                <li class="loginWithAnotherSiteLink loginWithFacebook"><a href="${pageContext.request.contextPath}/dologin.page?authSource=${authSource.id}"><fmt:message key="index.block.loginWithFacebook"/></a> <fmt:message key="index.block.loginWithPostText"/></li>
              </c:when>
              <c:when test="${authSource.strategy eq 'Google'}">
                <li class="loginWithAnotherSiteLink loginWithGoogle"><a href="${pageContext.request.contextPath}/dologin.page?authSource=${authSource.id}"><fmt:message key="index.block.loginWithGoogle"/></a> <fmt:message key="index.block.loginWithPostText"/></li>
              </c:when>
              <c:when test="${authSource.strategy eq 'Twitter'}">
                <li class="loginWithAnotherSiteLink loginWithTwitter"><a href="${pageContext.request.contextPath}/dologin.page?authSource=${authSource.id}"><fmt:message key="index.block.loginWithTwitter"/></a> <fmt:message key="index.block.loginWithPostText"/></li>
              </c:when>
              <c:otherwise>
                <li class="loginWithAnotherSiteLink"><a href="${pageContext.request.contextPath}/dologin.page?authSource=${authSource.id}">${authSource.name}</a> <fmt:message key="index.block.loginWithPostText"/></li>
              </c:otherwise>
            </c:choose>
          </c:forEach>
        </ul>
        
        <!-- TODO tupas authentication
        <div id="loginWithTupasContainer">
          <c:forEach var="bankId" items="${tupasBankIds}">
            <jsp:include page="/jsp/fragments/tupas_bank.jsp">
              <jsp:param name="bankId" value="${bankId}"/>
            </jsp:include>
          </c:forEach>
        </div>
        -->
        
      </div>
    </c:if>
  </div>

</div>

