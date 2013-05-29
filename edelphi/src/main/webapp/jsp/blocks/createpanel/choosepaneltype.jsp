<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="createPanel_pageContent">
  <div class="createPanel_pageContentTitle">
    <fmt:message key="createPanel.modal.createPanelPageTitle" />
  </div>

  <div class="createPanel_pageContentDescription">
    <fmt:message key="createPanel.modal.createPanelChoosePanelTypePageDescription" />
  </div>

  <div class="createPanel_panelTypesContainer">
    <c:forEach items="${panelSettingsTemplates}" var="template">
      <div class="createPanel_panelType">
        <div class="createPanel_panelTypeImage"> </div>
        <div class="createPanel_panelTypeName">${template.name}</div>
        <div class="createPanel_panelTypeDescription">${template.description}</div>
        <input type="hidden" name="panelTypeId" value="${template.id}" />
      </div>
    </c:forEach>
  </div>
</div>