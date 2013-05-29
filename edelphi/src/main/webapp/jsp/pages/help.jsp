<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><fmt:message key="index.pageTitle" /> </title>
    <jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
  </head>
  <body class="environment index">
  
    <jsp:include page="/jsp/templates/index_header.jsp">
      <jsp:param name="activeTrail" value="help"/>
    </jsp:include>
  
    <div class="GUI_pageWrapper">
	    
		  <div class="GUI_pageContainer">
		  
        <div id="GUI_indexHelpListingPanel" class="pagePanel">
				  <jsp:include page="/jsp/blocks/index/helpmaterial_listing.jsp"></jsp:include>
	      </div>

        <div id="GUI_indexHelpContentPanel" class="pagePanel">
          <c:if test="${not empty title}">
            <h2 class="documentPageTitle">${title}</h2>
          </c:if>
          
          <div class="documentContentContainer">${content}</div>
          
          <c:if test="${pageCount gt 1}">
            <div class="documentNavigationContainer">
              <c:choose>
                <c:when test="${type eq 'LOCAL_DOCUMENT'}">
                  <ed:include page="/jsp/fragments/contextual_link.jsp">
                    <ed:param name="href" value="?documentId=${document.id}&page=0" />
                    <ed:param name="action" value="documentFirstPage" />
                    <ed:param name="label" value="panel.block.document.firstPageLink" />
                    <ed:param name="disabled" value="${page > 0 ? 'false' : 'true'}"/>
                  </ed:include>
      
                  <ed:include page="/jsp/fragments/contextual_link.jsp">
                    <ed:param name="href" value="?documentId=${document.id}&page=${page - 1}" />
                    <ed:param name="action" value="documentPreviousPage" />
                    <ed:param name="label" value="panel.block.document.previousPageLink" />
                    <ed:param name="disabled" value="${page > 0 ? 'false' : 'true'}"/>
                  </ed:include>
      
                  <ed:include page="/jsp/fragments/contextual_link.jsp">
                    <ed:param name="href" value="?documentId=${document.id}&page=${page + 1}" />
                    <ed:param name="action" value="documentNextPage" />
                    <ed:param name="label" value="panel.block.document.nextPageLink" />
                    <ed:param name="disabled" value="${page < pageCount - 1 ? 'false' : 'true'}"/>
                  </ed:include>
      
                  <ed:include page="/jsp/fragments/contextual_link.jsp">
                    <ed:param name="href" value="?documentId=${document.id}&page=${pageCount - 1}" />
                    <ed:param name="action" value="documentLastPage" />
                    <ed:param name="label" value="panel.block.document.lastPageLink" />
                    <ed:param name="disabled" value="${page < pageCount - 1 ? 'false' : 'true'}"/>
                  </ed:include>
                </c:when>
              </c:choose>
            </div>
          </c:if>
        </div>
        
	      <div class="clearBoth"></div>
	    </div>
	    
    </div>
    
  </body>
</html>