<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.admin.managePanelBulletins.listTitle" name="titleLocale" />
  </jsp:include>

  <jsp:include page="/jsp/fragments/block_contextmenu.jsp">
    <jsp:param name="items" value="CREATE" />
    <jsp:param name="item.CREATE.tooltipLocale" value="panel.admin.managePanelBulletins.createTooltip" />
    <jsp:param name="item.CREATE.href" value="${pageContext.request.contextPath}/panel/admin/createpanelbulletin.page?panelId=${panel.id}" />
  </jsp:include>

  <div id="panelBulletinsBlockContent" class="blockContent">

    <c:choose>
      <c:when test="${panelActions['MANAGE_PANEL_BULLETINS']}">
        <c:set var="editAccess" value="true" />
      </c:when>
      <c:otherwise>
        <c:set var="editAccess" value="false" />
      </c:otherwise>
    </c:choose>

    <c:forEach var="bulletin" items="${bulletins}">
      <c:choose>
        <c:when test="${param.selected eq 'true'}">
          <c:set var="rowClasses">panelAdminBulletinRow panelAdminBulletinRowSelected</c:set>
        </c:when>
        <c:otherwise>
          <c:set var="rowClasses">panelAdminBulletinRow</c:set>
        </c:otherwise>
      </c:choose>

      <div class="${rowClasses}">
        <div class="panelAdminGenericTitle">
          <a href="${pageContext.request.contextPath}/panel/viewbulletin.page?panelId=${panel.id}&bulletinId=${bulletin.id}">${bulletin.title}</a>
        </div>
        <div class="panelAdminGenericDescription">
          ${bulletin.summary}
        </div>
        <div class="panelAdminGenericMeta">
          <fmt:message key="panel.admin.managePanelBulletins.bulletinCreatedModified">
            <fmt:param>
              <fmt:formatDate value="${bulletin.created}" />
            </fmt:param>
            <fmt:param>
              <fmt:formatDate value="${bulletin.lastModified}" />
            </fmt:param>
          </fmt:message>
        </div>
 
        <c:if test="${editAccess eq 'true'}">
          <div class="contextualLinks">
            <jsp:include page="/jsp/fragments/contextual_link.jsp">
              <jsp:param name="href" value="${pageContext.request.contextPath}/panel/admin/editpanelbulletin.page?panelId=${panel.id}&bulletinId=${bulletin.id}" />
              <jsp:param name="action" value="edit" />
              <jsp:param name="label" value="panel.admin.managePanelBulletins.bulletinEdit" />
            </jsp:include>
            
            <jsp:include page="/jsp/fragments/contextual_link.jsp">
              <jsp:param name="href" value="#bulletinId:${bulletin.id}" />
              <jsp:param name="action" value="delete" />
              <jsp:param name="label" value="panel.admin.managePanelBulletins.bulletinDelete" />
            </jsp:include>        
          </div>
        </c:if>
      </div>
    </c:forEach>
  </div>

</div>