<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>
      <fmt:message key="panels.admin.panelExperts.pageTitle">
        <fmt:param>${panel.name}</fmt:param>
      </fmt:message> 
    </title>
    <jsp:include page="/jsp/templates/panel_head.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/dragdrop_support.jsp"></jsp:include>
    <jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/panel_admin/panelexperts.js"></script>
  </head>
  <body class="panel_admin panelexperts">
  
    <c:set var="pageBreadcrumbTitle"><fmt:message key="breadcrumb.panelAdmin.panelExperts"/></c:set>

    <jsp:include page="/jsp/templates/panel_header.jsp">
      <jsp:param value="management" name="activeTrail"/>
      <jsp:param value="${pageBreadcrumbTitle}" name="breadcrumbPageTitle"/>
      <jsp:param value="${pageContext.request.contextPath}/panel/admin/panelexperts.page?panelId=${panel.id}" name="breadcrumbPageUrl"/>
    </jsp:include>

    <div class="GUI_pageWrapper">
      
      <div class="GUI_pageContainer">
      
        <jsp:include page="/jsp/fragments/page_title.jsp">
			    <jsp:param value="panels.admin.panelExperts.pageTitle" name="titleLocale"/>
          <jsp:param value="${panel.name}" name="titleLocaleParam"/>
			  </jsp:include>

        <div id="GUI_newInteressExpertiseWrapper" class="block">
        
		       <div class="blockTitle">
		         <h2><fmt:message key="panels.admin.panelExperts.definePanelsInterestsAndExpertisesTitle"></fmt:message></h2>
		       </div>
		       
		      <form name="newIntressForm">
            <div class="newIntressExpertisePanel">
  		        <jsp:include page="/jsp/fragments/formfield_text.jsp">
  		          <jsp:param name="name" value="newIntressName"/>
  		          <jsp:param name="classes" value="newIntressName required"/>
  		          <jsp:param name="id" value="newIntressName"/>
  		          <jsp:param name="value" value=""/>
  		          <jsp:param name="labelLocale" value="panels.admin.panelExperts.addIntressCaption"/>
  		        </jsp:include>
  		      
  		        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
  		        <jsp:param name="name" value="newIntressButton"/>
  		         <jsp:param name="classes" value="formvalid"/>
  		        <jsp:param name="labelLocale" value="panels.admin.panelExperts.addInteressButtonLabel"/>
  		        <jsp:param name="id" value="newIntressButton"/>
  		      </jsp:include>
  		      
  		      </div>
          </form>
		      
		      <form name="newExpertiseForm">
            <div class="newIntressExpertisePanel">
  		        <jsp:include page="/jsp/fragments/formfield_text.jsp">
  		          <jsp:param name="name" value="newExpertiseName"/>
  		          <jsp:param name="classes" value="newExpertiseName required"/>
  		          <jsp:param name="id" value="newExpertiseName"/>
  		          <jsp:param name="value" value=""/>
  		          <jsp:param name="labelLocale" value="panels.admin.panelExperts.addExpertiseCaption"/>
  		        </jsp:include>
  		       
  		        <jsp:include page="/jsp/fragments/formfield_submit.jsp">
  		          <jsp:param name="name" value="newExpertiseButton"/>
  		          <jsp:param name="classes" value="formvalid"/>
  		          <jsp:param name="labelLocale" value="panels.admin.panelExperts.addExpertiseButtonLabel"/>
  		          <jsp:param name="id" value="newExpertiseButton"/>
  		        </jsp:include>
  		      </div>
          </form>		       
		      
          <div class="clearBoth"></div>
        </div>

        <div id="GUI_panelExpertsUserMatrixWrapper">
          <div id="GUI_panelExpertsUsersWrapper" class="block">
            <div class="blockTitle">
              <h2><fmt:message key="panels.admin.panelExperts.userListingTitle"></fmt:message></h2>
            </div>
            <div id="GUI_panelExpertsUserList">
              <div id="panelExpertsUsersContainer"></div>
            </div>
          </div>

          <div id="GUI_panelExpertsMatrix" class="block">
            <div class="blockTitle">
              <h2><fmt:message key="panels.admin.panelExperts.expertiseMatrixTitle"></fmt:message></h2>
            </div>
            <div id="panelExpertsMatrixContainer"></div>
          </div>
          
          <div class="clearBoth"></div>
        </div>
		  </div>

	  </div>
  </body>
</html>