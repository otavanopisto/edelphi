<%@page import="java.util.Iterator"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%
  @SuppressWarnings("unchecked")
  Map<String, String> jsData = (Map<String, String>) request.getAttribute("jsData");
  if (jsData != null) {
    out.append("<script type=\"text/javascript\">");
    out.append("var JSDATA={");
    
    Iterator<String> keys = jsData.keySet().iterator();
    while (keys.hasNext()) {
      String key = keys.next();
      String value = jsData.get(key);
      out.append("'");
      out.append(StringEscapeUtils.escapeJavaScript(key));
      out.append("':'");
      out.append(StringEscapeUtils.escapeJavaScript(value));
      out.append("'");
      if (keys.hasNext())
        out.append(',');
    }
    
    out.append("};</script>");
  }
%>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/prototype/prototype.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/uiutils.js"></script>
<script type="text/javascript">
  function setLocale(locale) {
    var date = new Date();
    date.setTime(date.getTime() + (3650*24*60*60*1000));
    var expires = "; expires=" + date.toGMTString();
    document.cookie = "eDelphiLocale=" + locale + expires + "; path=/";
    window.location.reload();
  }
</script>
<jsp:include page="/jsp/supports/jsonutils_support.jsp"></jsp:include>
<jsp:include page="/jsp/supports/validation_support.jsp"></jsp:include>
<jsp:include page="/jsp/supports/blockcontroller_support.jsp"></jsp:include>
<jsp:include page="/jsp/supports/eventqueue_support.jsp"></jsp:include>
<link href="${pageContext.request.contextPath}/_themes/${theme}/css/theme.css" rel="stylesheet"/>

<c:if test="${!empty(messages) && param.skipErrorProcessing ne 'true'}">
  <script type="text/javascript">
    document.observe("dom:loaded", function() {
      var eventQueue = getGlobalEventQueue();

      <c:forEach var="message" items="${messages}">
        <c:choose>
          <c:when test="${message.severity eq 'OK'}">
            eventQueue.addItem(new EventQueueItem("${fn:escapeXml(message.message)}", {
              className: "eventQueueSuccessItem",
              timeout: 1000 * 2
            }));
          </c:when>
          <c:when test="${message.severity eq 'INFORMATION'}">
            eventQueue.addItem(new EventQueueItem("${fn:escapeXml(message.message)}", {
              className: "eventQueueInfoItem",
              timeout: 1000 * 30
            }));
          </c:when>
          <c:when test="${message.severity eq 'WARNING'}">
            eventQueue.addItem(new EventQueueItem("${fn:escapeXml(message.message)}", {
              className: "eventQueueWarningItem",
              timeout: -1
            }));    
          </c:when>
          <c:when test="${message.severity eq 'ERROR'}">
            eventQueue.addItem(new EventQueueItem("${fn:escapeXml(message.message)}", {
              className: "eventQueueErrorItem",
              timeout: -1
            }));
          </c:when>
          <c:when test="${message.severity eq 'CRITICAL'}">
            eventQueue.addItem(new EventQueueItem("${fn:escapeXml(message.message)}", {
              className: "eventQueueCriticalItem",
              timeout: -1
            }));
          </c:when>
        </c:choose>
      </c:forEach>
    });
  </script>
  
</c:if>

<script type="text/javascript">
  var CONTEXTPATH = "${pageContext.request.contextPath}";
  var THEMEPATH = "${pageContext.request.contextPath}/_themes/${theme}/";
  
  function redirectTo(url) {
    if (url.indexOf("#") > 0) {
      // Url contains an anchor
      var splittedOld = window.location.href.split('#');
      var splittedNew = url.split('#');

      var oldHref = splittedOld[0];
      var newHref = splittedNew[0];
      
      if (oldHref === newHref) {
        var oldAnchor = splittedOld[1];
        var newAnchor = splittedNew[1];
        // We are tring to redirect to same url where we came from 
        if (newAnchor === oldAnchor) {
          // And same anchor that we came from
          location.reload();
        } else {
          location.hash = '#' + newAnchor;
          location.reload();
        }
      } else {
        // Url does not point to same url that where we came from so we just redirect it
        location.assign(url);
      }
    } else {
      // Url does not contain anchor reference, so we just do the redirect 
      location.assign(url);
    }
  }
</script>