<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="profile.block.profileBlockTitle" name="titleLocale" />
  </jsp:include>

  <div id="profileGenericBlockContent" class="blockContent">
    <c:if test="${loggedUserHasPicture}">
      <c:set var="userPictureStyle" value="background-image:url('${pageContext.request.contextPath}/user/picture.binary?userId=${user.id}');" />
    </c:if>

    <div class="profilePictureWrapper">
      <div class="profilePictureTitle">
        <fmt:message key="profile.block.profilePictureBlockTitle" />
      </div>
      <div id="profilePicture" class="profilePicture" style="${userPictureStyle}">
        <div class="changeProfilePictureButton">
          <fmt:message key="profile.block.profilePicture.changeProfilePictureButton" />
        </div>
        <div class="changeProfilePictureModalOverlay" style="display: none"></div>
        <div class="changeProfilePictureModalContainer" style="display: none">
          <iframe id="_uploadFrame" onload="profilePictureSelected('${pageContext.request.contextPath}/user/picture.binary?userId=${user.id}');"
            style="display: none" name="_uploadFrame"> </iframe>

          <div class="changeProfilePictureCloseModalButton"></div>
          <div class="changeProfilePictureModalContent">
            <jsp:include page="/jsp/fragments/block_title.jsp">
              <jsp:param value="profile.block.profilePicture.modalTitle" name="titleLocale" />
            </jsp:include>

            <form action="${pageContext.request.contextPath}/profile/updatepicture.json" target="_uploadFrame" method="post" enctype="multipart/form-data">
              <jsp:include page="/jsp/fragments/formfield_file.jsp">
                <jsp:param name="name" value="imageData" />
                <jsp:param name="classes" value="required" />
                <jsp:param name="labelLocale" value="profile.block.profilePicture.pictureFileCaption" />
              </jsp:include>

              <jsp:include page="/jsp/fragments/formfield_submit.jsp">
                <jsp:param name="name" value="updateProfilePictureButton" />
                <jsp:param name="classes" value="formvalid" />
                <jsp:param name="labelLocale" value="profile.block.profilePicture.updatePictureButton" />
              </jsp:include>
            </form>
          </div>
        </div>
      </div>
    </div>

    <div id="profileSettingsForm">
      <form name="profileSettings">
        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="name" value="firstName" />
          <jsp:param name="classes" value="required" />
          <jsp:param name="labelLocale" value="profile.block.profileFirstNameLabel" />
          <jsp:param name="value" value="${user.firstName}" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="name" value="lastName" />
          <jsp:param name="classes" value="required" />
          <jsp:param name="labelLocale" value="profile.block.profileLastNameLabel" />
          <jsp:param name="value" value="${user.lastName}" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="name" value="nickname" />
          <jsp:param name="labelLocale" value="profile.block.profileNicknameLabel" />
          <jsp:param name="value" value="${user.nickname}" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_text.jsp">
          <jsp:param name="name" value="email" />
          <jsp:param name="classes" value="required email" />
          <jsp:param name="labelLocale" value="profile.block.profileEmailLabel" />
          <jsp:param name="value" value="${user.defaultEmail.address}" />
        </jsp:include>

        <input type="hidden" name="emailId" value="${user.defaultEmail.id}" /> <input id="profileUserIdElement" type="hidden" name="userId" value="${user.id}" />

        <jsp:include page="/jsp/fragments/formfield_checkbox.jsp">
          <jsp:param name="name" value="commentMail" />
          <jsp:param name="labelLocale" value="profile.block.profileCommentMailLabel" />
          <jsp:param name="checked" value="${userCommentMail}" />
          <jsp:param name="value=" value="1" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
          <jsp:param name="name" value="updateProfileButton" />
          <jsp:param name="classes" value="formvalid" />
          <jsp:param name="labelLocale" value="profile.block.updateProfileButtonLabel" />
        </jsp:include>
      </form>
    </div>
  </div>

  <!-- Internal password -->

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="profile.block.profilePasswordBlockTitle" name="titleLocale" />
  </jsp:include>

  <div id="profilePasswordBlockContent" class="blockContent">
    <div id="profilePasswordForm">
      <form name="profilePassword">
        <input type="hidden" name="passwordUserId" value="${user.id}" />

        <c:choose>
          <c:when test="${not userHasPassword}">
            <div id="noPasswordMessageContainer">
              <fmt:message key="profile.block.profileHasNoPasswordInformativeText" />
            </div>
            <div id="oldPasswordContainer" style="display: none;">
              <jsp:include page="/jsp/fragments/formfield_password.jsp">
                <jsp:param name="name" value="oldPassword" />
                <jsp:param name="classes" value="required" />
                <jsp:param name="labelLocale" value="profile.block.profileOldPasswordLabel" />
              </jsp:include>
            </div>
          </c:when>

          <c:otherwise>
            <div id="noPasswordMessageContainer" style="display: none;">
              <fmt:message key="profile.block.profileHasNoPasswordInformativeText" />
            </div>
            <div id="oldPasswordContainer">
              <jsp:include page="/jsp/fragments/formfield_password.jsp">
                <jsp:param name="name" value="oldPassword" />
                <jsp:param name="classes" value="required" />
                <jsp:param name="labelLocale" value="profile.block.profileOldPasswordLabel" />
              </jsp:include>
            </div>
          </c:otherwise>
        </c:choose>

        <jsp:include page="/jsp/fragments/formfield_password.jsp">
          <jsp:param name="name" value="newPassword1" />
          <jsp:param name="classes" value="required equals equals-newPassword2" />
          <jsp:param name="labelLocale" value="profile.block.profilePasswordLabel" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_password.jsp">
          <jsp:param name="name" value="newPassword2" />
          <jsp:param name="classes" value="required equals equals-newPassword1" />
          <jsp:param name="labelLocale" value="profile.block.profileRetypePasswordLabel" />
        </jsp:include>

        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
          <jsp:param name="name" value="updatePasswordButton" />
          <jsp:param name="classes" value="formvalid" />
          <jsp:param name="labelLocale" value="profile.block.updatePasswordButtonLabel" />
        </jsp:include>
      </form>
    </div>
  </div>

  <!-- External authentication providers -->

  <c:if test="${authCount gt credentialAuthCount}">
    <jsp:include page="/jsp/fragments/block_title.jsp">
      <jsp:param value="profile.block.profileExternalAuthenticationProvidersBlockTitle" name="titleLocale" />
    </jsp:include>

    <div id="profileExternalAuthBlockContent" class="blockContent">
      <c:forEach var="authSource" items="${authSources}" begin="${credentialAuthCount}">
        <c:set var="activeAuthSource" value="false" />
        <c:forEach var="userIdentification" items="${userIdentifications}">
          <c:if test="${userIdentification.authSource.id eq authSource.id}">
            <c:set var="activeAuthSource" value="true" />
          </c:if>
        </c:forEach>
        <c:choose>
          <c:when test="${activeAuthSource}">
            <div class="profileExternalAuthLoginTypeIcon loginType${authSource.strategy}">
              ${authSource.name} <span class="loginTypeInUseTextContainer"><fmt:message key="profile.block.loginType.activated" /></span>
            </div>
          </c:when>
          <c:otherwise>
            <div class="profileExternalAuthLoginTypeIcon loginType${authSource.strategy}">
              ${authSource.name} <a href="${pageContext.request.contextPath}/dologin.page?authSource=${authSource.id}"><fmt:message
                  key="profile.block.loginType.activate" /></a>
            </div>
          </c:otherwise>
        </c:choose>
      </c:forEach>
    </div>
  </c:if>

  <!-- Invitations -->

  <c:if test="${!empty(myInvitations)}">
    <jsp:include page="/jsp/fragments/block_title.jsp">
      <jsp:param value="profile.block.invitationsBlockTitle" name="titleLocale" />
    </jsp:include>
    
    <div id="profileInvitationBlockContent" class="blockContent">
      <c:forEach var="invitation" items="${myInvitations}">
        <div class="profileInvitationRowWrapper">
          <div>
            <div class="profileInvitationPanelName">
              ${invitation.panel.name}
            </div>
            <div class="profileInvitationDate">
              <fmt:formatDate pattern="d.M.yyyy" value="${invitation.created}"/>
            </div>
            <div class="profileInvitationUrl">
              <fmt:message key="profile.block.invitationLinkTemplate">
                <fmt:param>${invitation.panel.id}</fmt:param>
                <fmt:param>${invitation.hash}</fmt:param>
              </fmt:message>
            </div>
          </div>
          <div class="contextualLinks">
            <div class="blockContextualLink delete">
              <c:set var="tooltip">
                <fmt:message key="profile.block.invitationDeleteTooltip"/>
              </c:set>
              <a target="" onclick="" href="#invitationId:473" title="${tooltip}">
                <span class="blockContextualLinkTooltip">
                  <span class="blockContextualLinkTooltipText">${tooltip}</span>
                  <span class="blockContextualLinkTooltipArrow"></span>
                </span>
              </a>
            </div>
          </div>
        </div>
      </c:forEach>
    </div>
  </c:if>


</div>