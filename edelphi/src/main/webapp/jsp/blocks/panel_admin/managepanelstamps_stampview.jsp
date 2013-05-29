<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <div id="panelAdminManagePanelStampsViewBlockContent" class="blockContent">
    <div id="managePanelStampsViewColumnContent" style="display: block;">
      <div class="blockTitle"><h2 id="managePanelStampsStampViewColumn_stampName"><fmt:message key="panels.admin.panelStamps.newPanelStamp"/></h2></div>

      <input type="hidden" name="stampId"/>
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="labelLocale" value="panels.admin.panelStamps.stampViewNameLabel" />
        <jsp:param name="name" value="title" />
        <jsp:param name="classes" value="required" />
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_memo.jsp">
        <jsp:param name="labelLocale" value="panels.admin.panelStamps.stampViewDescriptionLabel" />
        <jsp:param name="classes" value="panelStampDescription"/>        
        <jsp:param name="name" value="description" />
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="labelLocale" value="panels.admin.panelStamps.stampViewSubmitLabel" />
        <jsp:param name="classes" value="formvalid" />
        <jsp:param name="name" value="save" />
      </jsp:include>
      
    </div>
  </div>  

</div>