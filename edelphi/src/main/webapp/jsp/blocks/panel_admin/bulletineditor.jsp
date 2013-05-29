<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="panelAdminBulletinEditorBlock" class="block">

  <div id="panelAdminBulletinEditorBlockContent" class="blockContent">
  
    <div class="blockTitle"><h2><fmt:message key="panel.admin.managePanelBulletins.editTitle" /></h2></div>

    <div id="panelAdminBulletinEditorForm">
    
      <input type="hidden" name="bulletinId" value="${bulletin.id}"/>

      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="labelLocale" value="panel.admin.managePanelBulletins.titleLabel" />
        <jsp:param name="name" value="title" />
        <jsp:param name="value" value="${bulletin.title}" />
        <jsp:param name="classes" value="required" />
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_memo.jsp">
        <jsp:param name="labelLocale" value="panel.admin.managePanelBulletins.messageLabel" />
        <jsp:param name="name" value="message" />
        <jsp:param name="value" value="${bulletin.message}" />
      </jsp:include>

      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="labelLocale" value="panel.admin.managePanelBulletins.save" />
        <jsp:param name="classes" value="formvalid" />
        <jsp:param name="name" value="save" />
      </jsp:include>
    </div>
  </div>
</div>