<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="messageCenter.messageMessagesBlockTitle" name="titleLocale"/>
  </jsp:include>

  <div id="messageCenterMessagesBlockContent" class="blockContent">
  
    <div class="messageCenterTabLinks">
      <!-- <a href="#received" class="messageCenterTabLink messageCenterTabLinkSelected"><fmt:message key="messageCenter.messageMessagesLinkRecieved" /></a> -->
      <a href="#sent" class="messageCenterTabLink messageCenterTabLinkSelected"><fmt:message key="messageCenter.messageMessagesLinkSent" /></a>
      <a href="#sendQueue" class="messageCenterTabLink"><fmt:message key="messageCenter.messageMessagesLinkSendQueue" /></a>
    </div>
    
    <div class="messageCenterFilterMessages">
      <c:set var="labelValue"><fmt:message key="messageCenter.messageCenterFilterMessagesLabel"/></c:set>
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="id" value="messageCenterFilterMessages"/>
        <jsp:param name="name" value="messageCenterFilterMessages"/>
        <jsp:param name="classes" value="messageCenterFilterMessages"/>
        <jsp:param name="value" value="${labelValue}"/>
      </jsp:include>
    </div>
    
    <!--  Received Messages hidden for the time being
    
    <div class="messageCenterReceivedMessagesContainer" id="messageCenterReceivedMessagesContainer">
    
      <div class="messageCenterMessage receivedMessage messageRead messageExpanded">
        <div class="messageCenterMessageHeader">
          <div class="messageSenderAndTime">
            <div class="messageSender">Pasi Kukkonen</div>
            <div class="messageTime">12.12.2012, 14:56</div>
          </div>
          <div class="messageSubject">Contrary to popular belief, it has roots in a piece of classical Latin literature from 45 BC</div>
        </div>
        <div class="messageCenterMessagesContent">Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. <br/><br/>Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. <br/><br/>Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur?
        
          <div class="messageCenterReplyMessageLink"><a href="#reply"><fmt:message key="messageCenter.messageMessagesLinkReplyMessage" /></a></div>
        </div>
      </div>

      <div class="messageCenterMessage receivedMessage messageUnRead">
        <div class="messageCenterMessageHeader">
          <div class="messageSenderAndTime">
            <div class="messageSender">Pasi Kukkonen</div>
            <div class="messageTime">12.12.2012, 14:56</div>
          </div>
          <div class="messageSubject">It does have roots in a piece of classical Latin literature from 45 BC</div>
        </div>
        <div class="messageCenterMessagesContent"> . . . </div>
      </div>
      
      <div class="messageCenterMessage receivedMessage messageRead">
        <div class="messageCenterMessageHeader">
          <div class="messageSenderAndTime">
            <div class="messageSender">Pasi Kukkonen</div>
            <div class="messageTime">12.12.2012, 14:56</div>
          </div>
          <div class="messageSubject">It does have roots in a piece of classical Latin literature from 45 BC</div>
        </div>
        <div class="messageCenterMessagesContent"> . . . </div>
      </div>
      
      <div class="messageCenterMessage receivedMessage messageRead messageExpanded">
        <div class="messageCenterMessageHeader">
          <div class="messageSenderAndTime">
            <div class="messageSender">Pasi Kukkonen</div>
            <div class="messageTime">12.12.2012, 14:56</div>
          </div>
          <div class="messageSubject">It does have roots in a piece of classical Latin literature from 45 BC</div>
        </div>
        <div class="messageCenterMessagesContent">Nemo enim ipsam kääpiöt on hippasilla voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. <br/><br/>Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit.
        
          <div class="messageCenterReplyMessageLink"><a href="#reply"><fmt:message key="messageCenter.messageMessagesLinkReplyMessage" /></a></div>
        </div>
      </div>
      
    </div>
    
    -->

    <div class="messageCenterSentMessagesContainer" id="messageCenterSentMessagesContainer">
      
      <div class="messageCenterMessage sentMessage">
        <div class="messageCenterMessageHeader">
          <div class="messageRecipientAndTime">
            <div class="messageRecipients">Pasi Kukkonen, Jari Lankinen, Antti Leppä</div>
            <div class="messageTime">12.12.2012, 14:56</div>
          </div>
          <div class="messageSubject">Lorem Ipsum is not simply random text.</div>
        </div>
        <div class="messageCenterMessagesContent"> . . . </div>
      </div>

      
      <div class="messageCenterMessage sentMessage messageExpanded">
        <div class="messageCenterMessageHeader">
          <div class="messageRecipientAndTime">
            <div class="messageRecipients">Pasi Kukkonen, Jari Lankinen, Antti Leppä</div>
            <div class="messageTime">12.12.2012, 14:56</div>
          </div>
          <div class="messageSubject">Lorem Ipsum is not simply random text.</div>
        </div>
        <div class="messageCenterMessagesContent">Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. <br/><br/>Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. <br/><br/>Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? <br/><br/>Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?"
        
	        <div class="messageCenterResendMessageLink">
	          <a href="#resend"><fmt:message key="messageCenter.messageMessagesLinkReSendMessage" /></a>
	        </div>
        </div>
      </div>
      
      <div class="messageCenterMessage sentMessage">
        <div class="messageCenterMessageHeader">
          <div class="messageRecipientAndTime">
            <div class="messageRecipients">Pasi Kukkonen, Jari Lankinen, Antti Leppä</div>
            <div class="messageTime">12.12.2012, 14:56</div>
          </div>
          <div class="messageSubject">Lorem Ipsum is not simply random text.</div>
        </div>
        <div class="messageCenterMessagesContent"> . . . </div>
      </div>
      
    </div>    

  </div>

</div>