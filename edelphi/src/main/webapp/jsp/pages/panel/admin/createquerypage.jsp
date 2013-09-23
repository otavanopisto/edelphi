<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="panelAdminQueryEditorCreatePage">
  
  <c:forEach var="queryPageTemplate" items="${queryPageTemplates}">

    <div class="panelAdminQueryEditorCreatePagePageTemplate">
      <div class="panelAdminQueryEditorCreatePagePageTemplateIcon ${queryPageTemplate.iconName}"></div>
      <div class="panelAdminQueryEditorCreatePagePageTemplateName">${queryPageTemplate.name}</div>
      <input type="hidden" value="${queryPageTemplate.id}" name="templateId"/>
      <input type="hidden" value="${queryPageTemplate.description}" name="templateDescription"/>
    </div>

  </c:forEach>  
  
</div>

<div class="panelAdminQueryEditorCreatePageGuide">
  <div class="panelAdminQueryEditorCreatePageStaticGuideContainer">
  
  </div>
  <div class="panelAdminQueryEditorCreatePageGuideContainer"><fmt:message key="panelAdmin.dialog.createQueryPage.pageIntro"/></div>
  <div class="panelAdminQueryEditorCreatePageTemplateGuideContainer">
    <p id="templateGuideContainerTitle"></p>
    <p id="templateGuideContainerDescription"></p>
  </div>
</div>