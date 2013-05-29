<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryQuestionContainer queryTimelineQuestionContainer">
  
  <div>
    <label class="queryTimelineMin">${param.min}</label>
    <label class="queryTimelineMax">${param.max}</label>
  </div>
  
  <div>
    <label class="queryTimelineValue1Label">${param.value1Label}</label>
  </div>
  
  <div class="queryTimelineTrack"></div>

  <c:if test="${param.type eq '1'}">
    <div>
      <label class="queryTimelineValue2Label">${param.value2Label}</label>
    </div>
  </c:if>

  <input type="hidden" name="type" value="${param.type}"/>
  <input type="hidden" name="value1" value="${param.value1}"/>
  <c:if test="${param.type eq '1'}">
    <input type="hidden" name="value2" value="${param.value2}"/>
  </c:if>
  
</div>