<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="panelAdminQueryEditorBlock" class="block">

  <div id="panelAdminQueryEditorBlockContent" class="blockContent">

    <div id="panelAdminQueryEditorForm">

      <div class="panelAdminQueryEditorTabs">
        <ul>
          <li>
            <a href="#panelAdminQueryEditorSettingsTab"><fmt:message key="panelAdmin.block.query.settingsTabLabel"/></a>
            <div id="panelAdminQueryEditorSettingsTabValidationError" style="display: none;"></div>
          </li>
          <li>
            <a href="#panelAdminQueryEditorPagesTab"><fmt:message key="panelAdmin.block.query.pagesTabLabel"/></a>
            <div id="panelAdminQueryEditorPagesTabValidationError" style="display: none;"></div>
          </li>
        </ul>
        <div id="panelAdminQueryEditorSettingsTab">
          <jsp:include page="/jsp/fragments/formfield_text.jsp">
            <jsp:param name="name" value="name" />
            <jsp:param name="value" value="${query.name}" />
            <jsp:param name="classes" value="required" />
          </jsp:include>
          
          <jsp:include page="/jsp/fragments/formfield_memo.jsp">
            <jsp:param name="name" value="description" />
            <jsp:param name="classes" value="queryDescription"/>
            <jsp:param name="value" value="${query.description}" />
          </jsp:include>

          <jsp:include page="/jsp/fragments/formfield_checkbox.jsp">
            <jsp:param name="name" value="allowEditReply" />
            <jsp:param name="labelLocale" value="panelAdmin.block.query.queryAllowEditReplyLabel" />
            <jsp:param name="checked" value="${query.allowEditReply ne false ? 'true' : 'false'}" />
          </jsp:include>
          
          <c:set var="stateEditText">
            <fmt:message key="panelAdmin.block.query.queryStateEdit"/>
          </c:set>
          
          <c:set var="stateActiveText">
            <fmt:message key="panelAdmin.block.query.queryStateActive"/>
          </c:set>
          
          <c:set var="stateClosedText">
            <fmt:message key="panelAdmin.block.query.queryStateClosed"/>
          </c:set>

          <jsp:include page="/jsp/fragments/formfield_radio.jsp">
	        <jsp:param name="name" value="state"/>
          <jsp:param name="labelLocale" value="panelAdmin.block.query.queryStateLabel" />
	        <jsp:param name="options" value="EDIT,ACTIVE,CLOSED"/>
	        <jsp:param name="option.EDIT" value="${stateEditText}"/>
	        <jsp:param name="option.ACTIVE" value="${stateActiveText}"/>
	        <jsp:param name="option.CLOSED" value="${stateClosedText}"/>
	        <jsp:param name="value" value="${empty(query.state) ? 'EDIT' : query.state}"/>
          </jsp:include>
        </div>

        <div id="panelAdminQueryEditorPagesTab">

          <div class="panelAdminQueryEditorPageListContainer">
            <div class="panelAdminQueryEditorPageActionsContainer">
              <a href="javascript:void(null);" id="panelAdminQueryEditorCreatePageLink"> 
                <fmt:message key="panelAdmin.block.query.createPageLink"/>
              </a>
              <a href="javascript:void(null);" id="panelAdminQueryEditorCreateSectionLink"> 
                <fmt:message key="panelAdmin.block.query.createSectionLink"/>
              </a>
            </div>

            <div class="panelAdminQueryEditorPagesContainer">
              <div class="panelAdminQueryEditorPages" id="panelAdminQueryEditorPages">
              </div>
            </div>
          </div>

          <div class="panelAdminQueryEditorEditorContainer">
          </div>
        </div>
      </div>

      <jsp:include page="/jsp/fragments/formfield_submit.jsp">
        <jsp:param name="labelLocale" value="panelAdmin.block.query.saveQuery" />
        <jsp:param name="classes" value="formvalid" />
        <jsp:param name="name" value="save" />
      </jsp:include>

      <input type="hidden" name="queryId" value="${param.queryId}" /> 
      <input type="hidden" name="parentFolderId" value="${param.parentFolderId}" /> 
    </div>
  </div>
</div>