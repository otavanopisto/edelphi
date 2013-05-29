<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="createPanel_pageContent">
  <div class="createPanel_pageContentTitle">
    <fmt:message key="createPanel.modal.createPanelPageTitle" />
  </div>

  <div class="createPanel_pageContentDescription">
    <fmt:message key="createPanel.modal.createPanelBasicInfoPageDescription" />
  </div>

  <div>
    <jsp:include page="/jsp/fragments/formfield_text.jsp">
      <jsp:param name="name" value="createPanel_panelName"/>
      <jsp:param name="classes" value="required"/>
      <jsp:param name="labelLocale" value="createPanel.modal.createPanelBasicInfoNameFieldCaption"/>
    </jsp:include>  
    
    <jsp:include page="/jsp/fragments/formfield_memo.jsp">
      <jsp:param name="name" value="createPanel_panelDescription"/>
      <jsp:param name="classes" value=""/>
      <jsp:param name="labelLocale" value="createPanel.modal.createPanelBasicInfoDescriptionFieldCaption"/>
    </jsp:include>  
  </div>
  
  <div class="createPanel_footer">
    <div class="createPanel_prevPageLink createPanel_footerNaviLink"><fmt:message key="createPanel.modal.createPanelBasicInfoPreviousPageTitle" /></div>
    <div class="createPanel_donePageLink createPanel_footerNaviLink"><fmt:message key="createPanel.modal.createPanelBasicInfoFinishPageTitle" /></div>
<!--     <div class="createPanel_nextPageLink">Seuraava</div> -->
  </div>
  
</div>