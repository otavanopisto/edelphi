<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="messageCenter.messageContentBlockTitle" name="titleLocale"/>
  </jsp:include>

  <div id="messageCenterContentBlockContent" class="blockContent">
  
    <div class="messageCenterTabLinks">
      <ul>
        <li><a href="#send_email" class="messageCenterTabLink messageCenterTabLinkSelected"><fmt:message key="messageCenter.messageContentEmailLinkLabel" /></a></li>
        <li><a href="#invite_panelists" class="messageCenterTabLink"><fmt:message key="messageCenter.messageContentInviteLinkLabel" /></a></li>
        <li><a href="#send_notification" class="messageCenterTabLink"><fmt:message key="messageCenter.messageContentReminderLinkLabel" /></a></li>
      </ul>
    </div>
  
    <jsp:include page="/jsp/fragments/formfield_text.jsp">
      <jsp:param name="id" value="messageCenterMessageSubject"/>
      <jsp:param name="name" value="messageCenterMessageSubject"/>
      <jsp:param name="classes" value="messageCenterMessageSubject required"/>
      <jsp:param name="labelLocale" value="messageCenter.messageSubjectLabel"/>
    </jsp:include>
  
	  <jsp:include page="/jsp/fragments/formfield_memo.jsp">
      <jsp:param name="id" value="messageCenterMessageContent"/>
	    <jsp:param name="name" value="messageCenterMessageContent"/>
	    <jsp:param name="classes" value="messageCenterMessageContent required"/>
	    <jsp:param name="labelLocale" value="messageCenter.messageContentLabel"/>
	  </jsp:include>
	  
	  <div class="messageCenterRecipientsWrapper">
	    <h3><fmt:message key="messageCenter.messageRecipientsBlockTitle" /></h3>
	    <div class="messageCenterAddRecipientLink">
	      <a href="#"><fmt:message key="messageCenter.messageMessagesAddRecipientLinkLabel" /></a>
	    </div>
	    <div class="messageCenterSelectedRecipients" id="messageCenterSelectedRecipients">
	      
	    </div>
	  </div>
	  
	  <jsp:include page="/jsp/fragments/formfield_submit.jsp">
      <jsp:param name="id" value="messageCenterSendMessage"/>
      <jsp:param name="name" value="messageCenterSendMessage"/>
      <jsp:param name="classes" value="formvalid"/>
      <jsp:param name="labelLocale" value="messageCenter.messageSendButtonLabel"/>
    </jsp:include>
	    
    
  </div>

</div>