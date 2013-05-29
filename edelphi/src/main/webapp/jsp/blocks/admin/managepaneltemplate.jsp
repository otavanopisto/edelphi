<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="block">

  <jsp:include page="/jsp/fragments/block_title.jsp">
    <jsp:param name="titleLocale" value="managePanelTemplate.block.manageActionsBlockTitle"/>
    <jsp:param name="helpText" value=""/>
  </jsp:include>
  
  <div class="blockContent">
    <form action="${param.jsonHandler}" method="post">
      <c:if test="${!empty(panelId)}">
        <input type="hidden" name="panelId" value="${panelId}"/>
      </c:if>  

      <input type="hidden" name="templateId" value="${template.id}"/>

      <jsp:include page="/jsp/fragments/formfield_text.jsp">
        <jsp:param name="name" value="templateName"/>
        <jsp:param name="value" value="${template.name}"/>
        <jsp:param name="labelLocale" value="managePanelTemplate.block.templateName"/>
      </jsp:include>  

      <jsp:include page="/jsp/fragments/formfield_memo.jsp">
        <jsp:param name="name" value="templateDesc"/>
        <jsp:param name="value" value="${template.description}"/>
        <jsp:param name="labelLocale" value="managePanelTemplate.block.templateDescription"/>
      </jsp:include>  

      <table class="manageactionsTable">
        <tr class="manageactionsTableRow manageactionsTableHeaderRow">
          <td class="manageActionsTableActionNameCell"> </td>
          
          <c:forEach var="role" items="${roleList}">
            <td class="manageActionsTableRoleCell">${role.name}</td>
          </c:forEach>
        </tr>
        
        <c:forEach var="action" items="${actionList}">
          <tr class="manageactionsTableRow">
            <td class="manageActionsTableActionNameCell">${action.actionName}</td>
            
            <c:forEach var="role" items="${roleList}">
              <td class="manageActionsTableRoleCell">
                <c:choose>
                  <c:when test="${actionStatus[role.id][action.id]}">
                    <input name="delfoiActionRole.${role.id}.${action.id}" type="checkbox" value="1" checked="checked"/>
                  </c:when>
                  
                  <c:otherwise>
                    <input name="delfoiActionRole.${role.id}.${action.id}" type="checkbox" value="1"/>
                  </c:otherwise>
                </c:choose>
              </td>
            </c:forEach>
          </tr>
        </c:forEach>      
      </table>
  
      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="name" value="saveButton"/>
        <jsp:param name="labelLocale" value="managePanelTemplate.block.saveButton"/>
      </jsp:include>
    </form>
  </div>
</div>