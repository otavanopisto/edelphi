<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <div id="panelAdminManagePanelUsergroupViewBlockContent" class="blockContent">
    <div id="manageUsergroupsUsergroupViewColumnContent" style="display: block;">
      <div class="blockTitle"><h2 id="manageUsergroupsUsergroupViewColumn_usergroupName"><fmt:message key="panel.admin.managePanelUserGroups.editUsergroupPageTitle" /></h2></div>
      
        <form name="userGroupForm">
        
          <jsp:include page="/jsp/fragments/formfield_text.jsp">
            <jsp:param name="id" value="userGroupName"/>
            <jsp:param name="name" value="name" />
            <jsp:param name="classes" value="required" />
          </jsp:include>
        
          <div class="usergroupView_availableUsersListWrapper">
            <h3><fmt:message key="panel.admin.managePanelUserGroups.availablePanelists"/></h3>
            <jsp:include page="/jsp/fragments/formfield_text.jsp">
              <jsp:param name="id" value="filterAvailableUsers"/>
  		        <jsp:param name="name" value="searchAvailableUsers"/>
  		        <jsp:param name="classes" value="searchUsers"/>
  		      </jsp:include>
            <div id="usergroupView_availableUsersListContainer" class="usergroupView_availableUsersListContainer">
            </div>
          </div>
          
          <div class="usergroupViewArrowControllers">
            <div id="addUserGroupUser" class="usergroupViewArrowControllerRight"></div>
            <div id="removeUserGroupUser" class="usergroupViewArrowControllerLeft"></div>
          </div>
          
          <div class="usergroupView_usergroupUsersListWrapper">
            <h3><fmt:message key="panel.admin.managePanelUserGroups.userGroupMembers"/></h3>
            <jsp:include page="/jsp/fragments/formfield_text.jsp">
              <jsp:param name="id" value="filterGroupUsers"/>
              <jsp:param name="name" value="searchUsergroupsUsers"/>
              <jsp:param name="classes" value="searchUsers"/>
            </jsp:include>
            <div id="usergroupView_usergroupUsersListContainer" class="usergroupView_usergroupUsersListContainer">
            </div>
          </div>
          
          <div class="usergroupViewSaveButtonWrapper">
  	        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
  		        <jsp:param name="labelLocale" value="panelAdmin.block.query.saveQuery" />
  		        <jsp:param name="classes" value="formvalid" />
  		        <jsp:param name="name" value="save" />
  		      </jsp:include> 
  	      </div>
        </form>
      
    </div>
  </div>  

</div>