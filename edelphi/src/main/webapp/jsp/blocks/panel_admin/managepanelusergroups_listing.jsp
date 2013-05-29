<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="panelAdminUserGroupListBlock" class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.admin.managePanelUserGroups.usergroupListTitle" name="titleLocale" />
  </jsp:include>
  
  <c:if test="${activeStamp.id eq latestStamp.id}">
    <jsp:include page="/jsp/fragments/block_contextmenu.jsp">
      <jsp:param name="items" value="CREATEUSERGROUP"/>
      <jsp:param name="item.CREATEUSERGROUP.tooltipLocale" value="panel.admin.managePanelUserGroups.createUsergroupTooltip"/>
      <jsp:param name="item.CREATEUSERGROUP.href" value=""/>
    </jsp:include>
  </c:if>
  
  <div id="panelAdminManagePanelUsergroupListingBlockContent" class="blockContent">
  </div>  

</div>