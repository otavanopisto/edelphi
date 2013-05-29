<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="admin.dashboard.panelUsersTitle" name="titleLocale"/>
  </jsp:include>

  <div id="panelAdminDashboardUsersBlockContent" class="blockContent">
  
    <div class="panelAdminUserRow">
      <div class="panelAdminGenericTitle"><a href="inviteusers.page"><fmt:message key="admin.dashboard.usersInviteAction" /></a></div>
      <div class="panelAdminGenericDescription"><fmt:message key="admin.dashboard.usersInviteDescription" /></div>
    </div>
    
    <div class="panelAdminUserRow">
      <div class="panelAdminGenericTitle"><a href="managepanelusers.page"><fmt:message key="admin.dashboard.usersManageUsersAction" /></a></div>
      <div class="panelAdminGenericDescription"><fmt:message key="admin.dashboard.usersManageUsersDescription" /></div>
    </div>
    
  </div>

</div>