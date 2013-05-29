<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="ACTION_URL">tupas_${param.bankId}_ACTION_URL</c:set>
<c:set var="A01Y_ACTION_ID">tupas_${param.bankId}_A01Y_ACTION_ID</c:set>
<c:set var="A01Y_VERS">tupas_${param.bankId}_A01Y_VERS</c:set>
<c:set var="A01Y_RCVID">tupas_${param.bankId}_A01Y_RCVID</c:set>
<c:set var="A01Y_LANGCODE">tupas_${param.bankId}_A01Y_LANGCODE</c:set>
<c:set var="A01Y_STAMP">tupas_${param.bankId}_A01Y_STAMP</c:set>
<c:set var="A01Y_IDTYPE">tupas_${param.bankId}_A01Y_IDTYPE</c:set>
<c:set var="A01Y_RETLINK">tupas_${param.bankId}_A01Y_RETLINK</c:set>
<c:set var="A01Y_CANLINK">tupas_${param.bankId}_A01Y_CANLINK</c:set>
<c:set var="A01Y_REJLINK">tupas_${param.bankId}_A01Y_REJLINK</c:set>
<c:set var="A01Y_KEYVERS">tupas_${param.bankId}_A01Y_KEYVERS</c:set>
<c:set var="A01Y_ALG">tupas_${param.bankId}_A01Y_ALG</c:set>
<c:set var="A01Y_MAC">tupas_${param.bankId}_A01Y_MAC</c:set>
  
<FORM METHOD="POST" ACTION="${requestScope[ACTION_URL]}">

  <INPUT NAME="A01Y_ACTION_ID" TYPE="hidden" VALUE="${requestScope[A01Y_ACTION_ID]}">
  <INPUT NAME="A01Y_VERS" TYPE="hidden" VALUE="${requestScope[A01Y_VERS]}">
  <INPUT NAME="A01Y_RCVID" TYPE="hidden" VALUE="${requestScope[A01Y_RCVID]}">
  <INPUT NAME="A01Y_LANGCODE" TYPE="hidden" VALUE="${requestScope[A01Y_LANGCODE]}">
  <INPUT NAME="A01Y_STAMP" TYPE="hidden" VALUE="${requestScope[A01Y_STAMP]}">
  <INPUT NAME="A01Y_IDTYPE" TYPE="hidden" VALUE="${requestScope[A01Y_IDTYPE]}">
  <INPUT NAME="A01Y_RETLINK" TYPE="hidden" VALUE="${requestScope[A01Y_RETLINK]}">
  <INPUT NAME="A01Y_CANLINK" TYPE="hidden" VALUE="${requestScope[A01Y_CANLINK]}">
  <INPUT NAME="A01Y_REJLINK" TYPE="hidden" VALUE="${requestScope[A01Y_REJLINK]}">
  <INPUT NAME="A01Y_KEYVERS" TYPE="hidden" VALUE="${requestScope[A01Y_KEYVERS]}">
  <INPUT NAME="A01Y_ALG" TYPE="hidden" VALUE="${requestScope[A01Y_ALG]}">
  <INPUT NAME="A01Y_MAC" TYPE="hidden" VALUE="${requestScope[A01Y_MAC]}">
  
  <input type="submit" value="${bankId}" class="tupasLoginButton" id="${param.bankId}-tupasLoginButton"/>
</FORM>