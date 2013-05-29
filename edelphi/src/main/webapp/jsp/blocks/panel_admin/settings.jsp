<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="panelAdminSettingsBlockContent" class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param value="panel.admin.dashboard.panelSettings.title" name="titleLocale"/>
  </jsp:include>

  <div id="adminDashboardSettingsBlockContent" class="blockContent">
    <form id="panelAdminSettingsForm" action="${pageContext.request.contextPath}/panel/admin/savesettings.json">
      <input type="hidden" name="panelId" value="${panel.id}"/>
    
      <!-- <h3><fmt:message key="panel.admin.dashboard.panelSettings.name"></fmt:message></h3> -->
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="panelName"/>
        <jsp:param name="classes" value="name"/>
        <jsp:param name="value" value="${panel.name}"/>
      </jsp:include>
      
      <!-- <h3><fmt:message key="panel.admin.dashboard.panelSettings.description"></fmt:message></h3> -->
      <jsp:include page="/jsp/fragments/formfield_memo.jsp">
        <jsp:param name="name" value="panelDescription"/>
        <jsp:param name="classes" value="panelDescription"/>
        <jsp:param name="value" value="${panel.description}"/>
      </jsp:include>
      
      <c:set var="openLocale"><fmt:message key="panel.admin.dashboard.panelSettings.open"/></c:set>
      <c:set var="closedLocale"><fmt:message key="panel.admin.dashboard.panelSettings.closed"/></c:set>
      <h3><fmt:message key="panel.admin.dashboard.panelSettings.type"></fmt:message></h3>
      <jsp:include page="/jsp/fragments/formfield_radio.jsp">
        <jsp:param name="name" value="panelAccess"/>
        <jsp:param name="options" value="OPEN,CLOSED"/>
        <jsp:param name="option.OPEN" value="${openLocale}"/>
        <jsp:param name="option.CLOSED" value="${closedLocale}"/>
        <jsp:param name="value" value="${panel.accessLevel}"/>
      </jsp:include>

      <c:set var="designLocale"><fmt:message key="panel.admin.dashboard.panelSettings.design"/></c:set>
      <c:set var="inProgressLocale"><fmt:message key="panel.admin.dashboard.panelSettings.inProgress"/></c:set>
      <c:set var="endedLocale"><fmt:message key="panel.admin.dashboard.panelSettings.ended"/></c:set>
      <h3><fmt:message key="panel.admin.dashboard.panelSettings.state"></fmt:message></h3>
      <jsp:include page="/jsp/fragments/formfield_radio.jsp">
        <jsp:param name="name" value="panelState"/>
        <jsp:param name="options" value="DESIGN,IN_PROGRESS,ENDED"/>
        <jsp:param name="option.DESIGN" value="${designLocale}"/>
        <jsp:param name="option.IN_PROGRESS" value="${inProgressLocale}"/>
        <jsp:param name="option.ENDED" value="${endedLocale}"/>
        <jsp:param name="value" value="${panel.state}"/>
      </jsp:include>
      
      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="name" value="save"/>
        <jsp:param name="classes" value="formvalid"/>
        <jsp:param name="labelLocale" value="panel.admin.dashboard.panelSettings.save"/>
      </jsp:include>
    
    </form>
  
  </div>

</div>