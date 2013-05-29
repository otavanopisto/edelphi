<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.admin.sendEmail.emailContentBlockTitle" name="titleLocale"/>
  </jsp:include>

  <div id="panelAdminSendEmailContentBlockContent" class="blockContent">
  
    <jsp:include page="/jsp/fragments/formfield_text.jsp">
      <jsp:param name="id" value="sendEmailSubject"/>
      <jsp:param name="name" value="sendEmailSubject"/>
      <jsp:param name="classes" value="sendEmailSubject required"/>
      <jsp:param name="labelLocale" value="panel.admin.sendEmail.emailSubjectLabel"/>
    </jsp:include>
  
	  <jsp:include page="/jsp/fragments/formfield_memo.jsp">
      <jsp:param name="id" value="sendEmailContent"/>
	    <jsp:param name="name" value="sendEmailContent"/>
	    <jsp:param name="classes" value="sendEmailContent required"/>
	    <jsp:param name="labelLocale" value="panel.admin.sendEmail.emailContentLabel"/>
	  </jsp:include>
	  
	  <jsp:include page="/jsp/fragments/formfield_submit.jsp">
      <jsp:param name="id" value="sendEmailSubmitButton"/>
      <jsp:param name="name" value="sendEmail"/>
      <jsp:param name="classes" value="formvalid"/>
      <jsp:param name="labelLocale" value="panel.admin.sendEmail.emailSendButtonLabel"/>
    </jsp:include>
	    
    
  </div>

</div>