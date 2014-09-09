<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="reportIssueBlockContent" class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="reportIssue.block.blockTitle" name="titleLocale"/>
  </jsp:include>
  
  <div id="reportIssueBlockContent" class="blockContent">
  
    <p><fmt:message key="reportIssue.block.blockDescription"/></p>
    <p><fmt:message key="reportIssue.block.googleGroupsPrefix"/> <a href="<fmt:message key="reportIssue.block.googleGroupsGroupLink"/>"><fmt:message key="reportIssue.block.googleGroupsGroupLink"/></a><fmt:message key="reportIssue.block.googleGroupsPostfix"/></p>
  
    <form name="reportIssue">
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="subject"/>
        <jsp:param name="classes" value="required"/>
        <jsp:param name="labelLocale" value="reportIssue.block.blockSubjectLabel"/>
        <jsp:param name="value" value=""/>
      </jsp:include>  
      
      <jsp:include page="/jsp/fragments/formfield_memo.jsp">
        <jsp:param name="name" value="content"/>
        <jsp:param name="classes" value="required reportIssue"/>
        <jsp:param name="labelLocale" value="reportIssue.block.blockContentLabel"/>
        <jsp:param name="value" value=""/>
      </jsp:include>  
    
      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="name" value="send"/>
        <jsp:param name="classes" value="formvalid"/>
        <jsp:param name="labelLocale" value="reportIssue.block.blockSendButton"/>
      </jsp:include>
    </form>
  </div>

</div>