<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://edelphi.fi/_tags/edelfoi" prefix="ed"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="reportOptions">

  <!-- stamps -->
  
  <c:choose>
    <c:when test="${fn:length(stamps) gt 1}">
      <div class="queryStampsFilter">
        <h3>
          <fmt:message key="panel.admin.report.options.stampTitle" />
        </h3>
        <div class="queryOptionsListWrapper">
          <select name="stampId">
            <c:forEach var="stamp" items="${stamps}">
              <c:choose>
                <c:when test="${stamp.id eq stampId}">
                  <option value="${stamp.id}" selected="selected">${stamp.name}</option>
                </c:when>
                <c:otherwise>
                  <option value="${stamp.id}">${stamp.name}</option>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </select>
        </div>
      </div>
    </c:when>
    <c:otherwise>
      <input type="hidden" name="stampId" value="${stampId}" />
    </c:otherwise>
  </c:choose>

  <!-- pages -->
  <div class="queryPagesFilter">
    <h3>
      <fmt:message key="panel.admin.report.options.queryPageTitle" />
    </h3>
    <div class="queryOptionsListWrapper">
      <select name="queryPageId">
        <option value="0"><fmt:message key="panel.admin.report.options.allQueryPagesTitle" /></option>
        <c:forEach var="queryPage" items="${queryPages}">
          <option value="${queryPage.id}">${queryPage.title}</option>
        </c:forEach>
      </select>
    </div>
  </div>

  <!-- Interests and expertises -->

  <c:if test="${fn:length(queryExpertiseFilterExpertises) gt 0}">
    <div class="queryExpertiseFilter">
      <h3>
        <fmt:message key="panel.admin.report.options.expertiseFilterTitle" />
      </h3>
      <table>
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
                <c:when test="${queryExpertiseFilterSelected[groupId]}">
                  <c:set var="groupClassName" value="queryExpertiseFilterGroupSelected" />
                </c:when>
                <c:otherwise>
                  <c:set var="groupClassName" value="" />
                </c:otherwise>
              </c:choose>

              <td class="queryExpertiseFilterGroup ${groupClassName}">${groupUserCount}<input type="hidden" name="groupId" value="${groupId}" />
              </td>
            </c:forEach>
          </tr>
        </c:forEach>
      </table>
      <input type="hidden" name="queryExpertiseFilter" />
    </div>
  </c:if>

  <!-- Form fields -->

  <c:if test="${fn:length(queryFormFilterFields) gt 0}">
    <div class="queryBackgroundFilter">
      <h3>
        <fmt:message key="panel.admin.report.options.backgroundInformationFilterTitle" />
      </h3>
      <c:forEach var="field" items="${queryFormFilterFields}">
        <c:choose>
          <c:when test="${field.fieldType eq 'LIST'}">
            <div class="queryOptionsBackgroundListWrapper">
              <div class="queryOptionsListCaption">${field.queryOptionField.caption}</div>
              <div>
                <select name="ff:${field.queryOptionField.id}">
                  <option value=""></option>
                  <c:forEach var="option" items="${field.options}">
                    <option value="${option.value}">${option.text}</option>
                  </c:forEach>
                </select>
              </div>
            </div>
          </c:when>
        </c:choose>
      </c:forEach>
    </div>
  </c:if>

  <!-- User groups -->

  <c:if test="${fn:length(userGroups) gt 0}">
    <div class="queryUsergroupsFilter">
      <input type="hidden" name="userGroupsFilterEnabled" value="1" />
      <h3>
        <fmt:message key="panel.admin.report.options.userGroupFilterTitle" />
      </h3>
      <c:forEach var="userGroup" items="${userGroups}">
        <jsp:include page="/jsp/fragments/formfield_checkbox.jsp">
          <jsp:param name="name" value="userGroups" />
          <jsp:param name="labelText" value="${userGroup.name}" />
          <jsp:param name="value" value="${userGroup.id}" />
        </jsp:include>
      </c:forEach>
    </div>
  </c:if>

</div>