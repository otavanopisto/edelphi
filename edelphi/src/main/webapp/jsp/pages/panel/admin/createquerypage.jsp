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
      
      <span class="panelAdminQueryEditorCreatePagePageTemplateTooltip">
        <span class="panelAdminQueryEditorCreatePagePageTemplateTooltipText">${queryPageTemplate.description}</span>
        <span class="panelAdminQueryEditorCreatePagePageTemplateTooltipArrow"></span>
      </span>
    </div>

  </c:forEach>  
  
</div>

<div class="panelAdminQueryEditorCreatePageGuide">
  <div class="panelAdminQueryEditorCreatePageStaticGuideContainer">
  
  </div>
  <div class="panelAdminQueryEditorCreatePageGuideContainer">Placeholder for overall guide text for this modal window. Area can be used for longer text if ya please! orem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Duis aute irure dolor in reprehenderit in voluptate velit esse.</div>
  <div class="panelAdminQueryEditorCreatePageTemplateGuideContainer">
    <p>Placeholder title</p>
    <p>Placeholder guide text for each page which is visible when user hovers cursor over item. You can add more text here if ya please you scallywag you! Start adding lines allready! Customers are waiting! Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
  </div>
</div>