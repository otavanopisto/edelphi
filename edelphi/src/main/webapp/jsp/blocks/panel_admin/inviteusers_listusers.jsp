<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.admin.inviteUsers.usersListBlockTitle" name="titleLocale"/>
  </jsp:include>

  <div id="panelAdminInviteUsersListBlockContent" class="blockContent">
    
    <c:if test="${addedCount gt 0}">
      <div class="userContainer inviteUsersListAddedByManagerContainer">
        <h3><fmt:message key="panel.admin.inviteUsers.usersListBlock.addedByManagerLabel"></fmt:message>
          <span class="inviteUsersListResendInvitationLinkContainer">
            <a href="#" class="inviteUsersResendAllInvitationsLink"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationPending.resendInvitationToAll"></fmt:message></a>
          </span>
        </h3>
        <c:forEach var="panelUser" items="${panelUsers}">
          <c:if test="${panelUser.joinType eq 'ADDED'}">
            <div class="inviteUsersListRow">
              <input type="hidden" name="email" value="${panelUser.user.defaultEmail.address}"/>
              <div class="inviteUsersListUserInfo">
                <c:choose>
                  <c:when test="${panelUser.user.fullName eq panelUser.user.defaultEmail.address}">
                    ${panelUser.user.fullName}
                  </c:when>
                  <c:otherwise>
                    ${panelUser.user.fullName} <div class="inviteUsersListUserEmail">${panelUser.user.defaultEmail.address}</div>
                  </c:otherwise>
                </c:choose>
              </div>
              <div class="inviteUsersListMeta panelAdminGenericMeta"><fmt:message key="panel.admin.inviteUsers.usersListBlock.addedDateTimeLabel"></fmt:message> <fmt:formatDate value="${panelUser.created}"/></div>
              <div class="inviteUsersListResendLink"><a href="#" class="inviteUsersResendInvitationLink"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationPending.resendInvitationToUser"></fmt:message></a></div>
            </div>
          </c:if>
        </c:forEach>
      </div>
    </c:if>

    <c:if test="${registeredCount gt 0}">
      <div class="userContainer inviteUsersListRegisteredContainer">
        <h3><fmt:message key="panel.admin.inviteUsers.usersListBlock.registeredLabel"></fmt:message>
          <span class="inviteUsersListResendInvitationLinkContainer">
            <a href="#" class="inviteUsersResendAllInvitationsLink"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationPending.resendInvitationToAll"></fmt:message></a>
          </span>
        </h3>
        <c:forEach var="panelUser" items="${panelUsers}">
          <c:if test="${panelUser.joinType eq 'REGISTERED'}">
            <div class="inviteUsersListRow">
              <input type="hidden" name="email" value="${panelUser.user.defaultEmail.address}"/>
              <div class="inviteUsersListUserInfo">
                <c:choose>
                  <c:when test="${panelUser.user.fullName eq panelUser.user.defaultEmail.address}">
                    ${panelUser.user.fullName}
                  </c:when>
                  <c:otherwise>
                    ${panelUser.user.fullName} <div class="inviteUsersListUserEmail">${panelUser.user.defaultEmail.address}</div>
                  </c:otherwise>
                </c:choose>
              </div>
              <div class="inviteUsersListMeta panelAdminGenericMeta"><fmt:message key="panel.admin.inviteUsers.usersListBlock.registeredDateTimeLabel"></fmt:message> <fmt:formatDate value="${panelUser.created}"/></div>
              <div class="inviteUsersListResendLink"><a href="#" class="inviteUsersResendInvitationLink"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationPending.resendInvitationToUser"></fmt:message></a></div>
            </div>
          </c:if>
        </c:forEach>
      </div>
    </c:if>
    
    <c:if test="${acceptedCount gt 0}">
      <div class="userContainer inviteUsersListInvitationAcceptedContainer">
        <h3><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationAcceptedLabel"></fmt:message>
          <span class="inviteUsersListResendInvitationLinkContainer">
            <a href="#" class="inviteUsersResendAllInvitationsLink"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationPending.resendInvitationToAll"></fmt:message></a>
          </span>
        </h3>
        <c:forEach var="panelUser" items="${panelUsers}">
          <c:if test="${panelUser.joinType eq 'INVITED'}">
            <div class="inviteUsersListRow">
              <input type="hidden" name="email" value="${panelUser.user.defaultEmail.address}"/>
              <div class="inviteUsersListUserInfo">
                <c:choose>
                  <c:when test="${panelUser.user.fullName eq panelUser.user.defaultEmail.address}">
                    ${panelUser.user.fullName}
                  </c:when>
                  <c:otherwise>
                    ${panelUser.user.fullName} <div class="inviteUsersListUserEmail">${panelUser.user.defaultEmail.address}</div>
                  </c:otherwise>
                </c:choose>
              </div>
              <div class="inviteUsersListMeta panelAdminGenericMeta"><fmt:message key="panel.admin.inviteUsers.usersListBlock.acceptedDateTimeLabel"></fmt:message> <fmt:formatDate value="${panelUser.created}"/></div>
              <div class="inviteUsersListResendLink"><a href="#" class="inviteUsersResendInvitationLink"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationPending.resendInvitationToUser"></fmt:message></a></div>
            </div>
          </c:if>
        </c:forEach>
      </div>
    </c:if>
    
    <c:if test="${pendingCount gt 0}">
      <div class="userContainer inviteUsersListInvitationPendingContainer">
        <h3><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationPendingLabel"></fmt:message> 
          <span class="inviteUsersListResendInvitationLinkContainer">
            <a href="#" class="inviteUsersResendAllInvitationsLink"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationPending.resendInvitationToAll"></fmt:message></a>
          </span>
        </h3>
        <c:forEach var="invitation" items="${invitations}">
          <c:if test="${invitation.state eq 'PENDING'}">
            <div class="inviteUsersListRow">
              <input type="hidden" name="email" value="${invitation.email}"/>
              <div class="inviteUsersListUserInfo">
                ${invitation.email}
              </div>
              <div class="inviteUsersListMeta panelAdminGenericMeta"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationSentDateTimeLabel"></fmt:message> <fmt:formatDate value="${invitation.lastModified}"/></div>
              <div class="inviteUsersListResendLink"><a href="#" class="inviteUsersResendInvitationLink"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationPending.resendInvitationToUser"></fmt:message></a></div>
            </div>
          </c:if>
        </c:forEach>
      </div>
    </c:if>
    
    <c:if test="${declinedCount gt 0}">
      <div class="userContainer inviteUsersListInvitationDeniedContainer">
        <h3><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationDeniedLabel"></fmt:message></h3>
        <c:forEach var="invitation" items="${invitations}">
          <c:if test="${invitation.state eq 'DECLINED'}">
            <div class="inviteUsersListRow">
              <div class="inviteUsersListUserInfo">
                ${invitation.email}
              </div>
              <div class="inviteUsersListMeta panelAdminGenericMeta"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationDeniedDateTimeLabel"></fmt:message> <fmt:formatDate value="${invitation.lastModified}"/></div>
            </div>
          </c:if>
        </c:forEach>
      </div>
    </c:if>

    <c:if test="${queuedCount gt 0}">
      <div class="userContainer inviteUsersListInvitationQueuedContainer">
        <h3><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationQueuedLabel"></fmt:message></h3>
        <c:forEach var="invitation" items="${invitations}">
          <c:if test="${(invitation.state eq 'IN_QUEUE') or (invitation.state eq 'BEING_SENT')}">
            <div class="inviteUsersListRow">
              <div class="inviteUsersListUserInfo">
                ${invitation.email}
              </div>
              <div class="inviteUsersListMeta panelAdminGenericMeta"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationQueuedDateTimeLabel"></fmt:message> <fmt:formatDate value="${invitation.lastModified}"/></div>
            </div>
          </c:if>
        </c:forEach>
      </div>
    </c:if>

    <c:if test="${failedCount gt 0}">
      <div class="userContainer inviteUsersListInvitationFailedContainer">
        <h3><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationFailedLabel"></fmt:message> 
          <span class="inviteUsersListResendInvitationLinkContainer">
            <a href="#" class="inviteUsersResendAllInvitationsLink"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationPending.resendInvitationToAll"></fmt:message></a>
          </span>
        </h3>
        <c:forEach var="invitation" items="${invitations}">
          <c:if test="${invitation.state eq 'SEND_FAIL'}">
            <div class="inviteUsersListRow">
              <div class="inviteUsersListUserInfo">
                ${invitation.email}
              </div>
              <div class="inviteUsersListMeta panelAdminGenericMeta"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationFailedDateTimeLabel"></fmt:message> <fmt:formatDate value="${invitation.lastModified}"/></div>
              <div class="inviteUsersListResendLink"><a href="#" class="inviteUsersResendInvitationLink"><fmt:message key="panel.admin.inviteUsers.usersListBlock.invitationPending.resendInvitationToUser"></fmt:message></a></div>
            </div>
          </c:if>
        </c:forEach>
      </div>
    </c:if>
      
  </div>

</div>