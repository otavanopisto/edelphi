<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="block.bulletinsTitle" name="titleLocale"/>
  </jsp:include>
  

  <c:set var="editAccess" value="false"/>

  <div id="panelBulletinsBlockContent" class="blockContent">
    <c:choose>
      <c:when test="${actions['MANAGE_BULLETINS']}">
        <c:set var="editAccess" value="true"/>

        <jsp:include page="/jsp/fragments/block_contextmenu.jsp">
          <jsp:param name="items" value="CREATE" />
          <jsp:param name="item.CREATE.tooltipLocale" value="admin.managePanelBulletins.createTooltip" />
          <jsp:param name="item.CREATE.href" value="${pageContext.request.contextPath}/admin/createbulletin.page?lang=${dashboardLang}" />
        </jsp:include>

      </c:when>
      <c:otherwise>
        <c:set var="editAccess" value="false"/>
      </c:otherwise>
    </c:choose>
  
    <c:forEach items="${bulletins}" var="bulletin">
      <div class="panelBulletinRow">
        <div class="panelGenericTitle"><a href="${pageContext.request.contextPath}/viewbulletin.page?bulletinId=${bulletin.id}&lang=${dashboardLang}">${bulletin.title}</a></div>
        <div class="panelGenericDescription">${bulletin.summary}</div>
        <div class="panelGenericMeta">
        <fmt:message key="panel.block.bulletinsCreatedModified">
            <fmt:param>
              <fmt:formatDate value="${bulletin.created}" />
            </fmt:param>
            <fmt:param>
              <fmt:formatDate value="${bulletin.lastModified}" />
            </fmt:param>
          </fmt:message>
        </div>
        <div class="contextualLinks">
          <c:if test="${editAccess eq 'true'}">
            <jsp:include page="/jsp/fragments/contextual_link.jsp">
              <jsp:param name="onclick" value="" />
              <jsp:param name="href" value="${pageContext.request.contextPath}/admin/editbulletin.page?bulletinId=${bulletin.id}&lang=${dashboardLang}" />
              <jsp:param name="action" value="edit" />
              <jsp:param name="label" value="block.bulletinsContextualLinkTitle.editUpdate" />
            </jsp:include>
            
            <jsp:include page="/jsp/fragments/contextual_link.jsp">
              <jsp:param name="onclick" value="" />
              <jsp:param name="href" value="#bulletinId:${bulletin.id}" />
              <jsp:param name="action" value="delete" />
              <jsp:param name="label" value="block.bulletinsContextualLinkTitle.deleteUpdate" />
            </jsp:include>
          </c:if>
        </div>
      </div>  
    </c:forEach>
    
  </div>

</div>