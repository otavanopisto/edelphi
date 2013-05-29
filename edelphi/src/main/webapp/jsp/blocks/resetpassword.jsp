<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="resetPassword.block.title" name="titleLocale"/>
  </jsp:include>
	
	<div id="resetPasswordBlockContent" class="blockContent">
    <form id="resetPasswordForm">
      <input type="hidden" name="email" value="${email}"/>
      <input type="hidden" name="hash" value="${hash}"/>

      <jsp:include page="/jsp/fragments/formfield_password.jsp">
        <jsp:param name="name" value="password1"/>
        <jsp:param name="classes" value="required equals equals-password2"/>
        <jsp:param name="labelLocale" value="resetPassword.block.passwordLabel"/>
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_password.jsp">
        <jsp:param name="name" value="password2"/>
        <jsp:param name="classes" value="required equals equals-password1"/>
        <jsp:param name="labelLocale" value="resetPassword.block.passwordAgainLabel"/>
      </jsp:include>      
  
      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="name" value="changePasswordButton"/>
        <jsp:param name="classes" value="formvalid"/>
        <jsp:param name="labelLocale" value="resetPassword.block.saveButton"/>
      </jsp:include>
    </form>
  </div>

</div>