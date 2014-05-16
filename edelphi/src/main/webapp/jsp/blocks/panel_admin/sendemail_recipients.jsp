<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.admin.sendEmail.emailRecipientsBlockTitle" name="titleLocale" />
  </jsp:include>

  <div id="panelAdminSendEmailRecipientBlockContent" class="blockContent">
  
    <div id="selectAllRecipients" class="sendMainRecipientEmail" style="cursor:pointer;"><fmt:message key="panel.admin.sendEmail.selectAll"/></div>

    <c:forEach var="panelUser" items="${panelUsers}">
      <c:set var="classes">formField formCheckBox</c:set>
      <c:choose>
        <c:when test="${empty(panelUser.user.firstName) && empty(panelUser.user.lastName)}">
          <c:set var="name">${panelUser.user.defaultEmailAsObfuscatedString}</c:set>
          <c:set var="email"></c:set>
        </c:when>
        <c:otherwise>
          <c:set var="name">${panelUser.user.fullName}</c:set>
          <c:set var="email">${panelUser.user.defaultEmailAsObfuscatedString}</c:set>
        </c:otherwise>
      </c:choose>
      <div class="formFieldContainer formCheckBoxContainer">
        <input type="checkbox" id="emailRecipient.${panelUser.id}" name="emailRecipient.${panelUser.id}" value="${panelUser.id}" />
        <label for="emailRecipient.${panelUser.id}">${name} <span class="sendMainRecipientEmail">${email}</span></label><br />
      </div>
    </c:forEach>

  </div>

</div>