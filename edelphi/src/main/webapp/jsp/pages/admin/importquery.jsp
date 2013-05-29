<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><fmt:message key="admin.managePanelBulletins.pageTitle" /></title>
<jsp:include page="/jsp/templates/index_head.jsp"></jsp:include>
<jsp:include page="/jsp/supports/ckeditor_support.jsp"></jsp:include>
<jsp:include page="/jsp/supports/modalpopup_support.jsp"></jsp:include>
</head>
<body class="environment_admin index">

  <form id="panelAdminInvitationForm" method="post" enctype="multipart/form-data" autocomplete="off">
    
    <div>
      Target query id<br/>
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="queryId" />
      </jsp:include>
    </div>
    
    <div>
      Email mapping file<br />
      <jsp:include page="/jsp/fragments/formfield_file.jsp">
        <jsp:param name="name" value="emailMappingFile" />
      </jsp:include>
    </div>

    <div>
      Field mapping file<br />
      <jsp:include page="/jsp/fragments/formfield_file.jsp">
        <jsp:param name="name" value="fieldMappingFile" />
      </jsp:include>
    </div>

    <div>
      Comment mapping file<br />
      <jsp:include page="/jsp/fragments/formfield_file.jsp">
        <jsp:param name="name" value="commentMappingFile" />
      </jsp:include>
    </div>

    <div>
      Expertise matrix mapping file<br />
      <jsp:include page="/jsp/fragments/formfield_file.jsp">
        <jsp:param name="name" value="matrixMappingFile" />
      </jsp:include>
    </div>

    <div>
      Query data file<br />
      <jsp:include page="/jsp/fragments/formfield_file.jsp">
        <jsp:param name="name" value="queryDataFile" />
      </jsp:include>
    </div>

    <div>
      Date pattern<br/>
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="datePattern" />
        <jsp:param name="value" value="d.M.yyyy hh:mm:ss" />
      </jsp:include>
    </div>

    <div>
      Created parameter<br/>
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="createdParameter" />
        <jsp:param name="value" value="Luotu" />
      </jsp:include>
    </div>

    <div>
      Modified parameter<br/>
      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="modifiedParameter" />
        <jsp:param name="value" value="Muokattu" />
      </jsp:include>
    </div>

    <jsp:include page="/jsp/fragments/formfield_submit.jsp">
      <jsp:param name="name" value="submit" />
      <jsp:param name="labelText" value="Import" />
    </jsp:include>

  </form>

</body>
</html>