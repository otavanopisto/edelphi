<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryBlock" id="panelQueryBlock">
  <form action="${pageContext.request.contextPath}/queries/saveanswers.json">
    <c:set var="titleText" scope="page">
      <fmt:message key="panel.block.query.title">
        <fmt:param>${queryPage.querySection.query.name}</fmt:param>
      </fmt:message>
    </c:set>
    
    <jsp:include page="/jsp/fragments/block_title.jsp">
      <jsp:param name="titleText" value="${titleText}"/>
    </jsp:include>
    
    <div id="panelQueryBlockContent" class="blockContent">
      <h2 class="queryPageTitle">${queryPage.title}</h2>

      <c:forEach var="requiredQueryFragment" items="${requiredQueryFragments}">
        <ed:include page="/jsp/fragments/query_${requiredQueryFragment.name}.jsp">
          <c:forEach items="${requiredQueryFragment.attributes}" var="attribute">
            <ed:param name="${attribute.key}" value="${attribute.value}"/>
          </c:forEach>
        </ed:include>
      </c:forEach>
    </div>
    
    <input type="hidden" name="panelPath" value="${panel.fullPath}"/>
    <input type="hidden" name="queryPageId" value="${queryPage.id}"/>
    <input type="hidden" name="queryReplyId" value="${queryReply.id}"/>
    <input type="hidden" name="queryPageType" value="${queryPage.pageType}"/>
    <input type="hidden" name="queryNextPageNumber" value="${queryNextPageNumber}"/>
    <input type="hidden" name="queryPreviousPageNumber" value="${queryPreviousPageNumber}"/>
    
    <div class="clearBoth"></div>

    <div class="queryQuestionSubmitContainer">

      <ed:include page="/jsp/fragments/formfield_submit.jsp">
        <ed:param name="labelLocale" value="panel.block.query.previous"/>
        <ed:param name="titleLocale" value="panel.block.query.previousTooltip"/>
        <ed:param name="classes" value="formvalid"/>
        <ed:param name="name" value="previous"/>
        <c:choose>
          <c:when test="${queryPreviousPageNumber eq null}">
            <ed:param name="disabled" value="true"/>
          </c:when>
        </c:choose>
      </ed:include>
      
      <div class="queryCurrentPageIndicator">
        <fmt:message key="panel.block.query.page">
          <fmt:param>${currentVisiblePageNumber}</fmt:param>
          <fmt:param>${queryPageCount}</fmt:param>
        </fmt:message>
        
        <div class="queryPageChangeQuickNavigationWrapper">
          <div class="queryPageChangeQuickNavigation">
            <div class="queryPageChangeQuickNavigationTitle"><fmt:message key="panel.block.query.quickNavigationTitle"></fmt:message></div>
          <c:forEach var="queryPage" items="${queryPages}">
            <div class="queryQuickNavigationPageLink"><a href="?page=${queryPage.pageNumber}"><span class="queryQuickNavigationPageNumber">${queryPage.uiPageNumber}.</span><span class="queryQuickNavigationPageTitle">${queryPage.title}</span></a></div>
          </c:forEach>
        
          </div>
          <div class="queryPageChangeQuickNavigationArrow"></div>
        </div>
      </div>
  
      <c:choose>
        <c:when test="${queryNextPageNumber eq null}">
          <ed:include page="/jsp/fragments/formfield_submit.jsp">
            <ed:param name="labelLocale" value="panel.block.query.finish"/>
            <ed:param name="titleLocale" value="panel.block.query.finishTooltip"/>
            <ed:param name="classes" value="formvalid"/>
            <ed:param name="name" value="finish"/>
          </ed:include>
          
          <div class="querySkipQuestionContainer">
            
            <ed:include page="/jsp/fragments/formfield_submit.jsp">
              <ed:param name="labelLocale" value="panel.block.query.skipLast"/>
              <ed:param name="titleLocale" value="panel.block.query.skipLastTooltip"/>
              <ed:param name="classes" value="formvalid"/>
              <ed:param name="name" value="skipLast"/>
            </ed:include>
          
          </div>
        </c:when>
        <c:otherwise>
          <ed:include page="/jsp/fragments/formfield_submit.jsp">
            <ed:param name="labelLocale" value="panel.block.query.next"/>
            <ed:param name="titleLocale" value="panel.block.query.nextTooltip" />
            <ed:param name="classes" value="formvalid"/>
            <ed:param name="name" value="next"/>
          </ed:include>
          
          <div class="querySkipQuestionContainer">
            <ed:include page="/jsp/fragments/formfield_submit.jsp">
              <ed:param name="labelLocale" value="panel.block.query.skip"/>
              <ed:param name="titleLocale" value="panel.block.query.skipTooltip"/>
              <ed:param name="classes" value="formvalid"/>
              <ed:param name="name" value="skip"/>
            </ed:include>
          </div>
        </c:otherwise>
      </c:choose>
    
    </div>
    
  </form>
</div>