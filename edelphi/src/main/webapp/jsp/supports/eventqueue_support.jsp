<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
  <c:when test="${eventQueueSupportIncluded != true}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/_scripts/gui/eventqueue.js"></script>
    <script type="text/javascript">
      Event.observe(window, "load", function () {
        document.body.appendChild(window._globalEventQueue.domNode);
      });

      window._globalEventQueue = new EventQueue();
    
      function getGlobalEventQueue() {
        return window._globalEventQueue;
      }
      
      function addGlobalEventQueueInfo() {
        
      }
      
    </script>
    
    <c:set scope="request" var="eventQueueSupportIncluded" value="true"/>
  </c:when>
</c:choose>