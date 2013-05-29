<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.admin.managePanelUsers.userListTitle" name="titleLocale" />
  </jsp:include>
  
  <div id="panelAdminManagePanelUserListingBlockContent" class="blockContent">
    
    <c:forEach var="role" items="${panelUserRoles}">
      <h3>${role.name}</h3>
      <div class="manageUsersList" id="manageUsersList_${role.id}"></div>
    </c:forEach>
  </div>  

</div>