<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="indexRegisterBlockContent" class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="index.block.registerBlockTitle" name="titleLocale"/>
  </jsp:include>
  
  <div id="registerBlockContent" class="blockContent">
    <form id="registerForm">
    
      <c:if test="${param.skipEmailVerification}">
        <input type="hidden" name="skipEmailVerification" value="${param.skipEmailVerification}"/>
      </c:if>
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="firstName"/>
        <jsp:param name="classes" value="required"/>
        <jsp:param name="labelLocale" value="index.block.registerFirstNameLabel"/>
      </jsp:include>  
      
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="lastName"/>
        <jsp:param name="classes" value="required"/>
        <jsp:param name="labelLocale" value="index.block.registerLastNameLabel"/>
      </jsp:include>        
    
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="email"/>
        <jsp:param name="classes" value="required email"/>
        <jsp:param name="value" value="${param.email}"/>
        <jsp:param name="labelLocale" value="index.block.registerEmailLabel"/>
      </jsp:include>  
      
      <jsp:include page="/jsp/fragments/formfield_password.jsp">
        <jsp:param name="name" value="password1"/>
        <jsp:param name="classes" value="required equals equals-password2"/>
        <jsp:param name="labelLocale" value="index.block.registerPasswordLabel"/>
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_password.jsp">
        <jsp:param name="name" value="password2"/>
        <jsp:param name="classes" value="required equals equals-password1"/>
        <jsp:param name="labelLocale" value="index.block.registerRetypePasswordLabel"/>
      </jsp:include>      
      
      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="name" value="register"/>
        <jsp:param name="classes" value="formvalid"/>
        <jsp:param name="labelLocale" value="index.block.registerButtonLabel"/>
      </jsp:include>
    </form> 
  </div>

</div>