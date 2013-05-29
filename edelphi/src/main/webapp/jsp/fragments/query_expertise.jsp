<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryQuestionContainer queryExpertiseQuestionContainer">

  <div class="queryExpertiseMatrix">
    <div class="queryExpertiseMatrixHeaderRow">
      <div class="queryExpertiseMatrixCell"></div>
      <c:if test="${param['expertiseClasses.count'] gt 0}">
        <c:forEach begin="0" end="${param['expertiseClasses.count'] - 1}" varStatus="evs">
          <c:set var="expertiseClassName" value="expertiseClass.${evs.index}.name"/>
          <div class="queryExpertiseMatrixCell queryExpertiseMatrixHeaderCell">${param[expertiseClassName]}</div>
        </c:forEach>
      </c:if>
    </div>

    <c:if test="${param['intrestClasses.count'] gt 0}">
      <c:forEach begin="0" end="${param['intrestClasses.count'] - 1}" varStatus="ivs">
        <c:set var="intrestClassId" value="intrestClass.${ivs.index}.id"></c:set>
        <c:set var="intrestClassName" value="intrestClass.${ivs.index}.name"></c:set>
  
        <div class="queryExpertiseMatrixRow">
          <div class="queryExpertiseMatrixCell queryExpertiseMatrixHeaderCell">${param[intrestClassName]}</div>
          <c:if test="${param['expertiseClasses.count'] gt 0}">
            <c:forEach begin="0" end="${param['expertiseClasses.count'] - 1}" varStatus="evs">
              <c:set var="expertiseClassId" value="expertiseClass.${evs.index}.id"></c:set>
              <c:set var="selectedParam" value="expertiseClass.${param[expertiseClassId]}.${param[intrestClassId]}.selected"/>
              <c:set var="selected" value="${param[selectedParam]}"/>
              <c:choose>
                <c:when test="${selected eq '1'}">
                  <div class="queryExpertiseMatrixCell queryExpertiseMatrixAnswerCell queryExpertiseMatrixCellSelected">
                    <input type="hidden" value="1" name="expertise.${param[expertiseClassId]}.${param[intrestClassId]}"/> 
                  </div>
                </c:when>
                <c:otherwise>
                  <div class="queryExpertiseMatrixCell queryExpertiseMatrixAnswerCell">
                    <input type="hidden" value="0" name="expertise.${param[expertiseClassId]}.${param[intrestClassId]}"/> 
                  </div>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </c:if>
        </div>
      </c:forEach>
    </c:if>

  </div>

  <input type="hidden" name="expertiseCount" value="${param['expertiseClasses.count']}"/>
  <input type="hidden" name="interestCount" value="${param['intrestClasses.count']}"/>

</div>