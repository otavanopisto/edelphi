<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <div class="blockContent" id="queryReportOptions">
    <div class="blockContent">
    
      <div class="queryExpertiseFilter">
        <jsp:include page="/jsp/fragments/block_title.jsp">
          <jsp:param value="panel.admin.report.options.filterTitle" name="titleLocale"/>
        </jsp:include>

        <h3><fmt:message key="panel.admin.report.options.expertiseFilterTitle" /></h3>
        <table>
          <!-- Title row -->
          <tr>
            <td></td>

            <c:forEach var="expertise" items="${queryExpertiseFilterExpertises}">
              <td>${expertise.name}</td>
            </c:forEach>  
          </tr>

          <c:forEach var="interest" items="${queryExpertiseFilterInterests}">
            <tr>
              <td>${interest.name}</td>
            
              <c:forEach var="expertise" items="${queryExpertiseFilterExpertises}">
                <c:set var="groupId" value="${queryExpertiseFilterGroupMap[interest.id][expertise.id]}" />
                <c:set var="groupUserCount" value="${queryExpertiseFilterGroupUserCount[groupId]}" />
                <c:choose>
                  <c:when test="${queryExpertiseFilterSelected[groupId]}"><c:set var="groupClassName" value="queryExpertiseFilterGroupSelected" /></c:when>
                  <c:otherwise><c:set var="groupClassName" value="" /></c:otherwise>
                </c:choose>
              
                <td class="queryExpertiseFilterGroup ${groupClassName}">
                  ${groupUserCount}
                  <input type="hidden" name="groupId" value="${groupId}" />
                </td>
              </c:forEach>
            </tr>
          </c:forEach>
        </table>
      
        <h3><fmt:message key="panel.admin.report.options.backgroundInformationFilterTitle" /></h3>
      
        <form action="${pageContext.request.contextPath}${pageContext.request.pathInfo}" method="post">  
          <input type="hidden" name="panelId" value="${panelId}" />
          <input type="hidden" name="queryId" value="${queryId}" />
          <input type="hidden" name="pageId" value="${pageId}" />
          <input type="hidden" id="queryExpertiseFilterValue" name="queryExpertiseFilter" value="${queryExpertiseFilter}" />

          <c:forEach var="field" items="${queryFormFilterFields}">
            <c:choose>
              <c:when test="${field.fieldType eq 'LIST'}">
                <div class="queryFormFieldFilterOptionList">
                  <div class="queryFormFieldFilterOptionListCaption">${field.queryOptionField.caption}</div>
                  <div class="queryFormFieldFilterOptionListSelect">
                    <select name="ff:${field.queryOptionField.id}">
                      <option value=""></option>
                      <c:forEach var="option" items="${field.options}">
                        <c:choose>
                          <c:when test="${field.selectedOptions[option.id] eq true}">
                            <option value="${option.value}" selected="selected">${option.text}</option>
                          </c:when>
                          <c:otherwise>
                            <option value="${option.value}">${option.text}</option>
                          </c:otherwise>
                        </c:choose>
                      </c:forEach>
                    </select>
                  </div>
                </div>
              </c:when>
            </c:choose>
          </c:forEach>

          <jsp:include page="/jsp/fragments/formfield_submit.jsp">
            <jsp:param name="name" value="applyFilter"/>
            <jsp:param name="labelLocale" value="panel.admin.report.options.applyFilterButton"/>
          </jsp:include>
        </form>
      </div>
      
      
    </div>
  </div>

</div>

