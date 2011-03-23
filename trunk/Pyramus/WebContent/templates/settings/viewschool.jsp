<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title><fmt:message key="settings.viewSchool.pageTitle"/></title>

    <jsp:include page="/templates/generic/head_generic.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonrequest_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/jsonform_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/scriptaculous_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/tabs_support.jsp"></jsp:include>
    <jsp:include page="/templates/generic/table_support.jsp"></jsp:include>

	  <script type="text/javascript">
	    function onLoad(event) {
	      var tabControl = new IxProtoTabs($('tabs'));
        var variablesTable = new IxTable($('variablesTable'), {
          id : "variablesTable",
          columns : [{
            dataType : 'hidden',
            editable: false,
            paramName: 'key'
          },{
            left : 0,
            width: 150,
            dataType : 'text',
            editable: false,
            paramName: 'name'
          }, {
            left : 150,
            width : 750,
            dataType: 'text',
            editable: false,
            paramName: 'value'
          }]
        });
        var value;
        variablesTable.detachFromDom();
        <c:forEach var="variableKey" items="${variableKeys}">
          value = '${fn:escapeXml(school.variablesAsStringMap[variableKey.variableKey])}';
          var rowNumber = variablesTable.addRow([
            '${fn:escapeXml(variableKey.variableKey)}',
            '${fn:escapeXml(variableKey.variableName)}',
            value
          ]);

          var dataType;
          <c:choose>
			<c:when test="${variableKey.variableType == 'NUMBER'}">
			  dataType = 'number';
			</c:when>
			<c:when test="${variableKey.variableType == 'DATE'}">
			  dataType = 'date';
			</c:when>
			<c:when test="${variableKey.variableType == 'BOOLEAN'}">
			  dataType = 'checkbox';
			</c:when>
			<c:otherwise>
			  dataType = 'text';
			</c:otherwise>
          </c:choose>
          
          variablesTable.setCellDataType(rowNumber, 3, dataType);
        </c:forEach>
        variablesTable.reattachToDom();
      };
    </script>
    
  </head>
  <body onload="onLoad(event);">
    <jsp:include page="/templates/generic/header.jsp"></jsp:include>
  
    <h1 class="genericPageHeader"><fmt:message key="settings.viewSchool.pageTitle" /></h1>
  
    <div class="genericFormContainer"> 
      <div class="tabLabelsContainer" id="tabs">
        <a class="tabLabel" href="#basic">
          <fmt:message key="settings.viewSchool.tabLabelBasic"/>
        </a>
      </div>
      
      <input type="hidden" name="schoolId" value="${school.id}"></input>

      <div id="basic" class="tabContent">
        <div class="genericFormSection">
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="settings.viewSchool.codeTitle"/>
            <jsp:param name="helpLocale" value="settings.viewSchool.codeHelp"/>
          </jsp:include>
          ${fn:escapeXml(school.code)}
        </div>
        <div class="genericFormSection">
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="settings.viewSchool.nameTitle"/>
            <jsp:param name="helpLocale" value="settings.viewSchool.nameHelp"/>
          </jsp:include>
          ${fn:escapeXml(school.name)}
        </div>
        <div class="genericFormSection">  
          <jsp:include page="/templates/generic/fragments/formtitle.jsp">
            <jsp:param name="titleLocale" value="settings.viewSchool.variablesTitle"/>
            <jsp:param name="helpLocale" value="settings.viewSchool.variablesHelp"/>
          </jsp:include>
          <div id="variablesTable"></div>
        </div>
      </div>
    </div>
    
    <jsp:include page="/templates/generic/footer.jsp"></jsp:include>
  </body>
</html>