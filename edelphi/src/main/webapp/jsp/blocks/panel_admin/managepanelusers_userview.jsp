<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <div id="panelAdminManagePanelUserViewBlockContent" class="blockContent">
    <div id="manageUsersUserViewColumnContent" style="display: none;">
      <div class="blockTitle"><h2 id="manageUsersUserViewColumn_userName"></h2></div>
      
      <!-- 
      <div>
        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
          <jsp:param name="name" value="removeUser"/>
          <jsp:param name="classes" value=""/>
          <jsp:param name="labelLocale" value="panel.admin.managePanelUsers.removeUserFromPanelButtonCaption"/>
          <jsp:param name="id" value="manageUsersUserViewColumn_removeUserButton"/>
        </jsp:include>
      </div>
      -->
      
      <h3><fmt:message key="panel.admin.managePanelUsers.basicInfoSectionTitle" /></h3>
      
      <div id="manageUsersUserViewColumn_userImage"></div>
      
      <div class="manageUsersUserViewColumn_userInformation">
      
        <div class="manageUsersUserViewColumn_userInformation_firstNameWrapper">
         <jsp:include page="/jsp/fragments/formfield_text.jsp">
           <jsp:param name="name" value="panelName"/>
           <jsp:param name="classes" value="name"/>
           <jsp:param name="id" value="manageUsersUserViewColumn_userInformation_firstName"/>
           <jsp:param name="value" value=""/>
           <jsp:param name="labelLocale" value="panel.admin.managePanelUsers.firstNameCaption"/>
         </jsp:include>
	      </div>
        
        <div class="manageUsersUserViewColumn_userInformation_lastNameWrapper">
	        <jsp:include page="/jsp/fragments/formfield_text.jsp">
	          <jsp:param name="name" value="panelName"/>
	          <jsp:param name="classes" value="name"/>
	          <jsp:param name="id" value="manageUsersUserViewColumn_userInformation_lastName"/>
	          <jsp:param name="value" value=""/>
	          <jsp:param name="labelLocale" value="panel.admin.managePanelUsers.lastNameCaption"/>
	        </jsp:include>
	      </div>
	      
        <div class="manageUsersUserViewColumn_userInformation_emailWrapper">
          <div class="manageUsersUserViewColumn_userInformation_emailTitle"><fmt:message key="panel.admin.managePanelUsers.emailCaption" /></div>
          <div id="manageUsersUserViewColumn_userInformation_email"></div>
        </div>

        <div class="manageUsersUserViewColumn_userInformation_roleWrapper">
	        <div class="manageUsersUserViewColumn_userInformation_roleTitle"><fmt:message key="panel.admin.managePanelUsers.roleCaption" /></div>
	        <div id="manageUsersUserViewColumn_userInformation_role">
	          <select name="selectedUserRole">
	            <c:forEach var="role" items="${panelUserRoles}">
	              <option value="${role.id}">${role.name}</option>
	            </c:forEach>
	          </select>
	        </div>
        </div>
        
        <div class="manageUsersUserViewColumn_userInformation_authWrapper">
          <span class="manageUsersUserViewColumn_userInformation_authTitle"><fmt:message key="panel.admin.managePanelUsers.authenticationCaption" /></span>
          <span id="manageUsersUserViewColumn_userInformation_auth"> </span>
        </div>
        
      </div>
      
      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="name" value="removeUser"/>
         <jsp:param name="classes" value=""/>
        <jsp:param name="labelLocale" value="panel.admin.managePanelUsers.saveBasicInfoButtonCaption"/>
        <jsp:param name="id" value="manageUsersUserViewColumn_saveUserButton"/>
      </jsp:include>
      
      <!-- 
      <h3><fmt:message key="panel.admin.managePanelUsers.sendUserInformationSectionTitle" /></h3>
      
      <div>
        <jsp:include page="/jsp/fragments/formfield_memo.jsp">
          <jsp:param name="name" value="resetPassword"/>
          <jsp:param name="classes" value="resetPassword"/>
          <jsp:param name="value" value=""/>
        </jsp:include>
      </div>
      
      <div>
        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
          <jsp:param name="name" value="removeUser"/>
          <jsp:param name="classes" value=""/>
          <jsp:param name="labelLocale" value="panel.admin.managePanelUsers.sendUserInformationButtonCaption"/>
          <jsp:param name="id" value="manageUsersUserViewColumn_sendUserPassButton"/>
        </jsp:include>
      </div>
      -->
      
      <h3><fmt:message key="panel.admin.managePanelUsers.userExpertiseSectionTitle" /></h3>
      
      <div id="manageUsersUserViewColumn_panelExpertsMatrix"> </div>
    </div>
  </div>  

</div>