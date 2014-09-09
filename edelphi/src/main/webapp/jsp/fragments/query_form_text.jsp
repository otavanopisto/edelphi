<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div class="queryFormFieldContainer">
  
  <label for="${param.id}">${param.caption}</label> 
  <input type="text" name="${param.name}" value="${param.value}" id="${param.id}"/>
  
</div>