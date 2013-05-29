<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="defaultValue"><fmt:message key="panel.admin.inviteUsers.actionsBlock.searchFieldDefaultLabel"/></c:set>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.admin.inviteUsers.actionsBlockTitle" name="titleLocale"/>
  </jsp:include>

  <div id="panelAdminInviteUsersActionsBlockContent" class="blockContent">

    <iframe id="csvFileContent" name="csvFileContent" style="display:none;"></iframe>
    
    <form id="panelAdminInvitationForm" method="post" enctype="multipart/form-data" target="csvFileContent" autocomplete="off">
      <input type="hidden" name="securityContextId" value="${panel.id}"/>
      <input type="hidden" name="securityContextType" value="PANEL"/>
      <input type="hidden" name="invitationCount"/>
    
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="inviteUser"/>
        <jsp:param name="classes" value="invite"/>
        <jsp:param name="value" value="${defaultValue}"/>
      </jsp:include>  
      
      <h3><fmt:message key="panel.admin.inviteUsers.actionsBlock.CSVFieldLabel"></fmt:message></h3>
      
      <div id="inviteUsersCSVFileField">
        <jsp:include page="/jsp/fragments/formfield_file.jsp">
          <jsp:param name="name" value="csvFile"/>
          <jsp:param name="classes" value="file"/>
          <jsp:param name="value" value=""/>
        </jsp:include>
      </div>
      
      <div id="inviteUsersCSVUploadButton">
        <jsp:include page="/jsp/fragments/formfield_button.jsp">
          <jsp:param name="name" value="csvButton"/>
          <jsp:param name="classes" value="file"/>
          <jsp:param name="value" value=""/>
          <jsp:param name="labelLocale" value="panel.admin.inviteUsers.actionsBlock.uploadCSVButtonLabel"/>
        </jsp:include>
      </div>
      
      <div class="inviteUsersCSVExsampleFile">
        <c:choose>
          <c:when test="${pageContext.request.locale.language eq 'fi'}">
            <c:set var="csvExampleLink">/_files/userimportexample_fi.csv</c:set>
          </c:when>
          <c:otherwise>
            <c:set var="csvExampleLink">/_files/userimportexample_en.csv</c:set>
          </c:otherwise>
        </c:choose>
        <a href="${pageContext.request.contextPath}${csvExampleLink}"><fmt:message key="panel.admin.inviteUsers.actionsBlock.csvExampleLinkLabel"></fmt:message></a>
      </div>

      <h3><fmt:message key="panel.admin.inviteUsers.actionsBlock.usersToBeInvitedLabel"></fmt:message></h3>
      <div id="inviteUsersSelectedInvitationUsers"></div>

      <h3><fmt:message key="panel.admin.inviteUsers.actionsBlock.invitationFieldLabel"></fmt:message></h3>
      <jsp:include page="/jsp/fragments/formfield_memo.jsp">
        <jsp:param name="name" value="invitationMessage"/>
        <jsp:param name="classes" value="invitationMessage"/>
        <jsp:param name="value" value="${mailTemplate}"/>
      </jsp:include>

      <h3><fmt:message key="panel.admin.inviteUsers.actionsBlock.invitationTarget"></fmt:message></h3>
      <c:set var="panelTargetLocale"><fmt:message key="panel.admin.inviteUsers.actionsBlock.panelTarget"/></c:set>
      <ed:include page="/jsp/fragments/formfield_select.jsp">
        <ed:param name="name" value="queryId" />
        <ed:param name="options" value="none,${queryIds}" />
        <ed:param name="option.none" value="${panelTargetLocale}" />
        <c:forEach var="query" items="${queries}" varStatus="vs">
          <ed:param name="option.${query.id}" value="${query.name}" />
        </c:forEach>
      </ed:include>

      <jsp:include page="/jsp/fragments/formfield_checkbox.jsp">
        <jsp:param name="name" value="addUsers"/>
        <jsp:param name="classes" value="addUsers"/>
        <jsp:param name="value" value="1"/>
        <jsp:param name="labelLocale" value="panel.admin.inviteUsers.actionsBlock.addUsersWithoutInvitationLabel" />
      </jsp:include>

      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="name" value="sendInvitations"/>
        <jsp:param name="classes" value="addUsers"/>
        <jsp:param name="labelLocale" value="panel.admin.inviteUsers.actionsBlock.sendInvitationsButtonLabel"/>
      </jsp:include>

    </form>
     
  </div>

</div>